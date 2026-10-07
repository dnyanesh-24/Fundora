<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fundora - Social FinTech Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
    <!-- Module V: React 18 and Babel for in-browser JSX rendering -->
    <script src="https://unpkg.com/react@18/umd/react.production.min.js" crossorigin></script>
    <script src="https://unpkg.com/react-dom@18/umd/react-dom.production.min.js" crossorigin></script>
    <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
</head>
<body>

    <!-- Header Navigation -->
    <header class="navbar">
        <a href="${pageContext.request.contextPath}/dashboard" class="brand">
            <span>✨ Fundora</span>
            <span class="brand-badge">Mumbai Univ Full-Stack Java</span>
        </a>
        <div class="user-profile-badge">
            <span>👤 <strong><c:out value="${currentUser.name}"/></strong> (<c:out value="${currentUser.role}"/>)</span>
            <span style="color: var(--accent); font-weight: bold;">Wallet: ₹<fmt:formatNumber value="${currentUser.walletBalance}" type="number" minFractionDigits="2"/></span>
            <span style="font-size: 0.8rem; color: var(--text-muted);"><c:out value="${currentUser.upiId}"/></span>
        </div>
    </header>

    <div class="container">

        <!-- Group Selector Bar -->
        <div style="display: flex; gap: 1rem; align-items: center; margin-bottom: 2rem;">
            <label style="color: var(--text-muted); font-weight: 600;">Active Group:</label>
            <form action="${pageContext.request.contextPath}/dashboard" method="GET" style="display: flex; gap: 0.75rem;">
                <select name="groupId" onchange="this.form.submit()" class="form-control" style="width: auto; min-width: 250px;">
                    <c:forEach var="grp" items="${groups}">
                        <option value="${grp.id}" ${grp.id == selectedGroupId ? 'selected' : ''}>
                            <c:out value="${grp.name}"/> (<c:out value="${grp.groupType}"/>)
                        </option>
                    </c:forEach>
                </select>
            </form>
            <a href="${pageContext.request.contextPath}/ledger?groupId=${selectedGroupId}" class="btn btn-primary" style="margin-left: auto;">
                📊 View Full Shared Ledger
            </a>
        </div>

        <!-- Metric Cards -->
        <div class="hero-stats-grid">
            <div class="stat-card">
                <div class="stat-title">Total Group Spending</div>
                <div class="stat-value">₹<fmt:formatNumber value="${ledger.totalGroupSpending}" type="number" minFractionDigits="2"/></div>
            </div>
            <div class="stat-card">
                <div class="stat-title">Your Net Standing</div>
                <c:set var="myBalance" value="${ledger.netBalances[currentUser.id]}"/>
                <div class="stat-value ${myBalance >= 0 ? 'positive' : 'negative'}">
                    ${myBalance >= 0 ? '+₹' : '-₹'}<fmt:formatNumber value="${myBalance >= 0 ? myBalance : -myBalance}" type="number" minFractionDigits="2"/>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-title">Active Members</div>
                <div class="stat-value"><c:out value="${ledger.group.members.size()}"/> Flatmates</div>
            </div>
        </div>

        <!-- Main 2-Column Grid -->
        <div class="grid-2col">
            
            <!-- Left Column: Debts & Recent Expenses -->
            <div>
                <!-- Simplified Debts ("Who Owes Whom") & Smart Nudges -->
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">🤝 Net Settlements & Smart Reminders</h2>
                        <span style="font-size: 0.8rem; color: var(--text-muted);">Auto-Simplified Ledger</span>
                    </div>

                    <c:if test="${empty ledger.simplifiedDebts}">
                        <p style="color: var(--accent); padding: 1rem 0;">🎉 All debts are completely settled in this group!</p>
                    </c:if>

                    <c:forEach var="debt" items="${ledger.simplifiedDebts}">
                        <div class="debt-item">
                            <div class="debt-info">
                                <span class="debt-names">
                                    <c:out value="${debt.fromUserName}"/> owes <c:out value="${debt.toUserName}"/>
                                </span>
                                <span class="debt-upi">UPI: <c:out value="${debt.toUserUpiId}"/></span>
                            </div>
                            <div class="debt-actions">
                                <span style="font-weight: 700; color: #fff; margin-right: 0.5rem;">
                                    ₹<fmt:formatNumber value="${debt.amount}" type="number" minFractionDigits="2"/>
                                </span>
                                
                                <!-- Smart Nudge Button (Module IV JS Handler) -->
                                <button 
                                    class="btn btn-nudge btn-smart-nudge"
                                    data-sender-id="${debt.toUserId}"
                                    data-receiver-id="${debt.fromUserId}"
                                    data-receiver-name="${debt.fromUserName}"
                                    data-group-id="${selectedGroupId}"
                                    data-amount="${debt.amount}">
                                    Nudge 🔔
                                </button>

                                <!-- Direct UPI Settlement Button -->
                                <button 
                                    class="btn btn-upi btn-upi-settle"
                                    data-payer-id="${debt.fromUserId}"
                                    data-payee-id="${debt.toUserId}"
                                    data-payee-name="${debt.toUserName}"
                                    data-upi-id="${debt.toUserUpiId}"
                                    data-group-id="${selectedGroupId}"
                                    data-amount="${debt.amount}">
                                    Pay UPI ⚡
                                </button>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Add New Shared Expense Form -->
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">➕ Split a New Expense</h2>
                    </div>
                    <form action="${pageContext.request.contextPath}/expenses" method="POST">
                        <input type="hidden" name="action" value="addExpense"/>
                        <input type="hidden" name="groupId" value="${selectedGroupId}"/>
                        
                        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1rem;">
                            <div class="form-group">
                                <label class="form-label">Expense Title / Description</label>
                                <input type="text" name="title" class="form-control" placeholder="e.g. D-Mart Grocery / WiFi Bill" required/>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Total Amount (₹)</label>
                                <input type="number" step="0.01" name="amount" class="form-control" placeholder="0.00" required/>
                            </div>
                        </div>

                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                            <div class="form-group">
                                <label class="form-label">Category</label>
                                <select name="category" class="form-control">
                                    <option value="GROCERIES">Groceries & Food</option>
                                    <option value="RENT">Room Rent / Deposit</option>
                                    <option value="UTILITIES">WiFi / Electricity / Gas</option>
                                    <option value="TRAVEL">Cabs / Flights / Fuel</option>
                                    <option value="ENTERTAINMENT">Outing / Party</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Splitting Strategy</label>
                                <select name="splitType" class="form-control">
                                    <option value="EQUAL">Split Equally Among All</option>
                                </select>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 0.5rem;">
                            Record & Split Expense
                        </button>
                    </form>
                </div>

                <!-- Recent Expenses Table -->
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">📜 Recent Group Expenses</h2>
                    </div>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Item</th>
                                <th>Paid By</th>
                                <th>Category</th>
                                <th>Amount</th>
                            </tr>
                        </thead>
                        <tbody id="expenseTableBody">
                            <c:forEach var="exp" items="${ledger.recentExpenses}">
                                <tr>
                                    <td><c:out value="${exp.expenseDate.toLocalDate()}"/></td>
                                    <td><strong><c:out value="${exp.title}"/></strong></td>
                                    <td><c:out value="${exp.paidBy.name}"/></td>
                                    <td><span style="background: var(--bg-card-alt); padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem;"><c:out value="${exp.category}"/></span></td>
                                    <td class="amount-cell" data-amount="${exp.amount}">₹<fmt:formatNumber value="${exp.amount}" type="number" minFractionDigits="2"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

            </div>

            <!-- Right Column: Module V React Component (Collaborative Trip / Event Savings Goal) -->
            <div>
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">🏖️ Shared Trip & Goal Wallet</h2>
                        <span style="font-size: 0.75rem; background: #4f46e5; padding: 0.2rem 0.5rem; border-radius: 4px;">React.js SPA</span>
                    </div>
                    <!-- React Mounting Root -->
                    <div id="reactSavingsTrackerRoot" data-group-id="${selectedGroupId}"></div>
                </div>

                <!-- Recent Settlements History -->
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">⚡ UPI Settlement Log</h2>
                    </div>
                    <c:if test="${empty ledger.recentSettlements}">
                        <p style="color: var(--text-muted); font-size: 0.9rem;">No direct UPI settlements logged yet.</p>
                    </c:if>
                    <c:forEach var="st" items="${ledger.recentSettlements}">
                        <div style="padding: 0.75rem 0; border-bottom: 1px solid var(--border-color); font-size: 0.85rem;">
                            <div><strong><c:out value="${st.payer.name}"/></strong> ➡️ <strong><c:out value="${st.payee.name}"/></strong></div>
                            <div style="display: flex; justify-content: space-between; color: var(--text-muted); margin-top: 0.25rem;">
                                <span>Ref: <c:out value="${st.transactionReference}"/></span>
                                <span style="color: var(--accent); font-weight: bold;">₹<fmt:formatNumber value="${st.amount}" type="number" minFractionDigits="2"/></span>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>

        </div>

    </div>

    <!-- Scripts -->
    <script src="${pageContext.request.contextPath}/static/js/app.js"></script>
    <script type="text/babel" src="${pageContext.request.contextPath}/static/js/react-components.js"></script>
</body>
</html>
