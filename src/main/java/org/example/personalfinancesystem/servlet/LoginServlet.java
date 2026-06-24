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
                savingsAdvice = "เริ่มต้นบันทึกรายรับ-รายจ่ายของคุณ เพื่อรับคำแนะนำการออมอัจฉริยะ!";
                statusClass = "text-muted";
            } else {
                double expenseRatio = (totalExpense / totalIncome) * 100;
                if (expenseRatio <= 50) {
                    savingsAdvice = "ยอดเยี่ยมมาก! คุณใช้จ่ายไปเพียง " + String.format("%.1f", expenseRatio) + "% ของรายได้ มีเงินเก็บตามเป้าหมาย สภาพคล่องทางการเงินดีเยี่ยม";
                    statusClass = "text-success";
                } else if (expenseRatio <= 80) {
                    savingsAdvice = "สถานะปกติ: คุณใช้จ่ายไป " + String.format("%.1f", expenseRatio) + "% ของรายได้ ควรเริ่มวางแผนตัดค่าใช้จ่ายที่ไม่จำเป็นออกเพื่อเพิ่มเงินออม";
                    statusClass = "text-warning";
                } else {
                    savingsAdvice = "แจ้งเตือนความเสี่ยง! คุณใช้จ่ายสูงถึง " + String.format("%.1f", expenseRatio) + "% ของรายได้ เสี่ยงต่อสภาวะเงินช็อต ควรหยุดค่าใช้จ่ายฟุ่มเฟือยทันที";
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
            request.setAttribute("errorMessage", "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}