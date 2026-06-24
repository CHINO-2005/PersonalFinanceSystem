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
import java.util.List;

@WebServlet("/deleteTransaction")
public class DeleteTransactionServlet extends HttpServlet {
    private TransactionDao transactionDao = new TransactionDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        // 1. รับค่า id จากปุ่มลบที่กดมา
        int transactionId = Integer.parseInt(request.getParameter("id"));
        int userId = user.getUserId();

        // 2. เรียกคำสั่งลบข้อมูลในฐานข้อมูล
        transactionDao.deleteTransaction(transactionId);

        // 3. คำนวณยอดรวมกระเป๋าเงินและอัปเดตระบบคำแนะนำอัจฉริยะใหม่ทันที
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
                savingsAdvice = "สถานะปกติ: คุณใช้จ่ายไป " + String.format("%.1f", expenseRatio) + "% ของรายได้ ควรเริ่มวางแผนตัดค่าใช้จ่ายที่ไม่จำเป็นออกเพื่อเพิ่มเงินออม";
                statusClass = "text-warning";
            } else {
                savingsAdvice = "แจ้งเตือนความเสี่ยง! คุณใช้จ่ายสูงถึง " + String.format("%.1f", expenseRatio) + "% ของรายได้ เสี่ยงต่อสภาวะเงินช็อต ควรหยุดค่าใช้จ่ายฟุ่มเฟือยทันที";
                statusClass = "text-danger";
            }
        }

        // 4. บันทึกค่าที่คำนวณใหม่กลับเข้า Session
        session.setAttribute("totalIncome", totalIncome);
        session.setAttribute("totalExpense", totalExpense);
        session.setAttribute("balance", balance);
        session.setAttribute("savingsAdvice", savingsAdvice);
        session.setAttribute("statusClass", statusClass);
        session.setAttribute("transactions", transactionList);

        // ดีดกลับหน้าแดชบอร์ดเพื่ออัปเดตตัวเลขสด ๆ บนหน้าจอ
        response.sendRedirect("dashboard.jsp");
    }
}