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

@WebServlet("/selectMonth")
public class SelectMonthServlet extends HttpServlet {
    private TransactionDao transactionDao = new TransactionDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String filterValue = request.getParameter("monthFilter"); // ค่าที่ส่งมาจาก HTML <input type="month"> จะเป็นรูปแบบ "YYYY-MM"
        int userId = user.getUserId();

        double totalIncome = 0;
        double totalExpense = 0;
        List<Transaction> transactionList;

        if (filterValue != null && !filterValue.isEmpty()) {
            // กรณีมีการเลือกเดือน: แยกปีและเดือนออกจากกัน เช่น "2026-01" -> ปี 2026, เดือน 1
            String[] parts = filterValue.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            totalIncome = transactionDao.getTotalAmountByMonth(userId, "income", month, year);
            totalExpense = transactionDao.getTotalAmountByMonth(userId, "expense", month, year);
            transactionList = transactionDao.getTransactionsByMonth(userId, month, year);

            session.setAttribute("selectedMonth", filterValue); // ล็อกค่าเดือนที่เลือกไว้ในกล่องอินพุตบนหน้าจอ
        } else {
            // กรณีไม่ได้เลือกเดือน (หรือกดปุ่มดูทั้งหมด): ดึงข้อมูลทั้งหมดตามปกติ
            totalIncome = transactionDao.getTotalAmountByType(userId, "income");
            totalExpense = transactionDao.getTotalAmountByType(userId, "expense");
            transactionList = transactionDao.getTransactionsByUserId(userId);

            session.removeAttribute("selectedMonth"); // ล้างค่าในช่องเลือกเดือน
        }

        // คำนวณยอดคงเหลือ
        double balance = totalIncome - totalExpense;

        // คำนวณคำแนะนำการออมอัจฉริยะตามสัดส่วนเงินของเดือนนั้น ๆ
        String savingsAdvice;
        String statusClass;

        if (totalIncome == 0) {
            savingsAdvice = "本月暂无收入数据，或请开始记录收入以获取智能储蓄建议！";
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

        // อัปเดตข้อมูลทั้งหมดลงใน Session เพื่อให้หน้า JSP ดึงไปแสดงผลแบบไดนามิก
        session.setAttribute("totalIncome", totalIncome);
        session.setAttribute("totalExpense", totalExpense);
        session.setAttribute("balance", balance);
        session.setAttribute("transactions", transactionList);
        session.setAttribute("savingsAdvice", savingsAdvice);
        session.setAttribute("statusClass", statusClass);

        response.sendRedirect("dashboard.jsp");
    }
}