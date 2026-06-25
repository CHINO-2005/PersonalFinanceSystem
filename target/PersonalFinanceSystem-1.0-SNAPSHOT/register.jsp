<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>注册 - 个人财务分析系统</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center justify-content-center" style="height: 100vh;">
<div class="card p-4 shadow-sm" style="width: 400px; border-radius: 15px;">
    <h3 class="text-center fw-bold text-primary mb-3">创建新账户</h3>

    <% if(session.getAttribute("regError") != null) { %>
    <div class="alert alert-danger text-center p-2"><%= session.getAttribute("regError") %></div>
    <% session.removeAttribute("regError"); %>
    <% } %>

    <form action="register" method="POST">
        <div class="mb-3">
            <label class="form-label">用户名 (Username)</label>
            <input type="text" class="form-control" name="username" required>
        </div>
        <div class="mb-3">
            <label class="form-label">密码 (Password)</label>
            <input type="password" class="form-control" name="password" required>
        </div>
        <div class="mb-3">
            <label class="form-label">电子邮箱 (Email)</label>
            <input type="email" class="form-control" name="email" required>
        </div>
        <button type="submit" class="btn btn-primary w-100 fw-bold mb-3">注册</button>
        <div class="text-center">
            <a href="index.jsp" class="text-decoration-none small">已有账户？立即登录</a>
        </div>
    </form>
</div>
</body>
</html>