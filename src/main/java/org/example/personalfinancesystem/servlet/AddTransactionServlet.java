package org.example.personalfinancesystem.servlet;

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
import java.sql.Date;
import java.util.List;

@WebServlet("/addTransaction")
public class AddTransactionServlet extends HttpServlet {
    private TransactionDao transactionDao = new TransactionDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        // 1. รับค่าจากฟอร์มหน้าเว็บ
        int userId = user.getUserId();
        String type = request.getParameter("type");
        double amount = Double.parseDouble(request.getParameter("amount"));
        String category = request.getParameter("category");
        Date date = Date.valueOf(request.getParameter("date"));

        // 2. สร้างออบเจกต์และบันทึกลง MySQL
        Transaction t = new Transaction();
        t.setUserId(userId);
        t.setType(type);
        t.setAmount(amount);
        t.setCategory(category);
        t.setDate(date);

        transactionDao.addTransaction(t);

        // 3. คำนวณยอดเงินรวมและอัปเดตระบบคำนวณอัจฉริยะใหม่ทันที
        double totalIncome = transactionDao.getTotalAmountByType(userId, "income");
        double totalExpense = transactionDao.getTotalAmountByType(userId, "expense");
        double balance = totalIncome - totalExpense;
        List<Transaction> transactionList = transactionDao.getTransactionsByUserId(userId);

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
                savingsAdvice = "statusClass";
                savingsAdvice = "สถานะปกติ: คุณใช้จ่ายไป " + String.format("%.1f", expenseRatio) + "% ของรายได้ ควรเริ่มวางแผนตัดค่าใช้จ่ายที่ไม่จำเป็นออกเพื่อเพิ่มเงินออม";
                statusClass = "text-warning";
            } else {
                savingsAdvice = "แจ้งเตือนความเสี่ยง! คุณใช้จ่ายสูงถึง " + String.format("%.1f", expenseRatio) + "% ของรายได้ เสี่ยงต่อสภาวะเงินช็อต ควรหยุดค่าใช้จ่ายฟุ่มเฟือยทันที";
                statusClass = "text-danger";
            }
        }

        // 4. อัปเดตค่าใหม่ลงใน Session
        session.setAttribute("totalIncome", totalIncome);
        session.setAttribute("totalExpense", totalExpense);
        session.setAttribute("balance", balance);
        session.setAttribute("savingsAdvice", savingsAdvice);
        session.setAttribute("statusClass", statusClass);
        session.setAttribute("transactions", transactionList);

        // รีเฟรชกลับมาที่หน้าแดชบอร์ดเพื่อแสดงผลลัพธ์ล่าสุด
        response.sendRedirect("dashboard.jsp");
    }
}