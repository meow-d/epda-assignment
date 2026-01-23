package com.crs.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

public class CSRFUtil {

    private static final String CSRF_TOKEN_ATTR = "csrfToken";
    private static final int TOKEN_LENGTH = 32;

    /**
     * Generates a new CSRF token and stores it in the session
     */
    public static String generateToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        random.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        session.setAttribute(CSRF_TOKEN_ATTR, token);
        return token;
    }

    /**
     * Gets the current CSRF token from the session, generating one if none exists
     */
    public static String getToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String token = (String) session.getAttribute(CSRF_TOKEN_ATTR);
        if (token == null) {
            token = generateToken(request);
        }
        return token;
    }

    /**
     * Validates a CSRF token against the session
     */
    public static boolean validateToken(HttpServletRequest request, String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_ATTR);
        if (sessionToken == null) {
            return false;
        }

        // Use constant-time comparison to prevent timing attacks
        return constantTimeEquals(sessionToken, token);
    }

    /**
     * Constant-time string comparison to prevent timing attacks
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }

        return result == 0;
    }
}