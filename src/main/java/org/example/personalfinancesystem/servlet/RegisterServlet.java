package org.example.personalfinancesystem.servlet;

import org.example.personalfinancesystem.dao.UserDao;
import org.example.personalfinancesystem.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        HttpSession session = request.getSession();

        // สร้างออบเจกต์ User ใหม่
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);

        // ทำการบันทึกลง MySQL
        boolean success = userDao.registerUser(newUser);

        if (success) {
            // สมัครสำเร็จ ส่งไปหน้าล็อกอินหลัก
            session.setAttribute("regSuccess", "สมัครสมาชิกสำเร็จ! กรุณาเข้าสู่ระบบ");
            response.sendRedirect("index.jsp");
        } else {
            // สมัครล้มเหลว (เช่น ชื่อซ้ำ) ส่งกลับไปหน้าเดิมพร้อมข้อความเตือน
            session.setAttribute("regError", "ไม่สามารถสมัครได้ ชื่อผู้ใช้นี้อาจถูกใช้ไปแล้ว");
            response.sendRedirect("register.jsp");
        }
    }
}