package org.example.personalfinancesystem.model;

import java.sql.Date;
import java.sql.Timestamp; // นำเข้าไลบรารีสำหรับเก็บวันที่และเวลา (Timestamp)

public class Transaction {
    private int id;
    private int userId;
    private String type; // 'income' หรือ 'expense'
    private double amount;
    private String category;
    private Date date;
    private Timestamp createdAt; // เพิ่มฟิลด์สำหรับเก็บเวลาที่บันทึกระบบย้อนหลัง

    // Constructor ไม่มีพารามิเตอร์
    public Transaction() {}

    // Constructor สำหรับนำไปใช้งานตอนรับค่าหรือบันทึกข้อมูล
    public Transaction(int id, int userId, String type, double amount, String category, Date date, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.createdAt = createdAt;
    }

    // Getter and Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    // เพิ่ม Getter และ Setter สำหรับ createdAt
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}