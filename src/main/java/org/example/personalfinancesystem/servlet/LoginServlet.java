package org.example.personalfinancesystem.servlet;

import org.example.personalfinancesystem.dao.UserDao;
import org.example.personalfinancesystem.dao.TransactionDao;
import org.example.personalfinancesystem.model.User;
import org.example.personalfinancesystem.model.Transaction;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDao userDao = new UserDao();
    private TransactionDao transactionDao = new TransactionDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // ตรวจสอบข้อมูลจาก Database
        User user = userDao.login(username, password);

        if (user != null) {
            // ล็อกอินสำเร็จ: ดึงข้อมูลยอดรวมเพื่อใช้คำนวณเงื่อนไขระบบออมเงินอัจฉริยะ
            int userId = user.getUserId();
            double totalIncome = transactionDao.getTotalAmountByType(userId, "income");
            double totalExpense = transactionDao.getTotalAmountByType(userId, "expense");
            double balance = totalIncome - totalExpense;
            List<Transaction> transactionList = transactionDao.getTransactionsByUserId(userId);

            // คำนวณสถานะและคำแนะนำการออมอัจฉริยะ
            String savingsAdvice;
            String statusClass;
            if (totalIncome == 0) {
                savingsAdvice = "请开始记录您的收入和支出，以获取智能储蓄建议！";
                statusClass = "text-muted";
            } else {
                double expenseRatio = (totalExpense / totalIncome) * 100;
                if (expenseRatio <= 50) {
                    savingsAdvice = "太棒了！您的支出仅占收入的 " + String.format("%.1f", expenseRatio) + "%。达到了储蓄目标，财务状况非常良好。";
                    statusClass = "text-success";
                } else if (expenseRatio <= 80) {
                    savingsAdvice = "状态正常：您的支出占收入的 " + String.format("%.1f", expenseRatio) + "%。建议开始削减不必要的开支，以增加储蓄。";
                    statusClass = "text-warning";
                } else {
                    savingsAdvice = "风险预警！您的支出高达收入的 " + String.format("%.1f", expenseRatio) + "%，面临资金短缺的风险。请立即停止非必要消费。";
                    statusClass = "text-danger";
                }
            }

            // บันทึกค่าลงใน Session เพื่อส่งต่อไปแสดงผลที่หน้า Dashboard (JSP)
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user);
            session.setAttribute("totalIncome", totalIncome);
            session.setAttribute("totalExpense", totalExpense);
            session.setAttribute("balance", balance);
            session.setAttribute("savingsAdvice", savingsAdvice);
            session.setAttribute("statusClass", statusClass);
            session.setAttribute("transactions", transactionList);

            // ส่งผู้ใช้ไปที่หน้าแดชบอร์ดหลัก
            response.sendRedirect("dashboard.jsp");
        } else {
            // ล็อกอินไม่สำเร็จ: ส่งกลับหน้าแรกพร้อมข้อความแจ้งเตือน
            request.setAttribute("errorMessage", "用户名或密码不正确");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}