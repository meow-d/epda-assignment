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
| `/admin/` | GET | | Not yet tested |
| `/admin/users` | GET | | Not yet tested |
| `/admin/add-user` | GET | | Not yet tested |
| `/admin/add-user` | POST | | Not yet tested |
| `/admin/edit-user` | GET | | Not yet tested |
| `/admin/update-user` | POST | | Not yet tested |
| `/admin/delete-user` | POST | | Not yet tested |
| `/admin/advanced-reports` | GET | | Not yet tested |

---

## Officer Endpoints (Requires officer or admin role)

| Endpoint | Method | Status | Notes |
|----------|--------|--------|-------|
| `/officer/` | GET | | Not yet tested |
| `/officer/recovery-plan` | GET | | Not yet tested |
| `/officer/eligibility` | GET | | Not yet tested |
| `/officer/academic-report` | GET | | Not yet tested |
| `/officer/add-recovery-plan` | POST | | Not yet tested |
| `/officer/update-recovery-plan` | POST | | Not yet tested |
| `/officer/delete-recovery-plan` | POST | | Not yet tested |
| `/officer/send-report` | POST | | Not yet tested |

---

## Auth Endpoints

| Endpoint | Method | Status | Notes |
|----------|--------|--------|-------|
| `/auth/logout` | GET/POST | | Not yet tested |

---

## Test Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Officer | officer | officer123 |

---

## Issues Found

### [Date] - Issue Description
- **Endpoint:** 
- **Error:** 
- **Root Cause:** 
- **Status:** 

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
