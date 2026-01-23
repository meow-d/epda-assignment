package com.crs.dao;

import com.crs.util.DBConnect;

import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public class PasswordResetDAO {
    private static final String INSERT_TOKEN = "INSERT INTO PasswordResetTokens (user_id, token, expires_at) VALUES (?, ?, ?)";
    private static final String SELECT_TOKEN = "SELECT user_id, expires_at, used FROM PasswordResetTokens WHERE token = ? AND used = FALSE";
    private static final String MARK_TOKEN_USED = "UPDATE PasswordResetTokens SET used = TRUE WHERE token = ?";
    private static final String CLEANUP_EXPIRED = "DELETE FROM PasswordResetTokens WHERE expires_at < NOW()";

    public static String createPasswordResetToken(int userId) throws SQLException, IOException {
        // Generate a secure random token
        String token = UUID.randomUUID().toString() + "-" + System.currentTimeMillis();

        // Token expires in 1 hour
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + (60 * 60 * 1000));

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_TOKEN)) {
            ps.setInt(1, userId);
            ps.setString(2, token);
            ps.setTimestamp(3, expiresAt);
            ps.executeUpdate();
        }

        return token;
    }

    public static Integer validatePasswordResetToken(String token) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_TOKEN)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp expiresAt = rs.getTimestamp("expires_at");
                    if (expiresAt.after(new Timestamp(System.currentTimeMillis()))) {
                        return rs.getInt("user_id");
                    }
                }
            }
        }
        return null; // Token invalid or expired
    }

    public static void markTokenAsUsed(String token) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(MARK_TOKEN_USED)) {
            ps.setString(1, token);
            ps.executeUpdate();
        }
    }

    public static void cleanupExpiredTokens() throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(CLEANUP_EXPIRED)) {
            ps.executeUpdate();
        }
    }
}