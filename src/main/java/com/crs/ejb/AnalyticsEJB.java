package com.crs.ejb;

import com.crs.dao.AnalyticsDAO;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class AnalyticsEJB {

    /**
     * Gets comprehensive system analytics for dashboard
     */
    public Map<String, Object> getSystemAnalytics() throws SQLException, IOException {
        return AnalyticsDAO.getSystemAnalytics();
    }

    /**
     * Gets academic performance analytics
     */
    public Map<String, Object> getAcademicAnalytics() throws SQLException, IOException {
        return AnalyticsDAO.getAcademicAnalytics();
    }

    /**
     * Gets recovery success metrics
     */
    public Map<String, Object> getRecoveryMetrics() throws SQLException, IOException {
        Map<String, Object> metrics = AnalyticsDAO.getSystemAnalytics();
        // Filter to only recovery-related metrics
        Map<String, Object> recoveryMetrics = new java.util.HashMap<>();
        recoveryMetrics.put("activeRecoveryPlans", metrics.get("activeRecoveryPlans"));
        recoveryMetrics.put("completedRecoveryPlans", metrics.get("completedRecoveryPlans"));
        recoveryMetrics.put("recoverySuccessRate", metrics.get("recoverySuccessRate"));
        recoveryMetrics.put("recentRecoveryPlans", metrics.get("recentRecoveryPlans"));
        return recoveryMetrics;
    }
}