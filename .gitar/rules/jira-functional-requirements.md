---
title: "Jira Functional Requirements Verification"
description: "Verify a PR's implementation fulfils the requirements of its linked Jira ticket and report the result in the summary comment"
slug: "jira_functional_requirements_verification"
when: "PR/MR references a Jira issue key in its title or description (evaluate on open and on every code update)"
actions: "Fetch the linked Jira issue, compare the diff against its described requirements and acceptance criteria, and add a 'Functional Requirements' section to the Gitar summary comment stating whether requirements are fully met, partially met (gaps), or misinterpreted"
integrations: "jira"
---

# Jira Functional Requirements Verification

When a pull request is tied to a Jira ticket, read what the ticket actually asks
for and judge whether the code in the PR delivers it. Surface the verdict as a
**Functional Requirements** section inside Gitar's summary comment so a reviewer can
tell, at a glance, whether the implementation matches the intent — and where it
diverges.

## When to Use This

- The PR title **or** description contains a Jira issue key matching the pattern
  `PROJ-123` (uppercase letters, a hyphen, then digits — e.g. `PET-42`, `FE-1007`).
  Also detect magic words such as `Fixes PET-42`, `Closes PET-42`, `Relates to PET-42`.
- Re-evaluate on every update (new commits, force-push, description edit), not only
  on open — the implementation changes over the PR's life, so the verdict must be
  refreshed. Build on the previous evaluation rather than starting from scratch.

Skip if:
- No valid Jira issue key is present. Bracket/area tags like `[Web]`, `[docs]`,
  `[hotfix]` are **not** issue references — only match letters-hyphen-digits.
- The PR is a pure chore with no behavioural intent (e.g. dependency bump,
  formatting-only, generated-file sync) AND the ticket is administrative. In that
  case note "No functional requirements to verify" rather than forcing a verdict.
- The referenced issue cannot be fetched (deleted, no access). Note that the ticket
  was referenced but could not be read, instead of guessing.

## How It Works

### 1. Resolve the Jira ticket

- Extract every Jira issue key from the title and description (a PR may reference
  more than one). Prefer keys in the description body and "magic word" closures.
- Fetch each issue via the Jira integration: summary, description, **acceptance
  criteria**, sub-tasks/checklist items, labels, and issue type
  (Story / Bug / Task / Spike).

### 2. Extract the functional requirements

From the ticket, distil a concrete, testable list of what must be true when the
work is done. Pull from, in priority order:

1. An explicit **Acceptance Criteria** section (Given/When/Then, checklists).
2. Sub-tasks or a definition-of-done checklist.
3. The description's "should / must / expected behaviour" statements.

Treat each as a separate requirement. For a **Bug**, the requirement is "the
described faulty behaviour no longer occurs and the expected behaviour does."

### 3. Map each requirement to the implementation

For every requirement, inspect the diff (and surrounding code where needed) for
evidence it is satisfied. Classify each requirement as:

- **Met** — the diff clearly implements it; cite the file/function as evidence.
- **Partial** — started but incomplete (e.g. happy path only, missing validation,
  missing edge case the ticket calls out, no tests where the ticket asks for them).
- **Missing** — no corresponding change found in the PR.
- **Misinterpreted** — code was written for this requirement but does something
  different from, or contrary to, what the ticket describes.

Also flag **scope creep**: substantive behaviour in the PR that no requirement in
the ticket asks for (note it, do not fail the PR for it).

### 4. Decide the overall verdict

| Overall status | Condition |
|----------------|-----------|
| ✅ **Fully implemented** | Every requirement is **Met**, no misinterpretations |
| 🟡 **Gaps found** | At least one **Partial** or **Missing**, none **Misinterpreted** |
| 🔴 **Misinterpretation** | One or more requirements are **Misinterpreted** or contradict the ticket |

### 5. Add the "Functional Requirements" section at the bottom of the PR description

Append the following section to Gitar's PR summary comment (update it in place on
re-evaluation rather than duplicating):

```markdown
## Functional Requirements

**Ticket:** [PET-42](https://your-domain.atlassian.net/browse/PET-42) — Notify owner when a visit is scheduled
**Status:** 🟡 Gaps found

| # | Requirement (from Jira) | Status | Evidence / Notes |
|---|--------------------------|--------|------------------|
| 1 | Owner receives a notification when a visit is booked | ✅ Met | `VisitController.processNewVisitForm` calls `NotificationService.notifyOwner` |
| 2 | Notification includes vet name and appointment time | 🟡 Partial | Time included; vet name not passed to the template |
| 3 | Failed notifications are logged, not fatal | ❌ Missing | No error handling around the send call |

**Gaps & misinterpretations**
- Req 2: `notify.html` omits the assigned vet — acceptance criteria asks for it.
- Req 3: a send failure currently propagates and would roll back the visit.

**Scope beyond the ticket**
- Adds a `/notifications/preferences` endpoint not mentioned in PET-42 (verify intended).
```

Rules for the section:
- Always include the linked ticket(s) and the overall **Status** line first.
- One table row per requirement, each with a status icon and concrete evidence
  (reference real files/symbols from the diff — `path:symbol`).
- Only render **Gaps & misinterpretations** / **Scope beyond the ticket** blocks
  when they have content; omit empty blocks.
- Keep it factual and specific. Do not restate the whole ticket; state what is and
  isn't satisfied.

## Why This Matters

- **Catches intent drift early** — reviewers see if the code solves the problem the
  ticket describes, not just whether the code is clean.
- **Makes gaps explicit** — partial and missing requirements are called out before
  merge instead of surfacing in QA or production.
- **Flags misinterpretation** — distinguishes "incomplete" from "built the wrong
  thing," which need different fixes.
- **Preserves traceability** — ties the merged code back to the functional reason it
  was written.
