# API Collection Test Report

- Gerado em: 2026-04-10T00:04:58.085361+00:00
- Base URL: http://localhost:8080
- Total: 19
- Passou: 19
- Falhou: 0

| # | Caso | Metodo | Status | Esperado | Resultado |
|---|------|--------|--------|----------|-----------|
| 1 | Emitir Token | POST | 200 | 200 | PASS |
| 2 | Refresh Token | POST | 200 | 200 | PASS |
| 3 | Logout | POST | 204 | 204 | PASS |
| 4 | Create Account | POST | 201 | 201 | PASS |
| 5 | Get All Accounts | GET | 200 | 200 | PASS |
| 6 | Get Account by ID | GET | 200 | 200 | PASS |
| 7 | Update Account | PUT | 200 | 200 | PASS |
| 8 | Create Transaction - Receita | POST | 201 | 201 | PASS |
| 9 | Create Transaction - Despesa | POST | 201 | 201 | PASS |
| 10 | Get All Transactions | GET | 200 | 200 | PASS |
| 11 | Get Transaction by ID | GET | 200 | 200 | PASS |
| 12 | Get Transactions by Account ID | GET | 200 | 200 | PASS |
| 13 | Get Transactions by Account ID (Paged) | GET | 200 | 200 | PASS |
| 14 | Update Transaction | PUT | 200 | 200 | PASS |
| 15 | Delete Transaction | DELETE | 204 | 204 | PASS |
| 16 | Delete Account | DELETE | 204 | 204 | PASS |
| 17 | NEG - Invalid credentials | POST | 400 | 400 | PASS |
| 18 | NEG - Protected endpoint without token | GET | 401 | 401 | PASS |
| 19 | NEG - Protected endpoint with invalid token | GET | 401 | 401 | PASS |
