<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.example.personalfinancesystem.model.User" %>
<%@ page import="org.example.personalfinancesystem.model.Transaction" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%
    // ตรวจสอบความปลอดภัย: ถ้าไม่มี Session หรือยังไม่ได้ล็อกอิน ให้เด้งกลับหน้าแรก
    User user = (User) session.getAttribute("currentUser");
    if (user == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    // กำหนดค่าเริ่มต้นเพื่อความปลอดภัย ป้องกันค่า Null ตอนกรองข้อมูล
    double totalIncome = session.getAttribute("totalIncome") != null ? (Double) session.getAttribute("totalIncome") : 0.0;
    double totalExpense = session.getAttribute("totalExpense") != null ? (Double) session.getAttribute("totalExpense") : 0.0;
    double balance = session.getAttribute("balance") != null ? (Double) session.getAttribute("balance") : 0.0;
    String savingsAdvice = session.getAttribute("savingsAdvice") != null ? (String) session.getAttribute("savingsAdvice") : "ไม่มีข้อมูลรายรับในเดือนนี้ หรือเริ่มต้นบันทึกรายรับเพื่อรับคำแนะนำการออมอัจฉริยะ!";
    String statusClass = session.getAttribute("statusClass") != null ? (String) session.getAttribute("statusClass") : "text-muted";
%>
<!DOCTYPE html>
<html lang="th">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>แผงควบคุมระบบการเงิน - แดชบอร์ด</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="#"><i class="bi bi-wallet2 me-2"></i>ระบบวิเคราะห์การเงินอัจฉริยะ</a>
        <div class="text-end text-white">
            ยินดีต้อนรับ, <span class="fw-bold text-warning">${currentUser.username}</span>
            <a href="logout" class="btn btn-sm btn-outline-light ms-3 fw-bold" onclick="return confirm('คุณต้องการออกจากระบบใช่หรือไม่?')">
                <i class="bi bi-box-arrow-right"></i> ออกจากระบบ
            </a>
        </div>
    </div>
</nav>

<div class="container my-5">

    <div class="card p-4 shadow-sm border-0 rounded-3 mb-4 bg-white">
        <div class="card-body">
            <h5 class="card-title"><i class="bi bi-lightbulb-fill text-warning"></i> ระบบแนะนำการออมอัจฉริยะ</h5>
            <p class="card-text fw-bold ${not empty sessionScope.statusClass ? sessionScope.statusClass : statusClass} mt-3">
                ${not empty sessionScope.savingsAdvice ? sessionScope.savingsAdvice : savingsAdvice}
            </p>
        </div>
    </div>

    <div class="row g-4 mb-5">
        <div class="col-md-4">
            <div class="card p-3 shadow-sm border-start border-success border-4 rounded-3 bg-white">
                <span class="text-muted small text-uppercase fw-bold">รายรับรวม</span>
                <h2 class="text-success fw-bold mt-1">
                    +<fmt:formatNumber value="${not empty sessionScope.totalIncome ? sessionScope.totalIncome : totalIncome}" pattern="#,##0.00"/> บาท
                </h2>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card p-3 shadow-sm border-start border-danger border-4 rounded-3 bg-white">
                <span class="text-muted small text-uppercase fw-bold">รายจ่ายรวม</span>
                <h2 class="text-danger fw-bold mt-1">
                    -<fmt:formatNumber value="${not empty sessionScope.totalExpense ? sessionScope.totalExpense : totalExpense}" pattern="#,##0.00"/> บาท
                </h2>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card p-3 shadow-sm border-start border-primary border-4 rounded-3 bg-white">
                <span class="text-muted small text-uppercase fw-bold">ยอดเงินคงเหลือ</span>
                <h2 class="text-primary fw-bold mt-1">
                    <fmt:formatNumber value="${not empty sessionScope.balance ? sessionScope.balance : balance}" pattern="#,##0.00"/> บาท
                </h2>
            </div>
        </div>
    </div>

    <div class="card p-4 shadow-sm border-0 rounded-3 mb-4 bg-white">
        <h5 class="fw-bold text-dark mb-3"><i class="bi bi-plus-circle-fill text-primary me-2"></i>บันทึกรายการเงินใหม่</h5>
        <form action="addTransaction" method="POST" class="row g-3">
            <div class="col-md-3">
                <label class="form-label">วันที่</label>
                <input type="date" class="form-control" name="date" required>
            </div>
            <div class="col-md-2">
                <label class="form-label">ประเภท</label>
                <select class="form-select" name="type" required>
                    <option value="income">รายรับ</option>
                    <option value="expense">รายจ่าย</option>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label">หมวดหมู่</label>
                <input type="text" class="form-control" name="category" placeholder="เช่น เงินเดือน, ค่าอาหาร" required>
            </div>
            <div class="col-md-2">
                <label class="form-label">จำนวนเงิน (บาท)</label>
                <input type="number" step="0.01" class="form-control" name="amount" placeholder="0.00" required>
            </div>
            <div class="col-md-2 d-flex align-items-end">
                <button type="submit" class="btn btn-primary w-100 fw-bold">บันทึกรายการ</button>
            </div>
        </form>
    </div>

    <div class="card p-4 shadow-sm border-0 rounded-3 bg-white">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h4 class="m-0"><i class="bi bi-clock-history"></i> ประวัติรายการล่าสุด</h4>

            <form action="selectMonth" method="GET" class="d-flex align-items-center gap-2">
                <label class="fw-bold text-secondary text-nowrap m-0">เลือกเดือน:</label>
                <input type="month" class="form-control form-control-sm" name="monthFilter" value="${sessionScope.selectedMonth}">
                <button type="submit" class="btn btn-sm btn-primary fw-bold text-nowrap">กรองข้อมูล</button>
                <a href="selectMonth" class="btn btn-sm btn-secondary fw-bold text-nowrap">ดูทั้งหมด</a>
            </form>
        </div>

        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table align-middle">
                    <thead>
                    <tr class="table-dark">
                        <th>วันที่ธุรกรรม</th>
                        <th>เวลาบันทึกระบบ</th>
                        <th>ประเภท</th>
                        <th>หมวดหมู่</th>
                        <th>จำนวนเงิน</th>
                        <th>จัดการ</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="t" items="${sessionScope.transactions != null ? sessionScope.transactions : transactions}">
                        <tr>
                            <td>${t.date}</td>
                            <td>
                                <fmt:formatDate value="${t.createdAt}" pattern="HH:mm:ss น." timeZone="Asia/Bangkok" />
                            </td>
                            <td>
                                <span class="badge ${t.type == 'income' ? 'bg-success' : 'bg-danger'}">
                                        ${t.type == 'income' ? 'รายรับ' : 'รายจ่าย'}
                                </span>
                            </td>
                            <td>${t.category}</td>
                            <td class="${t.type == 'income' ? 'text-success' : 'text-danger'} fw-bold">
                                    ${t.type == 'income' ? '+' : '-'}
                                <fmt:formatNumber value="${t.amount}" pattern="#,##0.00" /> บาท
                            </td>
                            <td>
                                <a href="deleteTransaction?id=${t.id}"
                                   class="btn btn-sm btn-danger fw-bold"
                                   onclick="return confirm('คุณแน่ใจใช่ไหมที่จะลบรายการนี้?')">
                                    <i class="bi bi-trash-fill"></i> ลบ
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>