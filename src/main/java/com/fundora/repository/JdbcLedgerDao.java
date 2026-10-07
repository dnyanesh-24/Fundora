package com.fundora.repository;

import com.fundora.config.DatabaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Module III (JDBC Core Syllabus):
 * Raw JDBC Data Access Object demonstrating explicit usage of
 * DriverManager, Connection, PreparedStatement, and ResultSet.
 */
@Repository
public class JdbcLedgerDao {

    @Autowired
    private DatabaseConfig databaseConfig;

    /**
     * Computes raw total paid vs total owed per user in a group using PreparedStatement and ResultSet
     * @param groupId Group identifier
     * @return Map of UserId -> Net balance (positive = owed money, negative = owes money)
     */
    public Map<Long, BigDecimal> calculateRawUserBalances(Long groupId) {
        Map<Long, BigDecimal> netBalances = new HashMap<>();

        String totalPaidSql = 
            "SELECT paid_by_user_id AS user_id, SUM(amount) AS total_paid " +
            "FROM expenses WHERE group_id = ? GROUP BY paid_by_user_id";

        String totalSplitSql = 
            "SELECT es.user_id, SUM(es.share_amount) AS total_owed " +
            "FROM expense_splits es " +
            "JOIN expenses e ON es.expense_id = e.id " +
            "WHERE e.group_id = ? " +
            "GROUP BY es.user_id";

        String settlementPayerSql = 
            "SELECT payer_user_id AS user_id, SUM(amount) AS total_settled_paid " +
            "FROM settlements WHERE group_id = ? AND settlement_status = 'COMPLETED' " +
            "GROUP BY payer_user_id";

        String settlementPayeeSql = 
            "SELECT payee_user_id AS user_id, SUM(amount) AS total_settled_received " +
            "FROM settlements WHERE group_id = ? AND settlement_status = 'COMPLETED' " +
            "GROUP BY payee_user_id";

        try (Connection conn = databaseConfig.getConnection()) {
            
            // 1. Credit: Total amount user paid up front
            try (PreparedStatement psPaid = conn.prepareStatement(totalPaidSql)) {
                psPaid.setLong(1, groupId);
                try (ResultSet rs = psPaid.executeQuery()) {
                    while (rs.next()) {
                        long userId = rs.getLong("user_id");
                        BigDecimal paid = rs.getBigDecimal("total_paid");
                        netBalances.put(userId, netBalances.getOrDefault(userId, BigDecimal.ZERO).add(paid));
                    }
                }
            }

            // 2. Debit: Total share user is obligated to pay
            try (PreparedStatement psSplit = conn.prepareStatement(totalSplitSql)) {
                psSplit.setLong(1, groupId);
                try (ResultSet rs = psSplit.executeQuery()) {
                    while (rs.next()) {
                        long userId = rs.getLong("user_id");
                        BigDecimal owed = rs.getBigDecimal("total_owed");
                        netBalances.put(userId, netBalances.getOrDefault(userId, BigDecimal.ZERO).subtract(owed));
                    }
                }
            }

            // 3. Credit for direct settlements paid
            try (PreparedStatement psSetPaid = conn.prepareStatement(settlementPayerSql)) {
                psSetPaid.setLong(1, groupId);
                try (ResultSet rs = psSetPaid.executeQuery()) {
                    while (rs.next()) {
                        long userId = rs.getLong("user_id");
                        BigDecimal settledPaid = rs.getBigDecimal("total_settled_paid");
                        netBalances.put(userId, netBalances.getOrDefault(userId, BigDecimal.ZERO).add(settledPaid));
                    }
                }
            }

            // 4. Debit for direct settlements received
            try (PreparedStatement psSetRecv = conn.prepareStatement(settlementPayeeSql)) {
                psSetRecv.setLong(1, groupId);
                try (ResultSet rs = psSetRecv.executeQuery()) {
                    while (rs.next()) {
                        long userId = rs.getLong("user_id");
                        BigDecimal settledReceived = rs.getBigDecimal("total_settled_received");
                        netBalances.put(userId, netBalances.getOrDefault(userId, BigDecimal.ZERO).subtract(settledReceived));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return netBalances;
    }
}
