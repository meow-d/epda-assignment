package com.crs.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Username validation: alphanumeric, underscore, dash, 3-50 chars
    private static final Pattern USERNAME_PATTERN =
        Pattern.compile("^[A-Za-z0-9_-]{3,50}$");

    // Password validation: at least 6 chars, at least one letter and one number
    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,}$");

    // Course code validation: typically 3-4 letters followed by 3-4 digits
    private static final Pattern COURSE_CODE_PATTERN =
        Pattern.compile("^[A-Z]{2,4}\\d{3,4}$");

    // Student ID validation: numeric, 5-10 digits
    private static final Pattern STUDENT_ID_PATTERN =
        Pattern.compile("^\\d{5,10}$");

    // Grade validation: A, B+, B, C+, C, D, F or numeric 0.0-4.0
    private static final Pattern GRADE_PATTERN =
        Pattern.compile("^(A|B\\+|B|C\\+|C|D|F|\\d\\.\\d)$");

    // CGPA validation: decimal between 0.00 and 4.00
    private static final Pattern CGPA_PATTERN =
        Pattern.compile("^\\d\\.\\d{2}$");

    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Email is required");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return new ValidationResult(false, "Invalid email format");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "Username is required");
        }
        if (!USERNAME_PATTERN.matcher(username.trim()).matches()) {
            return new ValidationResult(false, "Username must be 3-50 characters, alphanumeric with underscores and dashes only");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return new ValidationResult(false, "Password is required");
        }
        if (password.length() < 6) {
            return new ValidationResult(false, "Password must be at least 6 characters long");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return new ValidationResult(false, "Password must contain at least one letter and one number");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateCourseCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return new ValidationResult(false, "Course code is required");
        }
        if (!COURSE_CODE_PATTERN.matcher(courseCode.trim().toUpperCase()).matches()) {
            return new ValidationResult(false, "Course code must be in format like CS201 or COMP205");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return new ValidationResult(false, "Student ID is required");
        }
        try {
            int id = Integer.parseInt(studentId.trim());
            if (id <= 0) {
                return new ValidationResult(false, "Student ID must be a positive number");
            }
            if (!STUDENT_ID_PATTERN.matcher(studentId.trim()).matches()) {
                return new ValidationResult(false, "Student ID must be 5-10 digits");
            }
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Student ID must be numeric");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateGrade(String grade) {
        if (grade == null || grade.trim().isEmpty()) {
            return new ValidationResult(false, "Grade is required");
        }
        if (!GRADE_PATTERN.matcher(grade.trim().toUpperCase()).matches()) {
            return new ValidationResult(false, "Grade must be A, B+, B, C+, C, D, F, or numeric (0.0-4.0)");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateCgpa(String cgpa) {
        if (cgpa == null || cgpa.trim().isEmpty()) {
            return new ValidationResult(false, "CGPA is required");
        }
        try {
            double cgpaValue = Double.parseDouble(cgpa.trim());
            if (cgpaValue < 0.0 || cgpaValue > 4.0) {
                return new ValidationResult(false, "CGPA must be between 0.00 and 4.00");
            }
            if (!CGPA_PATTERN.matcher(cgpa.trim()).matches()) {
                return new ValidationResult(false, "CGPA must be in format X.XX");
            }
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "CGPA must be a valid decimal number");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return new ValidationResult(false, fieldName + " is required");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateLength(String value, String fieldName, int minLength, int maxLength) {
        if (value == null) {
            return new ValidationResult(false, fieldName + " is required");
        }
        if (value.length() < minLength) {
            return new ValidationResult(false, fieldName + " must be at least " + minLength + " characters");
        }
        if (value.length() > maxLength) {
            return new ValidationResult(false, fieldName + " must not exceed " + maxLength + " characters");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return new ValidationResult(false, "Role is required");
        }
        String normalizedRole = role.trim().toLowerCase();
        if (!normalizedRole.equals("admin") && !normalizedRole.equals("officer")) {
            return new ValidationResult(false, "Role must be either 'admin' or 'officer'");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return new ValidationResult(false, "Status is required");
        }
        String normalizedStatus = status.trim().toLowerCase();
        if (!normalizedStatus.equals("active") && !normalizedStatus.equals("inactive")) {
            return new ValidationResult(false, "Status must be either 'active' or 'inactive'");
        }
        return new ValidationResult(true, null);
    }

    // Validation result class
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}