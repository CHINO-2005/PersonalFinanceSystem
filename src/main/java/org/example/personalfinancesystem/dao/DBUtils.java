package org.example.personalfinancesystem.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {
    // แก้ไขจาก serverTimezone=UTC เป็น serverTimezone=Asia/Bangkok เพื่อให้ฐานข้อมูลใช้เวลาปัจจุบันของประเทศไทย
    private static final String URL = "jdbc:mysql://localhost:3306/personal_finance_db?useSSL=false&serverTimezone=Asia/Bangkok&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Jino12345";

    static {
        try {
            // โหลด MySQL Driver เวอร์ชัน 9.x
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // เมธอดสำหรับเรียกเปิดการเชื่อมต่อ
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // เมธอดสำหรับช่วยปิดการเชื่อมต่อที่ใช้งานเสร็จแล้ว
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}