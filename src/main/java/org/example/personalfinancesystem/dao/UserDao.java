package org.example.personalfinancesystem.dao;

import org.example.personalfinancesystem.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    // เมธอดสำหรับตรวจสอบการล็อกอิน
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password); // ในระบบจริงควรแฮชรหัสผ่าน แต่เวอร์ชันการศึกษาใช้แบบนี้ก่อนได้ครับ
            rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                return user; // พบบัญชีผู้ใช้และรหัสผ่านถูกต้อง
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // ปิดการเชื่อมต่อเพื่อไม่ให้เกิด Resource Leak
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBUtils.closeConnection(conn);
        }
        return null; // ล็อกอินไม่สำเร็จ
    }

    // เมธอดสำหรับสมัครสมาชิกใหม่
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword()); // ในระบบจริงควรเข้ารหัสผ่านก่อนบันทึก
            ps.setString(3, user.getEmail());

            int result = ps.executeUpdate();
            return result > 0; // ถ้าบันทึกสำเร็จจะส่งคืน true
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBUtils.closeConnection(conn);
        }
        return false;
    }
}