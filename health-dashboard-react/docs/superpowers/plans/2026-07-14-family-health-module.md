# Family Health Module Implementation Plan

**Goal:** Add a local simulated family-link workflow and a private family health dashboard without changing the existing personal dashboard or device-sync business logic.

**Architecture:** Keep the existing dashboard as the default view. Lift only sidebar view selection into `App`, add a `useFamilyMembers` hook for linked-member state and visibility-aware simulation, and render a focused `FamilyDashboard` page for the new family module. The first version uses a demo invite code and keeps family data separate from the current user's vitals.

**Tech Stack:** React, TypeScript, native CSS, existing Lucide icons.

## Global Constraints

- Do not modify `useSimulatedVitals`, Page Visibility behavior for the personal dashboard, device synchronization, anatomy assets, trend data, or existing copy.
- Only linked family members can appear in the family dashboard.
- A member who is not wearing the device must not show fabricated live vitals.
- The local demo invite flow must be clearly simulated and replaceable by a backend later.

### Task 1: Family data model and simulation hook

**Files:**
- Modify: `src/types.ts`
- Create: `src/hooks/useFamilyMembers.ts`

Define `FamilyMember` with relationship, device, wearing state, vitals, and last-sync fields. Seed two local members, update only wearing members at a low frequency, pause timers while the document is hidden, and expose `addByInviteCode(code)` for the demo code `FAMILY-2026`.

### Task 2: Family dashboard UI

**Files:**
- Create: `src/components/FamilyDashboard.tsx`
- Modify: `src/styles.css`

Render a page title, linked-member count, add-family form, member cards, wearing/device status, and live vitals only when available. Add a clear empty/error state for invalid invite codes and preserve the existing green dashboard visual system.

### Task 3: Navigation and view integration

**Files:**
- Modify: `src/components/Sidebar.tsx`
- Modify: `src/App.tsx`

Add a `家人健康` navigation item, lift active view state to `App`, preserve the existing overview render, and show `FamilyDashboard` only for the new view.

### Task 4: Verification

Run `npm run build`, open `http://localhost:3202`, verify the family navigation, valid/invalid invite behavior, wearing and not-wearing states, and page-visible timer behavior. Check desktop and mobile layouts for horizontal overflow.
