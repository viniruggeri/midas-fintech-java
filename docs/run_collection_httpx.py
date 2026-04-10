#!/usr/bin/env python3
"""Executa uma collection Postman (subset) usando httpx para smoke/e2e local.

Uso:
  python docs/run_collection_httpx.py \
    --collection docs/midas-api-collection.json \
    --base-url http://localhost:8080 \
        --username midas-admin-local \
        --password MidasLocal@Admin2026 \
        --run-negative \
        --report-json docs/collection-report.json \
        --report-md docs/collection-report.md
"""

from __future__ import annotations

import argparse
from datetime import datetime, timezone
import json
import re
from dataclasses import dataclass
from pathlib import Path
from typing import Any

import httpx


VAR_PATTERN = re.compile(r"\{\{\s*([a-zA-Z0-9_\-]+)\s*\}\}")
STATUS_PATTERN = re.compile(r"have\.status\((\d{3})\)")


@dataclass
class RequestCase:
    name: str
    request: dict[str, Any]
    expected_status: int | None


@dataclass
class CaseExecution:
    name: str
    method: str
    url: str
    status_code: int
    expected_status: str
    ok: bool
    body_snippet: str = ""


def substitute(value: Any, variables: dict[str, str]) -> Any:
    if isinstance(value, str):
        def repl(match: re.Match[str]) -> str:
            key = match.group(1)
            return str(variables.get(key, match.group(0)))

        return VAR_PATTERN.sub(repl, value)

    if isinstance(value, list):
        return [substitute(item, variables) for item in value]

    if isinstance(value, dict):
        return {k: substitute(v, variables) for k, v in value.items()}

    return value


def flatten_items(items: list[dict[str, Any]]) -> list[RequestCase]:
    cases: list[RequestCase] = []

    for item in items:
        if "item" in item:
            cases.extend(flatten_items(item["item"]))
            continue

        req = item.get("request")
        if not req:
            continue

        expected_status = None
        for event in item.get("event", []):
            if event.get("listen") != "test":
                continue
            script = event.get("script", {})
            for line in script.get("exec", []):
                m = STATUS_PATTERN.search(line)
                if m:
                    expected_status = int(m.group(1))
                    break
            if expected_status is not None:
                break

        cases.append(RequestCase(name=item.get("name", "(sem nome)"), request=req, expected_status=expected_status))

    return cases


def reorder_cases(cases: list[RequestCase]) -> list[RequestCase]:
    """Mantem ordem natural, mas adia requests destrutivas para o final.

    A collection atual tem cenarios que ainda referenciam recursos apos um DELETE.
    """

    def priority(case: RequestCase) -> int:
        method = case.request.get("method", "GET").upper()
        if method != "DELETE":
            return 0
        # Deletar conta sempre por ultimo para nao invalidar testes por accountId.
        if case.name == "Delete Account":
            return 2
        return 1

    return sorted(cases, key=priority)


def resolve_collection_vars(collection: dict[str, Any], args: argparse.Namespace) -> dict[str, str]:
    variables = {
        var.get("key", ""): str(var.get("value", ""))
        for var in collection.get("variable", [])
        if var.get("key")
    }

    if args.base_url:
        variables["baseUrl"] = args.base_url
    if args.username:
        variables["username"] = args.username
    if args.password:
        variables["password"] = args.password

    return variables


def apply_response_variables(case_name: str, response_json: dict[str, Any], variables: dict[str, str]) -> None:
    # Mapeamento direto dos testes da collection atual.
    if case_name in {"Emitir Token", "Refresh Token"}:
        token = response_json.get("token")
        refresh = response_json.get("refreshToken")
        if token:
            variables["accessToken"] = str(token)
        if refresh:
            variables["refreshToken"] = str(refresh)

    if case_name == "Create Account":
        account_id = response_json.get("id")
        if account_id is not None:
            variables["accountId"] = str(account_id)

    if case_name == "Create Transaction - Receita":
        tx_id = response_json.get("id")
        if tx_id is not None:
            variables["transactionId1"] = str(tx_id)

    if case_name == "Create Transaction - Despesa":
        tx_id = response_json.get("id")
        if tx_id is not None:
            variables["transactionId2"] = str(tx_id)


def build_headers(req: dict[str, Any], variables: dict[str, str], collection_auth: dict[str, Any]) -> dict[str, str]:
    headers: dict[str, str] = {}

    for h in req.get("header", []):
        key = h.get("key")
        value = h.get("value")
        if key and value is not None:
            headers[substitute(key, variables)] = substitute(value, variables)

    req_auth_type = req.get("auth", {}).get("type")
    if req_auth_type != "noauth":
        if collection_auth.get("type") == "bearer" and variables.get("accessToken"):
            headers.setdefault("Authorization", f"Bearer {variables['accessToken']}")

    return headers


def build_url(req: dict[str, Any], variables: dict[str, str]) -> str:
    url_raw = req.get("url", {}).get("raw")
    if not url_raw:
        raise ValueError("Request sem url.raw na collection")
    return substitute(url_raw, variables)


def build_body(req: dict[str, Any], variables: dict[str, str]) -> str | None:
    body = req.get("body")
    if not body:
        return None

    mode = body.get("mode")
    if mode == "raw":
        return substitute(body.get("raw", ""), variables)

    return None


def execute_case(
    client: httpx.Client,
    case_name: str,
    method: str,
    url: str,
    headers: dict[str, str],
    body: str | None,
    expected: int | None,
) -> tuple[CaseExecution, dict[str, Any] | None]:
    response = client.request(method, url, headers=headers, content=body)

    if expected is None:
        ok = 200 <= response.status_code < 300
        expected_text = "2xx"
    else:
        ok = response.status_code == expected
        expected_text = str(expected)

    payload = None
    if ok:
        try:
            raw = response.json()
            if isinstance(raw, dict):
                payload = raw
        except json.JSONDecodeError:
            payload = None

    snippet = ""
    if not ok:
        snippet = response.text[:300].replace("\n", " ")

    execution = CaseExecution(
        name=case_name,
        method=method,
        url=url,
        status_code=response.status_code,
        expected_status=expected_text,
        ok=ok,
        body_snippet=snippet,
    )
    return execution, payload


def print_case(index: int, execution: CaseExecution) -> None:
    status_text = "PASS" if execution.ok else "FAIL"
    print(
        f"[{index:02d}] {status_text} {execution.name} -> "
        f"{execution.method} {execution.url} [{execution.status_code}] "
        f"expected {execution.expected_status}"
    )
    if execution.body_snippet:
        print(f"     body: {execution.body_snippet}")


def run_negative_checks(client: httpx.Client, variables: dict[str, str]) -> list[tuple[str, str, str, dict[str, str], str | None, int]]:
    base_url = variables.get("baseUrl", "http://localhost:8080")
    invalid_payload = json.dumps({
        "username": variables.get("username", "midas-admin-local") + "-invalid",
        "password": "invalid-password",
    })

    return [
        (
            "NEG - Invalid credentials",
            "POST",
            f"{base_url}/api/auth/token",
            {"Content-Type": "application/json"},
            invalid_payload,
            400,
        ),
        (
            "NEG - Protected endpoint without token",
            "GET",
            f"{base_url}/api/accounts",
            {},
            None,
            401,
        ),
        (
            "NEG - Protected endpoint with invalid token",
            "GET",
            f"{base_url}/api/accounts",
            {"Authorization": "Bearer invalid.token.value"},
            None,
            401,
        ),
    ]


def write_reports(report: dict[str, Any], args: argparse.Namespace) -> None:
    if args.report_json:
        json_path = Path(args.report_json)
        json_path.parent.mkdir(parents=True, exist_ok=True)
        json_path.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
        print(f"Relatório JSON: {json_path}")

    if args.report_md:
        md_path = Path(args.report_md)
        md_path.parent.mkdir(parents=True, exist_ok=True)

        lines = [
            "# API Collection Test Report",
            "",
            f"- Gerado em: {report['generatedAt']}",
            f"- Base URL: {report['baseUrl']}",
            f"- Total: {report['summary']['total']}",
            f"- Passou: {report['summary']['passed']}",
            f"- Falhou: {report['summary']['failed']}",
            "",
            "| # | Caso | Metodo | Status | Esperado | Resultado |",
            "|---|------|--------|--------|----------|-----------|",
        ]

        for idx, case in enumerate(report["cases"], start=1):
            outcome = "PASS" if case["ok"] else "FAIL"
            lines.append(
                f"| {idx} | {case['name']} | {case['method']} | {case['status_code']} | "
                f"{case['expected_status']} | {outcome} |"
            )

        failed_cases = [case for case in report["cases"] if not case["ok"]]
        if failed_cases:
            lines.extend(["", "## Falhas", ""])
            for case in failed_cases:
                lines.append(f"- **{case['name']}**: {case['body_snippet']}")

        md_path.write_text("\n".join(lines) + "\n", encoding="utf-8")
        print(f"Relatório Markdown: {md_path}")


def run_collection(args: argparse.Namespace) -> int:
    collection_path = Path(args.collection)
    if not collection_path.exists():
        print(f"ERRO: collection não encontrada: {collection_path}")
        return 2

    collection = json.loads(collection_path.read_text(encoding="utf-8"))
    variables = resolve_collection_vars(collection, args)
    cases = reorder_cases(flatten_items(collection.get("item", [])))

    if not cases:
        print("ERRO: nenhuma request encontrada na collection")
        return 2

    print(f"Executando {len(cases)} requests de {collection_path}")
    print(f"Base URL: {variables.get('baseUrl', '(não definida)')}")

    executions: list[CaseExecution] = []
    with httpx.Client(timeout=args.timeout, follow_redirects=False) as client:
        idx = 1
        for case in cases:
            method = case.request.get("method", "GET").upper()
            url = build_url(case.request, variables)
            headers = build_headers(case.request, variables, collection.get("auth", {}))
            body = build_body(case.request, variables)

            execution, payload = execute_case(
                client,
                case.name,
                method,
                url,
                headers,
                body,
                case.expected_status,
            )
            executions.append(execution)
            print_case(idx, execution)

            if payload is not None:
                apply_response_variables(case.name, payload, variables)
            idx += 1

        if args.run_negative:
            print("\nExecutando cenários negativos...")
            for negative in run_negative_checks(client, variables):
                execution, _ = execute_case(client, *negative)
                executions.append(execution)
                print_case(idx, execution)
                idx += 1

    failures = sum(1 for item in executions if not item.ok)
    report = {
        "generatedAt": datetime.now(timezone.utc).isoformat(),
        "baseUrl": variables.get("baseUrl", ""),
        "summary": {
            "total": len(executions),
            "passed": len(executions) - failures,
            "failed": failures,
        },
        "cases": [
            {
                "name": item.name,
                "method": item.method,
                "url": item.url,
                "status_code": item.status_code,
                "expected_status": item.expected_status,
                "ok": item.ok,
                "body_snippet": item.body_snippet,
            }
            for item in executions
        ],
    }
    write_reports(report, args)

    if failures:
        print(f"\nResultado: {failures} falha(s)")
        return 1

    print("\nResultado: todos os casos passaram")
    return 0


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Runner Postman collection com httpx")
    parser.add_argument("--collection", default="docs/midas-api-collection.json", help="Caminho da collection")
    parser.add_argument("--base-url", default="http://localhost:8080", help="Base URL da API")
    parser.add_argument("--username", default="midas-admin-local", help="Usuário para /api/auth/token")
    parser.add_argument("--password", default="MidasLocal@Admin2026", help="Senha para /api/auth/token")
    parser.add_argument("--timeout", type=float, default=20.0, help="Timeout em segundos por request")
    parser.add_argument("--run-negative", action="store_true", help="Executa cenários negativos de segurança")
    parser.add_argument("--report-json", default="", help="Caminho para salvar relatório JSON")
    parser.add_argument("--report-md", default="", help="Caminho para salvar relatório Markdown")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    return run_collection(args)


if __name__ == "__main__":
    raise SystemExit(main())
