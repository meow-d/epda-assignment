# Endpoint Testing Status

**Branch:** test-and-fix  
**Date:** 2026-03-20  
**Base URL:** http://localhost:8080

---

## Public Endpoints (No Authentication Required)

| Endpoint | Method | Status | Notes |
|----------|--------|--------|-------|
| `/login.jsp` | GET | ✅ PASS | Returns 200, login form renders correctly |
| `/auth/login` | POST | ✅ PASS | Returns 302 redirect to /admin/ on success |
| `/forgotPassword.jsp` | GET | ✅ PASS | Returns 200 |
| `/resetPassword.jsp` | GET | ✅ PASS | Returns 200 |
| `/css/style.css` | GET | ✅ PASS | Returns 200 |

---

## Admin Endpoints (Requires admin role)

| Endpoint | Method | Status | Notes |
|----------|--------|--------|-------|
| `/admin/` | GET | ✅ PASS | Returns 200, dashboard renders correctly |
| `/admin/users` | GET | ✅ PASS | Returns 200 |
| `/admin/add-user` | GET | ✅ PASS | Returns 200 |
| `/admin/add-user` | POST | | Not yet tested |
| `/admin/edit-user` | GET | | Not yet tested |
| `/admin/update-user` | POST | | Not yet tested |
| `/admin/delete-user` | POST | | Not yet tested |
| `/admin/advanced-reports` | GET | ✅ PASS | Returns 200, charts render with proper JSON data |

---

## Officer Endpoints (Requires officer or admin role)

| Endpoint | Method | Status | Notes |
|----------|--------|--------|-------|
| `/officer/` | GET | ✅ PASS | Returns 200 |
| `/officer/recovery-plan` | GET | ✅ PASS | Returns 200 |
| `/officer/eligibility` | GET | ✅ PASS | Returns 200, content renders correctly |
| `/officer/academic-report` | GET | ✅ PASS | Returns 200 |
| `/officer/add-recovery-plan` | POST | | Not yet tested |
| `/officer/update-recovery-plan` | POST | | Not yet tested |
| `/officer/delete-recovery-plan` | POST | | Not yet tested |
| `/officer/send-report` | POST | | Not yet tested |

---

## Auth Endpoints

| Endpoint | Method | Status | Notes |
|----------|--------|--------|-------|
| `/auth/logout` | GET | ✅ PASS | Returns 302 redirect to /login.jsp |

---

## Test Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Officer | officer | officer123 |

---

## Issues Found

### 2026-03-20 - Advanced Reports JavaScript Syntax Error
- **Endpoint:** `/admin/advanced-reports`
- **Error:** `Uncaught SyntaxError: expected expression, got '<'`
- **Root Cause:** Java Map.toString().replace() produced invalid JavaScript syntax
- **Fix:** Added Jackson dependency, serialize Maps to proper JSON in servlet
- **Status:** ✅ FIXED

### 2026-03-20 - Admin Nav Shows Wrong Advanced Reports URL
- **Endpoint:** All officer pages accessed by admin
- **Error:** Advanced Reports link pointed to `/officer/advanced-reports`
- **Root Cause:** header.jsp had wrong URL for admin nav item
- **Fix:** Changed to `/admin/advanced-reports`
- **Status:** ✅ FIXED

### 2026-03-20 - Academic Report Property Not Found
- **Endpoint:** `/officer/academic-report`
- **Error:** `Property [courseTitle] not found on type [com.crs.model.Grade]`
- **Root Cause:** JSP referenced non-existent property on Grade model
- **Fix:** Removed Course Title and Credit Hours columns from table
- **Status:** ✅ FIXED 

---

## Testing Commands Reference

```bash
# Test login page
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/login.jsp

# Test with session cookie
curl -s -c cookies.txt -b cookies.txt http://localhost:8080/admin/

# POST login
curl -s -c cookies.txt -b cookies.txt -X POST \
  -d "username=admin&password=admin123&csrfToken=TOKEN" \
  http://localhost:8080/auth/login

# View logs
podman logs crs-tomee 2>&1 | tail -100

# Watch logs in real-time
podman logs -f crs-tomee 2>&1
```
