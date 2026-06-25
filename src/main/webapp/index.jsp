<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录 - 个人财务管理系统</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; }
        .login-container { max-width: 450px; margin-top: 10%; }
    </style>
</head>
<body>

<div class="container d-flex justify-content-center">
    <div class="card shadow login-container w-100 p-4 rounded-3">
        <h3 class="text-center mb-4 text-primary fw-bold">个人财务系统</h3>
        <p class="text-muted text-center small">登录以管理您的收入和支出</p>
        <hr>

        <%-- แสดงข้อความเมื่อเกิด Error --%>
        <% if (request.getAttribute("errorMessage") != null) { %>
        <div class="alert alert-danger text-center py-2 small" role="alert">
            <%= request.getAttribute("errorMessage") %>
        </div>
        <% } %>

        <form action="login" method="POST">
            <div class="mb-3">
                <label for="username" class="form-label font-weight-bold">用户名 (Username)</label>
                <input type="text" class="form-gradient form-control" id="username" name="username" placeholder="请输入用户名" required>
            </div>
            <div class="mb-4">
                <label for="password" class="form-label">密码 (Password)</label>
                <input type="password" class="form-control" id="password" name="password" placeholder="请输入密码" required>
            </div>
            <button type="submit" class="btn btn-primary w-100 py-2 fw-bold rounded-2">登录</button>
        </form>
    </div>
</div>

<div class="text-center mt-3">
    <a href="register.jsp" class="text-decoration-none">还没有账号？点击这里注册</a>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>