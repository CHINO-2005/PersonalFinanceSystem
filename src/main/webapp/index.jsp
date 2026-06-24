<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="th">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>เข้าสู่ระบบ - ระบบจัดการการเงินส่วนบุคคล</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; }
        .login-container { max-width: 450px; margin-top: 10%; }
    </style>
</head>
<body>

<div class="container d-flex justify-content-center">
    <div class="card shadow login-container w-100 p-4 rounded-3">
        <h3 class="text-center mb-4 text-primary fw-bold">Personal Finance System</h3>
        <p class="text-muted text-center small">เข้าสู่ระบบเพื่อจัดการรายรับ-รายจ่ายของคุณ</p>
        <hr>

        <%-- แสดงข้อความเมื่อเกิด Error --%>
        <% if (request.getAttribute("errorMessage") != null) { %>
        <div class="alert alert-danger text-center py-2 small" role="alert">
            <%= request.getAttribute("errorMessage") %>
        </div>
        <% } %>

        <form action="login" method="POST">
            <div class="mb-3">
                <label for="username" class="form-label font-weight-bold">ชื่อผู้ใช้ (Username)</label>
                <input type="text" class="form-gradient form-control" id="username" name="username" placeholder="กรอกชื่อผู้ใช้" required>
            </div>
            <div class="mb-4">
                <label for="password" class="form-label">รหัสผ่าน (Password)</label>
                <input type="password" class="form-control" id="password" name="password" placeholder="กรอกรหัสผ่าน" required>
            </div>
            <button type="submit" class="btn btn-primary w-100 py-2 fw-bold rounded-2">เข้าสู่ระบบ</button>
        </form>
    </div>
</div>

<div class="text-center mt-3">
    <a href="register.jsp" class="text-decoration-none">ยังไม่มีบัญชี? สมัครสมาชิกที่นี่</a>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>