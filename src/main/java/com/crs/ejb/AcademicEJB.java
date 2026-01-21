package com.crs.ejb;

import com.crs.dao.StudentDAO;
import com.crs.model.Grade;
import com.crs.model.Student;
import com.crs.util.EmailUtil;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class AcademicEJB {

    public void addStudent(Student student) throws SQLException, IOException {
        StudentDAO.addStudent(student);
        sendWelcomeEmail(student.getEmail(), student.getName());
    }

    public void updateStudent(Student student) throws SQLException, IOException {
        StudentDAO.updateStudent(student);
    }

    public Student getStudentById(int studentId) throws SQLException, IOException {
        return StudentDAO.getStudentById(studentId);
    }

    public List<Student> getAllStudents() throws SQLException, IOException {
        return StudentDAO.getAllStudents();
    }

    public List<Grade> getFailedCourses(int studentId) throws SQLException, IOException {
        return StudentDAO.getFailedCourses(studentId);
    }

    public List<Grade> getStudentGrades(int studentId) throws SQLException, IOException {
        return StudentDAO.getStudentGrades(studentId);
    }

    public double calculateCGPA(int studentId) throws SQLException, IOException {
        return StudentDAO.calculateCGPA(studentId);
    }

    public int getFailedCourseCount(int studentId) throws SQLException, IOException {
        return StudentDAO.getFailedCourseCount(studentId);
    }

    public boolean checkEligibility(int studentId) throws SQLException, IOException {
        double cgpa = calculateCGPA(studentId);
        int failedCourses = getFailedCourseCount(studentId);
        return cgpa >= 2.0 && failedCourses <= 3;
    }

    public List<Student> getIneligibleStudents() throws SQLException, IOException {
        List<Student> allStudents = getAllStudents();
        List<Student> ineligible = new java.util.ArrayList<>();
        for (Student student : allStudents) {
            if (!checkEligibility(student.getId())) {
                student.setCurrentCgpa(calculateCGPA(student.getId()));
                ineligible.add(student);
            }
        }
        return ineligible;
    }

    public Map<String, Object> generateAcademicReport(int studentId) throws SQLException, IOException {
        Map<String, Object> report = new HashMap<>();
        Student student = getStudentById(studentId);
        List<Grade> grades = getStudentGrades(studentId);
        double cgpa = calculateCGPA(studentId);

        report.put("student", student);
        report.put("grades", grades);
        report.put("cgpa", cgpa);

        return report;
    }

    public void sendAcademicReport(int studentId) throws SQLException, IOException {
        Map<String, Object> report = generateAcademicReport(studentId);
        Student student = (Student) report.get("student");
        List<Grade> grades = (List<Grade>) report.get("grades");
        double cgpa = (double) report.get("cgpa");

        StringBuilder body = new StringBuilder();
        body.append("Academic Performance Report\n");
        body.append("==========================\n\n");
        body.append("Student Name: ").append(student.getName()).append("\n");
        body.append("Student ID: ").append(student.getId()).append("\n");
        body.append("Program: ").append(student.getProgram()).append("\n\n");
        body.append("Cumulative GPA: ").append(String.format("%.2f", cgpa)).append("\n\n");
        body.append("Grades:\n");

        for (Grade grade : grades) {
            body.append("- ").append(grade.getCourseCode())
                .append(": ").append(grade.getGrade())
                .append(" (").append(String.format("%.1f", grade.getGradePoint())).append(")\n");
        }

        String subject = "Academic Performance Report";
        try {
            EmailUtil.sendEmail(student.getEmail(), subject, body.toString());
        } catch (jakarta.mail.MessagingException e) {
            e.printStackTrace();
        }
    }

    private void sendWelcomeEmail(String email, String name) {
        try {
            String subject = "Welcome to Course Recovery System";
            String body = "Dear " + name + ",\n\n" +
                    "Welcome to the Course Recovery System!\n\n" +
                    "Best regards,\n" +
                    "Course Recovery System Team";
            EmailUtil.sendEmail(email, subject, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
