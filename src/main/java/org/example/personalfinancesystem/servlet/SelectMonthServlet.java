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
            savingsAdvice = "ไม่มีข้อมูลรายรับในเดือนนี้ หรือเริ่มต้นบันทึกรายรับเพื่อรับคำแนะนำการออมอัจฉริยะ!";
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