package com.crs.dao;

import com.crs.model.User;
import com.crs.util.DBConnect;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final String INSERT_USER = "INSERT INTO Users (username, password, role, email, status) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE Users SET username = ?, password = ?, role = ?, email = ?, status = ? WHERE id = ?";
    private static final String DELETE_USER = "UPDATE Users SET status = 'inactive' WHERE id = ?";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM Users WHERE id = ?";
    private static final String SELECT_USER_BY_USERNAME = "SELECT * FROM Users WHERE username = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM Users";

    public static void addUser(User user) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getStatus());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
        }
    }

    public static void updateUser(User user) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_USER)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getStatus());
            ps.setInt(6, user.getId());
            ps.executeUpdate();
        }
    }

    public static void deleteUser(int userId) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_USER)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public static User getUserById(int userId) throws SQLException, IOException {
        User user = null;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_USER_BY_ID)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        }
        return user;
    }

    public static User getUserByUsername(String username) throws SQLException, IOException {
        User user = null;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_USER_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        }
        return user;
    }

    public static List<User> getAllUsers() throws SQLException, IOException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_USERS)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    private static User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setEmail(rs.getString("email"));
        user.setStatus(rs.getString("status"));
        return user;
    }
}
