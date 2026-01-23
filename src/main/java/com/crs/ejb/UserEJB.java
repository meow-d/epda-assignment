package com.crs.ejb;

import com.crs.dao.PasswordResetDAO;
import com.crs.dao.UserDAO;
import com.crs.model.User;
import com.crs.util.EmailUtil;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class UserEJB {

    public void addUser(User user) throws SQLException, IOException {
        UserDAO.addUser(user);
        sendWelcomeEmail(user.getEmail(), user.getUsername());
    }

    public void updateUser(User user) throws SQLException, IOException {
        UserDAO.updateUser(user);
    }

    public void deactivateUser(int userId) throws SQLException, IOException {
        User user = UserDAO.getUserById(userId);
        if (user != null) {
            UserDAO.deleteUser(userId);
            sendDeactivationEmail(user.getEmail(), user.getUsername());
        }
    }

    public User authenticateUser(String username, String password) throws SQLException, IOException {
        User user = UserDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password) && user.getStatus().equals("active")) {
            return user;
        }
        return null;
    }

    public User getUserById(int userId) throws SQLException, IOException {
        return UserDAO.getUserById(userId);
    }

    public List<User> getAllUsers() throws SQLException, IOException {
        return UserDAO.getAllUsers();
    }

    public boolean requestPasswordReset(String email) throws SQLException, IOException {
        User user = UserDAO.getUserByUsername(email);
        if (user != null && user.getStatus().equals("active")) {
            // Clean up expired tokens first
            PasswordResetDAO.cleanupExpiredTokens();

            // Create a new reset token
            String token = PasswordResetDAO.createPasswordResetToken(user.getId());

            // Send reset email
            sendPasswordResetRequestEmail(user.getEmail(), user.getUsername(), token);
            return true;
        }
        return false;
    }

    public boolean resetPasswordWithToken(String token, String newPassword) throws SQLException, IOException {
        Integer userId = PasswordResetDAO.validatePasswordResetToken(token);
        if (userId != null) {
            User user = UserDAO.getUserById(userId);
            if (user != null) {
                user.setPassword(newPassword);
                UserDAO.updateUser(user);
                PasswordResetDAO.markTokenAsUsed(token);
                sendPasswordResetConfirmationEmail(user.getEmail(), user.getUsername());
                return true;
            }
        }
        return false;
    }

    public boolean resetPassword(String email, String newPassword) throws SQLException, IOException {
        User user = UserDAO.getUserByUsername(email);
        if (user != null) {
            user.setPassword(newPassword);
            UserDAO.updateUser(user);
            sendPasswordResetEmail(user.getEmail(), user.getUsername());
            return true;
        }
        return false;
    }

    private void sendWelcomeEmail(String email, String username) {
        try {
            String subject = "Welcome to Course Recovery System";
            String body = "Dear " + username + ",\n\n" +
                    "Welcome to the Course Recovery System! Your account has been successfully created.\n\n" +
                    "Best regards,\n" +
                    "Course Recovery System Team";
            EmailUtil.sendEmail(email, subject, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendDeactivationEmail(String email, String username) {
        try {
            String subject = "Account Deactivated";
            String body = "Dear " + username + ",\n\n" +
                    "Your account has been deactivated. If you believe this is an error, please contact the administrator.\n\n" +
                    "Best regards,\n" +
                    "Course Recovery System Team";
            EmailUtil.sendEmail(email, subject, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPasswordResetEmail(String email, String username) {
        try {
            String subject = "Password Reset Successful";
            String body = "Dear " + username + ",\n\n" +
                    "Your password has been successfully reset.\n\n" +
                    "Best regards,\n" +
                    "Course Recovery System Team";
            EmailUtil.sendEmail(email, subject, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPasswordResetRequestEmail(String email, String username, String token) {
        try {
            String resetLink = "http://localhost:8080/crs/resetPassword.jsp?token=" + token;
            String subject = "Password Reset Request";
            String body = "Dear " + username + ",\n\n" +
                    "You have requested to reset your password. Please click the link below to reset your password:\n\n" +
                    resetLink + "\n\n" +
                    "This link will expire in 1 hour.\n\n" +
                    "If you did not request this password reset, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "Course Recovery System Team";
            EmailUtil.sendEmail(email, subject, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPasswordResetConfirmationEmail(String email, String username) {
        try {
            String subject = "Password Reset Successful";
            String body = "Dear " + username + ",\n\n" +
                    "Your password has been successfully reset. You can now log in with your new password.\n\n" +
                    "Best regards,\n" +
                    "Course Recovery System Team";
            EmailUtil.sendEmail(email, subject, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
