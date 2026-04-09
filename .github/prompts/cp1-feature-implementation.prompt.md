---
agent: agent
description: Implement FIAP CP1 feature with minimal complexity
---

You are implementing a FIAP CP1 feature in this repository.

## Objective
Implement only what is needed for this specific requirement:
{{requirement}}

## Constraints
- Keep code simple and oral-defense friendly.
- Follow existing package structure (`controller`, `service`, `repository`, `entity`, `dto`).
- Keep controllers thin and business logic in services.
- Avoid introducing new frameworks.
- Do not refactor unrelated code.

## Mandatory Checklist
1. Identify the smallest set of files to change.
2. Implement behavior with clear validation and meaningful errors.
3. Add or update tests for the new behavior.
4. Verify role-based access if route/UI is involved.
5. If schema changes, create a Flyway migration.
6. Summarize what changed and why it satisfies the requirement.

## Output Format
Return in this exact structure:

1. Requirement mapping
- Which FIAP requirement is covered.

2. Files changed
- List each file and the reason for the change.

3. Implementation notes
- Explain business rules implemented.
- Explain security rules implemented (if any).
- Explain migration changes (if any).

4. Validation/tests
- What tests were added/updated.
- Which critical scenarios are covered.

5. Oral defense summary
- 5 to 8 bullet points with plain-language explanation I can present.
