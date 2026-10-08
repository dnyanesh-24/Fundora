<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fundora - Detailed Group Ledger</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
</head>
<body>

    <header class="navbar">
        <a href="${pageContext.request.contextPath}/dashboard" class="brand">
            <span>✨ Fundora</span>
            <span class="brand-badge">Audit Ledger</span>
        </a>
        <div style="display: flex; gap: 0.75rem;">
            <a href="${pageContext.request.contextPath}/dashboard?groupId=${selectedGroupId}" class="btn btn-primary">
                ⬅️ Back to Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-nudge" style="text-decoration: none;">
                🚪 Logout
            </a>
        </div>
    </header>

    <div class="container">
        <div class="card">
            <div class="card-header">
                <div>
                    <h1 class="card-title" style="font-size: 1.5rem;"><c:out value="${ledger.group.name}"/></h1>
                    <p style="color: var(--text-muted);"><c:out value="${ledger.group.description}"/></p>
                </div>
                <span class="brand-badge"><c:out value="${ledger.group.groupType}"/></span>
            </div>

            <!-- Net balances summary breakdown -->
            <h3 style="margin-bottom: 1rem; color: #fff;">👥 Individual Net Balance Breakdown</h3>
            <table class="data-table" style="margin-bottom: 2rem;">
                <thead>
                    <tr>
                        <th>Member Name</th>
                        <th>Email / UPI</th>
                        <th>Net Balance Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="member" items="${ledger.group.members}">
                        <c:set var="bal" value="${ledger.netBalances[member.id]}"/>
                        <tr>
                            <td><strong><c:out value="${member.name}"/></strong></td>
                            <td><c:out value="${member.upiId}"/></td>
                            <td style="font-weight: bold; color: ${bal >= 0 ? 'var(--accent)' : 'var(--danger)'};">
                                ${bal >= 0 ? '+₹' : '-₹'}<fmt:formatNumber value="${bal >= 0 ? bal : -bal}" type="number" minFractionDigits="2"/>
                                <span style="font-size: 0.8rem; font-weight: normal; color: var(--text-muted);">
                                    (${bal >= 0 ? 'gets back' : 'owes'})
                                </span>
                            </td>
                            <td>
                                <c:if test="${bal < 0}">
                                    <span style="color: var(--warning); font-size: 0.85rem;">Pending Settlement</span>
                                </c:if>
                                <c:if test="${bal >= 0}">
                                    <span style="color: var(--accent); font-size: 0.85rem;">Clear</span>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <!-- Itemized Expenses with Split Breakdowns -->
            <h3 style="margin-bottom: 1rem; color: #fff;">🧾 Itemized Expense & Split Allocation Audit</h3>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Title</th>
                        <th>Paid By</th>
                        <th>Amount</th>
                        <th>Splits Breakdown</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="exp" items="${ledger.recentExpenses}">
                        <tr>
                            <td><c:out value="${exp.expenseDate.toLocalDate()}"/></td>
                            <td><strong><c:out value="${exp.title}"/></strong></td>
                            <td><c:out value="${exp.paidBy.name}"/></td>
                            <td style="color: var(--secondary); font-weight: bold;">₹<fmt:formatNumber value="${exp.amount}" type="number" minFractionDigits="2"/></td>
                            <td>
                                <div style="display: flex; flex-wrap: wrap; gap: 0.4rem;">
                                    <c:forEach var="sp" items="${exp.splits}">
                                        <span style="background: var(--bg-card-alt); padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem;">
                                            <c:out value="${sp.user.name}"/>: ₹<fmt:formatNumber value="${sp.shareAmount}" type="number" minFractionDigits="2"/>
                                        </span>
                                    </c:forEach>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

</body>
</html>
