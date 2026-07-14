# Family Relation Persistence Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Persist per-user family invite codes and authorized family relations behind JWT-protected Spring Boot APIs without changing the current Dashboard or simulated vitals.

**Architecture:** Add a small MyBatis-Plus relation module in the existing backend. A user gets one unique invite code; a logged-in user submits another user's code to create an owner/member relation. Every read and write scopes by the JWT user id from `SecurityUtil`, while the frontend remains on its local demo data until the API is verified.

**Tech Stack:** Spring Boot 2.7, MyBatis-Plus, MySQL, JUnit/Spring Boot Test, existing `Result` and JWT security utilities.

## Global Constraints

- Do not modify the personal Dashboard, `useSimulatedVitals`, anatomy assets, device sync, or current family simulation UI.
- Do not accept `ownerUserId` from request bodies or URL parameters.
- Only relations with `status = 'ACTIVE'` are returned by the list endpoint.
- A user's invite code is unique and must not be exposed in relation list responses.
- All endpoints use the existing `/api` servlet context and JWT authentication.

---

### Task 1: Add the database schema and entity model

**Files:**
- Create: `health-backend/src/main/resources/db/family_relation.sql`
- Create: `health-backend/src/main/java/com/health/entity/FamilyRelation.java`

**Interfaces:**
- Produces entity fields `id`, `ownerUserId`, `memberUserId`, `relationship`, `status`, `createdAt`, `updatedAt`.

- [ ] **Step 1: Write the failing mapper contract test**

Create a test that expects the relation table to exist and allows a unique active relation. The test must use the existing test database and clean only rows created by the test.

```java
@Test
void familyRelationTableAcceptsOneRelation() {
    jdbcTemplate.update("DELETE FROM family_relation WHERE owner_user_id = 2 AND member_user_id = 3");
    jdbcTemplate.update("INSERT INTO family_relation (owner_user_id, member_user_id, relationship, status) VALUES (2, 3, '父亲', 'ACTIVE')");
    Integer count = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM family_relation WHERE owner_user_id = 2 AND member_user_id = 3", Integer.class);
    assertThat(count).isEqualTo(1);
}
```

- [ ] **Step 2: Run the test and verify the expected missing-table failure**

Run from `health-backend`:

```powershell
mvn -q -Dtest=FamilyRelationSchemaTest test
```

Expected: failure because `family_relation` does not exist.

- [ ] **Step 3: Add the schema and entity**

Use this schema:

```sql
CREATE TABLE IF NOT EXISTS family_relation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  owner_user_id BIGINT NOT NULL,
  member_user_id BIGINT NOT NULL,
  relationship VARCHAR(32) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uk_family_owner_member UNIQUE (owner_user_id, member_user_id),
  CONSTRAINT fk_family_owner FOREIGN KEY (owner_user_id) REFERENCES user(id),
  CONSTRAINT fk_family_member FOREIGN KEY (member_user_id) REFERENCES user(id)
);
```

Annotate `FamilyRelation` with `@TableName("family_relation")`, use `@TableId(type = IdType.AUTO)`, and map camelCase fields through the existing MyBatis configuration.

- [ ] **Step 4: Run the schema test again**

Run the same command. Expected: PASS after the schema is applied to the test database.

- [ ] **Step 5: Commit the schema/model task**

```powershell
git add health-backend/src/main/resources/db/family_relation.sql health-backend/src/main/java/com/health/entity/FamilyRelation.java health-backend/src/test/java/com/health/FamilyRelationSchemaTest.java
git commit -m "feat: add family relation persistence model"
```

### Task 2: Add per-user invite-code lookup and relation mapper

**Files:**
- Modify: `health-backend/src/main/java/com/health/entity/User.java`
- Create: `health-backend/src/main/java/com/health/mapper/FamilyRelationMapper.java`
- Create: `health-backend/src/main/java/com/health/dto/FamilyRelationCreateRequest.java`
- Create: `health-backend/src/main/java/com/health/dto/FamilyRelationUpdateRequest.java`

**Interfaces:**
- `User.inviteCode` stores the unique code.
- `FamilyRelationMapper.findByOwnerId(Long ownerUserId)` returns only active relations.
- `FamilyRelationMapper.findByOwnerAndId(Long ownerUserId, Long id)` scopes ownership.
- `FamilyRelationMapper.findByInviteCode(String inviteCode)` resolves a user through `user.invite_code`.

- [ ] **Step 1: Write failing service tests for unique invite lookup and owner scoping**

Cover these exact cases: user 2 receives a non-empty code; user 2 can list its relation; user 3 cannot delete user 2's relation; a duplicate `(owner_user_id, member_user_id)` is rejected.

- [ ] **Step 2: Run the focused tests and verify they fail**

```powershell
mvn -q -Dtest=FamilyRelationServiceTest test
```

- [ ] **Step 3: Add `invite_code` to the user schema/model and mapper methods**

Use a unique `VARCHAR(24)` column. Generate missing codes with uppercase `ZH` plus 10 random alphanumeric characters, retrying on duplicate key. Never accept a code from the request when creating a user relation.

- [ ] **Step 4: Run focused tests and verify they pass**

```powershell
mvn -q -Dtest=FamilyRelationServiceTest test
```

- [ ] **Step 5: Commit the mapper/code task**

```powershell
git add health-backend/src/main/java/com/health/entity/User.java health-backend/src/main/java/com/health/mapper/FamilyRelationMapper.java health-backend/src/main/java/com/health/dto
git commit -m "feat: add per-user family invite codes"
```

### Task 3: Implement the authorized relation service and controller

**Files:**
- Create: `health-backend/src/main/java/com/health/service/FamilyRelationService.java`
- Create: `health-backend/src/main/java/com/health/service/impl/FamilyRelationServiceImpl.java`
- Create: `health-backend/src/main/java/com/health/controller/FamilyRelationController.java`
- Create: `health-backend/src/main/java/com/health/vo/FamilyRelationVO.java`

**Interfaces:**
- `GET /family/invite-code` returns `{ inviteCode }` for the current JWT user.
- `GET /family/relations` returns active `FamilyRelationVO` records without invite codes.
- `POST /family/relations` accepts `{ inviteCode, relationship }`.
- `DELETE /family/relations/{id}` revokes only a relation owned by the current user.
- `PATCH /family/relations/{id}` updates `relationship` or `status` only for the current owner.

- [ ] **Step 1: Write failing MockMvc tests**

Assert unauthenticated requests return 401, owner-scoped list returns only active rows, and a second user cannot delete the first user's relation.

- [ ] **Step 2: Run the MockMvc tests and verify failure**

```powershell
mvn -q -Dtest=FamilyRelationControllerTest test
```

- [ ] **Step 3: Implement service validation and controller endpoints**

Resolve the current user with `SecurityUtil`, reject blank invite codes and self-association, map duplicate key/database errors to a clear business error, and use `Result.success(...)` / `Result.error(...)` consistently with existing controllers.

- [ ] **Step 4: Run focused tests and verify pass**

```powershell
mvn -q -Dtest=FamilyRelationControllerTest test
```

- [ ] **Step 5: Commit the API task**

```powershell
git add health-backend/src/main/java/com/health/service health-backend/src/main/java/com/health/controller/FamilyRelationController.java health-backend/src/main/java/com/health/vo health-backend/src/test/java/com/health/FamilyRelationControllerTest.java
git commit -m "feat: add authorized family relation APIs"
```

### Task 4: Full verification and handoff

**Files:**
- Modify: `health-dashboard-react/docs/superpowers/plans/2026-07-14-family-relation-persistence.md` to mark completed steps.

- [ ] **Step 1: Apply the SQL schema to the configured development database**

Run the SQL file with the project's configured MySQL connection, then verify `user.invite_code` and `family_relation` exist.

- [ ] **Step 2: Run the full backend test suite**

```powershell
mvn -q test
mvn -q -DskipTests package
```

Expected: exit code 0 for both commands.

- [ ] **Step 3: Run the frontend build**

```powershell
npm run build
```

Expected: exit code 0; current local family UI remains unchanged.

- [ ] **Step 4: Check the diff and status**

```powershell
git diff --check
git status --short
```

Report any pre-existing untracked QA artifacts separately; do not delete or commit them.
