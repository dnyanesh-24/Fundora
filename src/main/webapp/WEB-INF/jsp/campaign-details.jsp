<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${campaign.title}"/> - Fundora</title>
    <!-- Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Styles -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
</head>
<body>

    <!-- NAVBAR -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/campaigns" class="brand">
            <div class="brand-icon">F</div>
            <span>Fundora</span>
        </a>

        <div class="nav-actions">
            <a href="${pageContext.request.contextPath}/campaigns" class="btn btn-outline" style="padding: 0.4rem 0.9rem; font-size: 0.85rem;">
                ← Back to Dashboard
            </a>

            <!-- Active User -->
            <div class="user-badge">
                <span>👤 <strong><c:out value="${sessionScope.currentUser.name}"/></strong></span>
                <span class="role-pill role-<c:out value='${sessionScope.currentUser.role}'/>">
                    <c:out value="${sessionScope.currentUser.role}"/>
                </span>
                <span style="color: var(--accent); font-weight: 700; margin-left: 0.5rem;">
                    $<fmt:formatNumber value="${sessionScope.currentUser.walletBalance}" pattern="#,##0.00"/>
                </span>
            </div>
        </div>
    </nav>

    <!-- MAIN CONTENT -->
    <div class="container" style="max-width: 1080px;">

        <div style="display: grid; grid-template-columns: 1.2fr 1fr; gap: 2.5rem; margin-top: 1rem;">
            
            <!-- LEFT COLUMN: IMAGE & DESCRIPTION -->
            <div>
                <div style="border-radius: var(--radius-md); overflow: hidden; height: 380px; box-shadow: var(--shadow-md); border: 1px solid var(--border-color); margin-bottom: 2rem;">
                    <img src="<c:out value='${campaign.imageUrl}'/>" alt="<c:out value='${campaign.title}'/>" style="width: 100%; height: 100%; object-fit: cover;">
                </div>

                <div style="background: var(--bg-card); padding: 2rem; border-radius: var(--radius-md); border: 1px solid var(--border-color);">
                    <h2 style="font-size: 1.4rem; font-weight: 700; margin-bottom: 1rem; color: var(--text-main);">About the Project</h2>
                    <p style="color: #cbd5e1; font-size: 1rem; line-height: 1.8; white-space: pre-line;">
                        <c:out value="${campaign.description}"/>
                    </p>
                </div>
            </div>

            <!-- RIGHT COLUMN: FINANCIAL PROGRESS & ACTION -->
            <div>
                <div style="background: var(--bg-card); padding: 2rem; border-radius: var(--radius-md); border: 1px solid var(--border-color); box-shadow: var(--shadow-md);">
                    
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                        <span class="role-pill" style="background: rgba(79, 70, 229, 0.2); color: #818cf8; border: 1px solid #4f46e5;">
                            <c:out value="${campaign.category}"/>
                        </span>
                        <span class="badge-status status-<c:out value='${campaign.status}'/>" style="position: static;">
                            <c:out value="${campaign.status}"/>
                        </span>
                    </div>

                    <h1 style="font-size: 1.7rem; font-weight: 800; line-height: 1.3; margin-bottom: 1.5rem; color: var(--text-main);">
                        <c:out value="${campaign.title}"/>
                    </h1>

                    <!-- Raised Info -->
                    <div style="margin-bottom: 1.5rem;">
                        <div style="font-size: 2.4rem; font-weight: 800; color: var(--accent);">
                            $<fmt:formatNumber value="${campaign.raisedAmount}" pattern="#,##0.00"/>
                        </div>
                        <div style="color: var(--text-muted); font-size: 0.95rem;">
                            pledged of <strong>$<fmt:formatNumber value="${campaign.goalAmount}" pattern="#,##0.00"/></strong> goal
                        </div>
                    </div>

                    <!-- Progress Bar -->
                    <div class="progress-container" style="margin-bottom: 1.5rem;">
                        <div class="progress-bar-bg" style="height: 10px;">
                            <div class="progress-fill ${campaign.isFunded() ? 'completed' : ''}" style="width: ${campaign.progressPercentage > 100 ? 100 : campaign.progressPercentage}%;"></div>
                        </div>
                        <div style="display: flex; justify-content: space-between; margin-top: 0.5rem; font-size: 0.85rem; color: var(--text-muted);">
                            <span><fmt:formatNumber value="${campaign.progressPercentage}" maxFractionDigits="1"/>% funded</span>
                            <span>⏳ <c:out value="${campaign.daysRemaining}"/> days to go</span>
                        </div>
                    </div>

                    <!-- Creator Card -->
                    <div style="background: rgba(15, 23, 42, 0.6); padding: 1rem; border-radius: var(--radius-sm); border: 1px solid var(--border-color); margin-bottom: 1.5rem; display: flex; align-items: center; gap: 0.75rem;">
                        <div style="width: 42px; height: 42px; border-radius: 50%; background: var(--primary); display: flex; align-items: center; justify-content: center; font-weight: 700; color: white;">
                            <c:out value="${campaign.creator.name.substring(0, 1)}"/>
                        </div>
                        <div>
                            <div style="font-weight: 700; font-size: 0.95rem;"><c:out value="${campaign.creator.name}"/></div>
                            <small style="color: var(--text-muted);"><c:out value="${campaign.creator.email}"/></small>
                        </div>
                    </div>

                    <!-- Actions -->
                    <c:choose>
                        <c:when test="${campaign.status == 'ACTIVE'}">
                            <button type="button" class="btn btn-success" style="width: 100%; padding: 0.9rem; font-size: 1.1rem; margin-bottom: 1rem;"
                                    onclick="openPledgeModal('${campaign.id}', '${campaign.title}', '${campaign.goalAmount}', '${campaign.raisedAmount}')">
                                🚀 Back This Project
                            </button>
                        </c:when>
                        <c:otherwise>
                            <div style="text-align: center; padding: 0.8rem; background: rgba(255, 255, 255, 0.05); border-radius: var(--radius-sm); color: var(--text-muted); font-weight: 600; margin-bottom: 1rem;">
                                This campaign is <c:out value="${campaign.status}"/>. Pledges are closed.
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <!-- Admin / Creator Lifecycle Controls -->
                    <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.id == campaign.creator.id}">
                        <div style="border-top: 1px solid var(--border-color); padding-top: 1rem; margin-top: 1rem;">
                            <span style="font-size: 0.8rem; color: var(--text-muted); text-transform: uppercase; font-weight: 700;">Management Controls:</span>
                            <div style="display: flex; gap: 0.5rem; margin-top: 0.5rem;">
                                <c:if test="${campaign.status == 'ACTIVE'}">
                                    <button class="btn btn-outline" style="font-size: 0.8rem; padding: 0.4rem 0.8rem;"
                                            onclick="updateStatus('${campaign.id}', 'COMPLETED')">
                                        Mark Completed
                                    </button>
                                    <button class="btn btn-outline" style="font-size: 0.8rem; padding: 0.4rem 0.8rem; color: var(--danger); border-color: rgba(239, 68, 68, 0.3);"
                                            onclick="updateStatus('${campaign.id}', 'CANCELLED')">
                                        Cancel Campaign
                                    </button>
                                </c:if>
                            </div>
                        </div>
                    </c:if>

                </div>
            </div>

        </div>

        <!-- TRANSACTION / BACKERS TABLE -->
        <div class="table-wrapper">
            <div style="padding: 1.25rem 1.5rem; border-bottom: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center;">
                <h3 style="font-size: 1.15rem; font-weight: 700;">Backer Ledger & Contributions (<c:out value="${transactions.size()}"/>)</h3>
                <span style="font-size: 0.85rem; color: var(--accent); font-weight: 600;">Verified Transactions</span>
            </div>

            <c:choose>
                <c:when test="${empty transactions}">
                    <div style="text-align: center; padding: 3rem; color: var(--text-muted);">
                        No contributions yet. Be the first visionary backer to support this project!
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>Supporter</th>
                                <th>Amount</th>
                                <th>Reference Code</th>
                                <th>Status</th>
                                <th>Timestamp</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="txn" items="${transactions}">
                                <tr>
                                    <td>
                                        <div style="font-weight: 600;"><c:out value="${txn.supporter.name}"/></div>
                                        <small style="color: var(--text-muted);"><c:out value="${txn.supporter.email}"/></small>
                                    </td>
                                    <td style="font-weight: 700; color: var(--accent);">
                                        $<fmt:formatNumber value="${txn.amount}" pattern="#,##0.00"/>
                                    </td>
                                    <td>
                                        <code style="background: rgba(255,255,255,0.06); padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.8rem;">
                                            <c:out value="${txn.paymentReference}"/>
                                        </code>
                                    </td>
                                    <td>
                                        <span class="role-pill" style="background: #d1fae5; color: #065f46;">
                                            <c:out value="${txn.paymentStatus}"/>
                                        </span>
                                    </td>
                                    <td style="color: var(--text-muted); font-size: 0.85rem;">
                                        <c:out value="${txn.timestamp}"/>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>

    </div>

    <!-- PLEDGE MODAL -->
    <div id="pledgeModal" class="modal-backdrop">
        <div class="modal-content">
            <div class="modal-header">
                <h3 style="font-size: 1.3rem; font-weight: 700;">Back This Initiative</h3>
                <button type="button" class="modal-close">&times;</button>
            </div>

            <div id="modalAlert" style="display:none;"></div>

            <h4 id="modalCampaignTitle" style="color: var(--secondary); margin-bottom: 0.5rem;"><c:out value="${campaign.title}"/></h4>
            <p id="modalProgressText" style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 1.5rem;">
                $<fmt:formatNumber value="${campaign.raisedAmount}" pattern="#,##0"/> raised of $<fmt:formatNumber value="${campaign.goalAmount}" pattern="#,##0"/>
            </p>

            <form id="pledgeForm">
                <input type="hidden" id="modalCampaignId" name="campaignId" value="${campaign.id}">
                <input type="hidden" id="modalSupporterId" name="supporterId" value="${sessionScope.currentUser.id}">

                <div class="form-group">
                    <label for="pledgeAmountInput">Pledge Amount (USD)</label>
                    <input type="number" id="pledgeAmountInput" name="amount" class="input-field" placeholder="e.g. 250.00" min="1.00" step="0.01" required>
                    <small style="color: var(--text-muted); display: block; margin-top: 0.3rem;">
                        Deducted from your wallet balance ($<fmt:formatNumber value="${sessionScope.currentUser.walletBalance}" pattern="#,##0.00"/> available)
                    </small>
                </div>

                <div style="display: flex; gap: 0.75rem; justify-content: flex-end; margin-top: 1.5rem;">
                    <button type="button" class="btn btn-outline btn-cancel-modal">Cancel</button>
                    <button type="submit" id="btnSubmitPledge" class="btn btn-success">Confirm Investment</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Application JS -->
    <script src="${pageContext.request.contextPath}/static/js/app.js"></script>
    <script>
        async function updateStatus(campaignId, status) {
            if (!confirm('Are you sure you want to mark this campaign as ' + status + '?')) return;
            try {
                const userEmail = '<c:out value="${sessionScope.currentUser.email}"/>';
                const response = await fetch('/api/campaigns/' + campaignId + '/status?status=' + status + '&performedBy=' + encodeURIComponent(userEmail), {
                    method: 'POST'
                });
                const res = await response.json();
                if (response.ok && res.success) {
                    window.location.reload();
                } else {
                    alert(res.message || 'Status update failed.');
                }
            } catch (err) {
                console.error(err);
                alert('Connection error.');
            }
        }
    </script>
</body>
</html>
