# Independent Family Relationship Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Let each side of a family connection independently set an optional relationship label while keeping cards focused on the private display name.

**Architecture:** Reuse the existing bidirectional `family_relation` rows. The owner-side `relationship` value is that user's view of the member; create requests may leave it empty, and accepting requests may provide a different relationship for the reverse row. Existing `display_name` remains the private card label. The frontend renders relationship only in profile detail and sends optional values through existing relation endpoints.

**Tech Stack:** Spring Boot/MyBatis-Plus, React/TypeScript, existing CSS tokens and fetch services.

## Global Constraints

- Do not change family snapshot polling, personal dashboard, device sync, or authorization semantics.
- Do not require a relationship to send or accept a family request.
- Do not display relationship labels on family cards; display them in personal detail only.
- Empty relationship values render as `未定义` in personal detail.

### Task 1: Independent relationship API behavior

**Files:** `health-backend/src/main/java/com/health/service/FamilyRelationService.java`, `health-backend/src/main/java/com/health/service/impl/FamilyRelationServiceImpl.java`, `health-backend/src/main/java/com/health/controller/FamilyRelationController.java`, `health-backend/src/test/java/com/health/FamilyRelationServiceTest.java`

- [ ] Add an optional relationship argument to request decisions.
- [ ] Preserve the requester's relationship on the original row and save the recipient's optional relationship only on the reverse row.
- [ ] Treat missing relationship as an empty value and keep existing active relations working.
- [ ] Verify a test where the two directions contain different values.

### Task 2: Frontend relationship editing and display

**Files:** `health-dashboard-react/src/services/familyRelations.ts`, `health-dashboard-react/src/components/NotificationDialog.tsx`, `health-dashboard-react/src/components/FamilyDashboard.tsx`, `health-dashboard-react/src/types.ts`, `health-dashboard-react/src/styles.css`

- [ ] Send optional relationship values for create and accept operations.
- [ ] Add an optional relationship selector/input to request acceptance.
- [ ] Remove relationship text from cards and show device as an explicit device field.
- [ ] Render relationship in profile detail with `未定义`, plus set/edit controls.

### Task 3: Verification

- [ ] Run `npm run build`.
- [ ] Run `mvn -q test` and `mvn -q -DskipTests package`.
- [ ] Run `git diff --check` and browser checks at 390×844, 768×1024, and 1366×768.
