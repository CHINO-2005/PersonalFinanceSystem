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