<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fundora - Sign In / Persona Switcher</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
    <style>
        .login-container {
            max-width: 900px;
            margin: 3rem auto;
            padding: 0 1.5rem;
        }
        .login-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 2rem;
            align-items: start;
        }
        @media (max-width: 768px) {
            .login-grid {
                grid-template-columns: 1fr;
            }
        }
        .persona-card {
            background: var(--bg-card-alt);
            border: 1px solid var(--border-color);
            border-radius: 10px;
            padding: 1rem;
            margin-bottom: 0.75rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            cursor: pointer;
            transition: all 0.2s;
        }
        .persona-card:hover {
            border-color: var(--primary);
            transform: translateX(4px);
            background: rgba(79, 70, 229, 0.15);
        }
        .persona-name {
            font-weight: 600;
            color: #fff;
            display: block;
        }
        .persona-meta {
            font-size: 0.8rem;
            color: var(--text-muted);
        }
        .role-badge {
            background: var(--primary);
            color: #fff;
            font-size: 0.7rem;
            padding: 0.2rem 0.5rem;
            border-radius: 4px;
            font-weight: 600;
        }
        .role-badge.manager {
            background: #f59e0b;
        }
        .alert-box {
            padding: 0.85rem 1rem;
            border-radius: 8px;
            margin-bottom: 1.25rem;
            font-size: 0.9rem;
        }
        .alert-danger {
            background: rgba(239, 68, 68, 0.15);
            border: 1px solid var(--danger);
            color: #fca5a5;
        }
        .alert-success {
            background: rgba(16, 185, 129, 0.15);
            border: 1px solid var(--accent);
            color: #6ee7b7;
        }
    </style>
</head>
<body>

    <header class="navbar">
        <a href="${pageContext.request.contextPath}/login" class="brand">
            <span>✨ Fundora</span>
            <span class="brand-badge">Authentication</span>
        </a>
    </header>

    <div class="login-container">
        
        <c:if test="${not empty errorMessage}">
            <div class="alert-box alert-danger">
                ⚠️ <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <c:if test="${param.loggedOut == 'true'}">
            <div class="alert-box alert-success">
                👋 You have been logged out safely. Select a profile to sign back in!
            </div>
        </c:if>

        <div class="login-grid">
            
            <!-- Left Panel: 1-Click Persona Quick Switcher -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">⚡ 1-Click Demo Profiles</h2>
                    <span style="font-size: 0.8rem; color: var(--text-muted);">Quick Sign-In</span>
                </div>
                <p style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 1.25rem;">
                    Select any student flatmate or hostel manager persona to instantly test peer debt splits and smart nudges:
                </p>

                <form action="${pageContext.request.contextPath}/login" method="POST" id="quickLoginForm">
                    <input type="hidden" name="quickUserId" id="selectedQuickUserId" value=""/>
                    <c:forEach var="usr" items="${demoUsers}">
                        <div class="persona-card" onclick="submitQuickLogin('${usr.id}')">
                            <div>
                                <span class="persona-name">👤 <c:out value="${usr.name}"/></span>
                                <span class="persona-meta">UPI: <c:out value="${usr.upiId}"/> | Wallet: ₹<fmt:formatNumber value="${usr.walletBalance}" type="number" minFractionDigits="2"/></span>
                            </div>
                            <div>
                                <span class="role-badge ${usr.role == 'HOSTEL_MANAGER' ? 'manager' : ''}">
                                    <c:out value="${usr.role}"/>
                                </span>
                            </div>
                        </div>
                    </c:forEach>
                </form>
            </div>

            <!-- Right Panel: Standard Credentials Form -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">🔐 Sign In with ID / Phone</h2>
                </div>
                <form action="${pageContext.request.contextPath}/login" method="POST">
                    <div class="form-group">
                        <label class="form-label">Email Address or Registered Phone</label>
                        <input type="text" name="emailOrPhone" class="form-control" placeholder="e.g. aarav@fundora.app or 9820112233" required/>
                    </div>

                    <div class="form-group">
                        <label class="form-label">UPI App Passcode / PIN</label>
                        <input type="password" name="pin" class="form-control" placeholder="••••" value="1234"/>
                        <span style="font-size: 0.75rem; color: var(--text-muted);">Default demo passcode: 1234</span>
                    </div>

                    <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 1rem; padding: 0.75rem;">
                        Sign In to Fundora
                    </button>
                </form>

                <div style="margin-top: 2rem; border-top: 1px dashed var(--border-color); padding-top: 1rem;">
                    <h4 style="color: #fff; font-size: 0.9rem; margin-bottom: 0.5rem;">🎓 Mumbai University Tech Stack:</h4>
                    <p style="color: var(--text-muted); font-size: 0.8rem; line-height: 1.5;">
                        Demonstrates <strong>Module III (HttpServlet Session State & Filter API)</strong> with secure Session attributes and cookie management.
                    </p>
                </div>
            </div>

        </div>

    </div>

    <script>
        function submitQuickLogin(userId) {
            document.getElementById('selectedQuickUserId').value = userId;
            document.getElementById('quickLoginForm').submit();
        }
    </script>
</body>
</html>
