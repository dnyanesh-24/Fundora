<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fundora - Social FinTech for Flatmates & Trips</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
</head>
<body>

    <header class="navbar">
        <a href="${pageContext.request.contextPath}/dashboard" class="brand">
            <span>✨ Fundora</span>
            <span class="brand-badge">Mumbai University Full-Stack Java</span>
        </a>
    </header>

    <div class="container" style="text-align: center; margin-top: 4rem;">
        <div class="card" style="max-width: 650px; margin: 0 auto; padding: 2.5rem;">
            <h1 style="font-size: 2.2rem; color: #fff; margin-bottom: 1rem;">✨ Welcome to Fundora</h1>
            <p style="color: var(--text-muted); font-size: 1.1rem; margin-bottom: 2rem;">
                The modern social FinTech platform for college students, flatmates, and youth circles. 
                Seamlessly split monthly rent, groceries, trip expenses, send polite smart reminders, and settle debts with direct UPI.
            </p>

            <div style="background: var(--bg-card-alt); padding: 1.25rem; border-radius: 8px; margin-bottom: 2rem; text-align: left;">
                <h3 style="color: var(--accent); margin-bottom: 0.5rem;">🎓 Mumbai University Syllabus Coverage:</h3>
                <ul style="color: var(--text-muted); font-size: 0.9rem; padding-left: 1.25rem; line-height: 1.8;">
                    <li><strong>Module I:</strong> OOP Principles, Encapsulation, Method Overloading</li>
                    <li><strong>Module II:</strong> Interfaces (ExpenseSplitter), Custom Exceptions</li>
                    <li><strong>Module III:</strong> JDBC (DriverManager, PreparedStatement), Servlets, Filters, JSP, JSTL</li>
                    <li><strong>Module IV:</strong> JavaScript DOM Manipulation, NodeIterator, Fetch API</li>
                    <li><strong>Module V:</strong> MVC Pattern & React.js SPA Savings Tracker</li>
                    <li><strong>Module VI:</strong> Spring Boot REST APIs, IoC & Dependency Injection</li>
                </ul>
            </div>

            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary" style="font-size: 1.1rem; padding: 0.85rem 2rem;">
                🚀 Launch Fundora Dashboard
            </a>
        </div>
    </div>

</body>
</html>
