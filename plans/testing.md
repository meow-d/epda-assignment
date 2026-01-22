# Course Recovery System - Integration Test Plan

## Test Environment
- Application: Course Recovery System (CRS)
- Technologies: Jakarta EE, JSP, EJB, MySQL
- Users: Course Administrator, Academic Officer

## Test Scope
Based on assignment functional requirements, this plan covers:

1. User Management
2. Course Recovery Plan
3. Eligibility Check and Enrollment
4. Academic Performance Reporting
5. Email Notifications
6. Authentication and Authorization

## Test Cases
### 1. Authentication and Authorization
- TC-001
  - Name: Admin Login
  - Description: Verify Course Administrator can login with valid credentials
  - Priority: Critical
  - Steps:
    1. Navigate to login page
    2. Enter username: `admin`, password: `admin123`
    3. Click Login button
  - Expected Result: Redirected to `/admindash/`
  - Actual result:

- TC-002
  - Name: Officer Login
  - Description: Verify Academic Officer can login with valid credentials
  - Priority: Critical
  - Steps:
    1. Navigate to login page
    2. Enter username: `officer1`, password: `officer123`
    3. Click Login button
  - Expected Result: Redirected to `/officer/`
  - Actual result:

- TC-003
  - Name: Invalid Login
  - Description: Verify invalid credentials are rejected
  - Priority: High
  - Steps:
    1. Navigate to login page
    2. Enter username: `invalid`, password: `wrong`
    3. Click Login button
  - Expected Result: Error message displayed, remains on login page
  - Actual result:

- TC-004
  - Name: Logout
  - Description: Verify user can logout successfully
  - Priority: High
  - Steps:
    1. Login as admin
    2. Click Logout link
  - Expected Result: Redirected to login page, session invalidated
  - Actual result:

- TC-005
  - Name: Role-Based Access Control
  - Description: Verify users can only access pages for their role
  - Priority: Critical
  - Steps:
    1. Login as admin
    2. Try accessing `/officer/` directly
    3. Logout and login as officer
    4. Try accessing `/admindash/` directly
  - Expected Result: Access denied or redirect to appropriate dashboard
  - Actual result:

---

### 2. User Management

- TC-006
  - Name: Add New User
  - Description: Verify admin can add new user
  - Priority: High
  - Steps:
    1. Login as admin
    2. Navigate to Manage Users
    3. Fill form with valid data:
       - Username: `newuser`
       - Password: `password123`
       - Role: `officer`
       - Email: `newuser@crs.edu`
    4. Submit form
  - Expected Result: User added successfully, appears in users list
  - Actual result:

- TC-007
  - Name: View All Users
  - Description: Verify admin can view all users
  - Priority: High
  - Steps:
    1. Login as admin
    2. Navigate to Manage Users
  - Expected Result: All users displayed in table
  - Actual result:

- TC-008
  - Name: Deactivate User
  - Description: Verify admin can deactivate user account
  - Priority: Medium
  - Steps:
    1. Login as admin
    2. Navigate to Manage Users
    3. Click Deactivate button for a user
    4. Confirm deactivation
  - Expected Result: User status changed to 'inactive'
  - Actual result:

- TC-009
  - Name: User Validation
  - Description: Verify form validation for user creation
  - Priority: High
  - Steps:
    1. Try adding user with empty fields
    2. Try adding user with duplicate username
    3. Try adding user with invalid email
  - Expected Result: Appropriate error messages for each case
  - Actual result:

---

### 3. Course Recovery Plan

- TC-010
  - Name: View Student Recovery Plans
  - Description: Verify officer can view recovery plans for a student
  - Priority: High
  - Steps:
    1. Login as officer
    2. Navigate to Recovery Plans
    3. Select a student (e.g., Sarah Johnson - ID 4)
  - Expected Result: Student details, failed courses, and existing recovery plans displayed
  - Actual result:

- TC-011
  - Name: List Failed Courses
  - Description: Verify failed courses are correctly listed
  - Priority: High
  - Steps:
    1. Select student Sarah Johnson (ID 4) in Recovery Plans
    2. Check Failed Courses section
  - Expected Result: Shows CS205 (D+), MA202 (F), EN201 (D) as failed
  - Actual result:

- TC-012
  - Name: Add Recovery Plan
  - Description: Verify officer can create new recovery plan
  - Priority: High
  - Steps:
    1. In Recovery Plans page, select student
    2. Fill add recovery plan form:
       - Course Code: `CS201`
       - Task: `Complete all tutorial exercises`
       - Deadline: Future date
    3. Submit form
  - Expected Result: New recovery plan appears in list, status 'active'
  - Actual result:

- TC-013
  - Name: Update Recovery Plan Status
  - Description: Verify officer can update recovery plan status
  - Priority: High
  - Steps:
    1. In Recovery Plans page
    2. Change status of a plan from 'active' to 'completed'
    3. Submit
  - Expected Result: Plan status updated to 'completed'
  - Actual result:

- TC-014
  - Name: Delete Recovery Plan
  - Description: Verify officer can delete recovery plan
  - Priority: Medium
  - Steps:
    1. In Recovery Plans page
    2. Click Delete button for a plan
    3. Confirm deletion
  - Expected Result: Plan removed from list
  - Actual result:

- TC-015
  - Name: Recovery Plan Email Notification
  - Description: Verify email sent when recovery plan created
  - Priority: Medium
  - Steps:
    1. Create new recovery plan
    2. Check email logs or test email account
  - Expected Result: Email sent to student with recovery plan details
  - Actual result:

---

### 4. Eligibility Check and Enrollment

- TC-016
  - Name: View Eligibility List
  - Description: Verify officer can view all students' eligibility status
  - Priority: High
  - Steps:
    1. Login as officer
    2. Navigate to Eligibility Check
  - Expected Result: All students displayed with CGPA, failed courses count, and eligibility status
  - Actual result:

- TC-017
  - Name: Eligible Student
  - Description: Verify eligibility criteria (CGPA ≥ 2.0, ≤ 3 failed courses)
  - Priority: High
  - Steps:
    1. Check John Doe (ID 1) - CGPA 3.25, 0 failed courses
  - Expected Result: Status shows "Eligible"
  - Actual result:

- TC-018
  - Name: Not Eligible (Low CGPA)
  - Description: Verify students with CGPA < 2.0 marked ineligible
  - Priority: High
  - Steps:
    1. Check Sarah Johnson (ID 4) - CGPA 1.95, 3 failed courses
  - Expected Result: Status shows "Not Eligible" (CGPA too low)
  - Actual result:

- TC-019
  - Name: Not Eligible (Too Many Failed Courses)
  - Description: Verify students with > 3 failed courses marked ineligible
  - Priority: High
  - Steps:
    1. Check David Wilson (ID 7) - CGPA 1.75, 5 failed courses
  - Expected Result: Status shows "Not Eligible" (too many failed courses)
  - Actual result:

- TC-020
  - Name: CGPA Calculation
  - Description: Verify CGPA calculation correctness
  - Priority: Critical
  - Steps:
    1. Verify CGPA for Alex Tan (ID 3):
       - CS201: A (4.0) × 3 = 12
       - CS205: B+ (3.3) × 3 = 9.9
       - CS210: B (3.0) × 3 = 9
       - MA202: C+ (2.3) × 4 = 9.2
       - EN201: A- (3.7) × 2 = 7.4
       - Total: 47.5 / 15 = 3.17 (approx 3.11 in data)
  - Expected Result: CGPA ≈ 3.11
  - Actual result:

---

### 5. Academic Performance Reporting

- TC-021
  - Name: View Academic Report
  - Description: Verify officer can generate academic report for student
  - Priority: High
  - Steps:
    1. Login as officer
    2. Navigate to Academic Report
    3. Select a student (e.g., Alex Tan - ID 3)
  - Expected Result: Student details, all grades, and CGPA displayed
  - Actual result:

- TC-022
  - Name: Grade Display
  - Description: Verify grades displayed correctly with course details
  - Priority: High
  - Steps:
    1. View report for Alex Tan
  - Expected Result: Shows CS201 (A, 4.0), CS205 (B+, 3.3), etc.
  - Actual result:

- TC-023
  - Name: Send Academic Report via Email
  - Description: Verify academic report can be emailed to student
  - Priority: Medium
  - Steps:
    1. View report for student
    2. Click "Send Report via Email"
  - Expected Result: Success message, email sent to student
  - Actual result:

- TC-024
  - Name: No Grades Student
  - Description: Verify handling of students with no grades
  - Priority: Medium
  - Steps:
    1. Create new student in database with no grades
    2. Try viewing academic report
  - Expected Result: Shows student info with CGPA: 0.00, message "No grades found"
  - Actual result:

---

### 6. Error Handling

- TC-025
  - Name: Database Connection Failure
  - Description: Verify graceful handling of database connection errors
  - Priority: Medium
  - Steps:
    1. Stop MySQL
    2. Try logging in
  - Expected Result: User-friendly error message, no stack traces exposed
  - Actual result:

- TC-026
  - Name: Invalid Student Selection
  - Description: Verify handling of invalid student ID
  - Priority: Medium
  - Steps:
    1. Manually navigate to `recovery-plan?studentId=9999`
  - Expected Result: Error message or redirects to dashboard
  - Actual result:

- TC-027
  - Name: Form Input Validation
  - Description: Verify all forms validate inputs
  - Priority: High
  - Steps:
    1. Test date fields with past dates
    2. Test text fields with special characters
    3. Test numeric fields with non-numeric input
  - Expected Result: Validation errors prevent submission
  - Actual result:

---

## Test Data Reference

### Default Users
| Username | Password | Role | Email |
|-----------|-----------|-------|-------|
| admin | admin123 | admin | admin@crs.edu |
| officer1 | officer123 | officer | officer1@crs.edu |
| officer2 | officer123 | officer | officer2@crs.edu |

### Sample Students
| ID | Name | Program | Expected CGPA | Failed Courses |
|----|------|---------|---------------|---------------|
| 1 | John Doe | BCS | 3.25 | 0 |
| 2 | Jane Smith | BIT | 2.85 | 0 |
| 3 | Alex Tan | BCS | 3.11 | 0 |
| 4 | Sarah Johnson | BSE | 1.95 | 3 |
| 5 | Michael Brown | BCS | 2.45 | 1 |
| 6 | Emily Davis | BIT | 3.55 | 0 |
| 7 | David Wilson | BCS | 1.75 | 5 |

### Eligibility Criteria
- **CGPA ≥ 2.0**
- **Failed courses ≤ 3**
- Both must be met for eligibility

---

## Test Execution Checklist

- [ ] All authentication tests (TC-001 to TC-005)
- [ ] All user management tests (TC-006 to TC-009)
- [ ] All recovery plan tests (TC-010 to TC-015)
- [ ] All eligibility tests (TC-016 to TC-020)
- [ ] All academic report tests (TC-021 to TC-024)
- [ ] All error handling tests (TC-025 to TC-027)

---

## Known Limitations

1. Email functionality requires valid SMTP configuration (may need to skip email-related tests if not configured)
2. Session timeout set to 30 minutes
3. Passwords stored in plain text (for demonstration only - production would use hashing)
4. Course Retrieval Policy (3 attempts) not fully enforced in current implementation

## Success Criteria

- All critical priority tests pass
- At least 80% of high priority tests pass
- No unhandled exceptions thrown
- All error messages are user-friendly
- Role-based access control enforced correctly
