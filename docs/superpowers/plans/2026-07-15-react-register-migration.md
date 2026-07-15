# React Registration Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Complete the authentication flow at `http://localhost:3202` by migrating the existing registration experience into React.

**Architecture:** Keep `App` as the auth gate. Render a React `RegisterPage` when the unauthenticated entry state is `register`; return to `LoginPage` after a successful registration. Reuse the existing `/api/auth/register` endpoint and the migrated login assets/styles.

**Tech Stack:** React, TypeScript, Vite, HTML/CSS, Fetch API, lucide-react.

## Global Constraints

- Keep the existing Dashboard, health data logic, device synchronization, family features, and login visual language unchanged.
- Use `http://localhost:3202` as the only frontend entry address.
- Registration must validate username, nickname, email, password, and confirmation before sending.
- A successful registration returns the user to the React login screen and does not create a fake token.

### Task 1: Registration API and Form Component

**Files:**
- Modify: `health-dashboard-react/src/services/auth.ts`
- Create: `health-dashboard-react/src/components/RegisterPage.tsx`

- [x] Add a typed `register` request for `/api/auth/register`.
- [x] Add a responsive React registration screen using the existing health hero asset and auth design tokens.
- [x] Validate username length, required nickname, email format, password length, and matching confirmation.
- [x] Expose `onBackToLogin` and `onRegistered` callbacks.

### Task 2: Auth Gate Integration

**Files:**
- Modify: `health-dashboard-react/src/App.tsx`
- Modify: `health-dashboard-react/src/components/LoginPage.tsx`
- Modify: `health-dashboard-react/src/styles.css`

- [x] Track an unauthenticated auth mode of `login` or `register`.
- [x] Make both visible “创建账户” actions open the React registration screen.
- [x] Return to login after registration and surface the success message.
- [x] Add the registration layout, field, error, and mobile styles.

### Task 3: Verification

**Files:**
- Test: `health-dashboard-react` build and browser flow.

- [x] Run `npm run build`.
- [x] Run `git diff --check`.
- [x] Verify registration navigation, validation, return-to-login, mobile overflow, and token-gated Dashboard behavior in a real browser.
