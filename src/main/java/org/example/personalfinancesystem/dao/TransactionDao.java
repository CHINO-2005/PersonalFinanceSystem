package org.example.personalfinancesystem.dao;

import org.example.personalfinancesystem.model.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {

    // 1. เมธอดสำหรับบันทึกรายรับ-รายจ่าย (MySQL จะแสตมป์เวลา created_at ให้เองอัตโนมัติจากโครงสร้าง DEFAULT CURRENT_TIMESTAMP)
    public boolean addTransaction(Transaction t) {
        String sql = "INSERT INTO transactions (user_id, type, amount, category, date) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, t.getUserId());
            ps.setString(2, t.getType());
            ps.setDouble(3, t.getAmount());
            ps.setString(4, t.getCategory());
            ps.setDate(5, t.getDate());

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBUtils.closeConnection(conn);
        }
        return false;
    }

    // 2. เมธอดสำหรับดึงรายการเงินทั้งหมดของผู้ใช้คนนั้น ๆ มาแสดงผล (แก้ไขเพิ่มการดึงเวลาบันทึกระบบ)
    public List<Transaction> getTransactionsByUserId(int userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC, created_at DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Transaction t = new Transaction();
                t.setId(rs.getInt("id"));
                t.setUserId(rs.getInt("user_id"));
                t.setType(rs.getString("type"));
                t.setAmount(rs.getDouble("amount"));
                t.setCategory(rs.getString("category"));
                t.setDate(rs.getDate("date"));
                t.setCreatedAt(rs.getTimestamp("created_at")); // ดึงค่าเวลาบันทึกระบบเข้ามา
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBUtils.closeConnection(conn);
        }
        return list;
    }

    // 3. เมธอดสำหรับดึงยอดรวมรายรับ หรือ รายจ่าย แยกตามประเภทของผู้ใช้คนนั้น ๆ
    public double getTotalAmountByType(int userId, String type) {
        String sql = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND type = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, type);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1); // ส่งคืนยอดรวม (ถ้าไม่มีข้อมูลจะคืนค่าเป็น 0.0)
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (ps != null) try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBUtils.closeConnection(conn);
        }
        return 0.0;
    }

    // 4. เมธอดสำหรับลบรายการธุรกรรม
    public boolean deleteTransaction(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBUtils.closeConnection(conn);
        }
        return false;
    }

    // 5. ดึงรายการธุรกรรมเฉพาะเดือนและปีที่ระบุ (แก้ไขเพิ่มการดึงเวลาบันทึกระบบ)
    public List<Transaction> getTransactionsByMonth(int userId, int month, int year) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id = ? AND MONTH(date) = ? AND YEAR(date) = ? ORDER BY date DESC, created_at DESC";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction();
                    t.setId(rs.getInt("id"));
                    t.setUserId(rs.getInt("user_id"));
                    t.setDate(rs.getDate("date"));
                    t.setType(rs.getString("type"));
                    t.setCategory(rs.getString("category"));
                    t.setAmount(rs.getDouble("amount"));
                    t.setCreatedAt(rs.getTimestamp("created_at")); // ดึงค่าเวลาบันทึกระบบเข้ามา
                    list.add(t);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 6. คำนวณยอดรวม (income หรือ expense) เฉพาะเดือนและปีที่ระบุ
    public double getTotalAmountByMonth(int userId, String type, int month, int year) {
        String sql = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND type = ? AND MONTH(date) = ? AND YEAR(date) = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setInt(3, month);
            ps.setInt(4, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}