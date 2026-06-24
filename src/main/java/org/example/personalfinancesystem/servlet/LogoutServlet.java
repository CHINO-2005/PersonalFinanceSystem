package org.example.personalfinancesystem.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // สั่งทำลาย Session เพื่อล้างข้อมูลของผู้ใช้ทั้งหมด
        }
        // ดีดกลับไปที่หน้า index.jsp (หน้าล็อกอินหลัก)
        response.sendRedirect("index.jsp");
    }
}