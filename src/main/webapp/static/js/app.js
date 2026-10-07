/**
 * Module IV: Fundamentals of JavaScript & DOM Manipulation
 * Module VI: Asynchronous Fetch API interaction with Spring Boot REST Controllers
 */

document.addEventListener('DOMContentLoaded', () => {
    console.log("⚡ Fundora Social FinTech Client Engine Initialized.");

    // Initialize Event Listeners for Nudges and UPI Settlements
    initNudgeButtons();
    initSettlementButtons();
    scanAndHighlightLedgerDOM();
});

/**
 * Module IV: JavaScript DOM Tree Traversal using NodeIterator & Dynamic Highlighting
 */
function scanAndHighlightLedgerDOM() {
    const tableBody = document.querySelector('#expenseTableBody');
    if (!tableBody) return;

    const iterator = document.createNodeIterator(
        tableBody,
        NodeFilter.SHOW_ELEMENT,
        {
            acceptNode: function(node) {
                return node.classList && node.classList.contains('amount-cell') 
                    ? NodeFilter.FILTER_ACCEPT 
                    : NodeFilter.FILTER_SKIP;
            }
        }
    );

    let currentNode;
    while ((currentNode = iterator.nextNode())) {
        const val = parseFloat(currentNode.getAttribute('data-amount') || 0);
        if (val > 5000) {
            currentNode.style.fontWeight = 'bold';
            currentNode.style.color = '#38bdf8'; // Highlight high-value shared expenses
        }
    }
}

/**
 * Module IV & VI: Smart Reminder Nudge Handler (Non-intrusive alert triggering)
 */
function initNudgeButtons() {
    const nudgeButtons = document.querySelectorAll('.btn-smart-nudge');
    nudgeButtons.forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const senderId = btn.getAttribute('data-sender-id');
            const receiverId = btn.getAttribute('data-receiver-id');
            const receiverName = btn.getAttribute('data-receiver-name');
            const groupId = btn.getAttribute('data-group-id');
            const amount = btn.getAttribute('data-amount');

            btn.disabled = true;
            btn.innerText = 'Sending Nudge...';

            try {
                const response = await fetch('/api/nudge', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-Fundora-User-Id': senderId
                    },
                    body: JSON.stringify({
                        senderUserId: parseInt(senderId),
                        receiverUserId: parseInt(receiverId),
                        groupId: parseInt(groupId),
                        amount: parseFloat(amount)
                    })
                });

                if (response.ok) {
                    const data = await response.json();
                    showToast(`✨ Smart Nudge sent to ${receiverName}!`, 'success');
                    btn.innerText = 'Nudged ✔';
                    btn.classList.add('btn-accent');
                } else {
                    showToast('Failed to send nudge. Please retry.', 'error');
                    btn.disabled = false;
                    btn.innerText = 'Smart Nudge 🔔';
                }
            } catch (err) {
                console.error("Nudge error:", err);
                showToast('Network error while nudging.', 'error');
                btn.disabled = false;
                btn.innerText = 'Smart Nudge 🔔';
            }
        });
    });
}

/**
 * Module IV & VI: Direct UPI Settlement Execution & Auto-Balancing
 */
function initSettlementButtons() {
    const settleButtons = document.querySelectorAll('.btn-upi-settle');
    settleButtons.forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const payerId = btn.getAttribute('data-payer-id');
            const payeeId = btn.getAttribute('data-payee-id');
            const payeeName = btn.getAttribute('data-payee-name');
            const upiId = btn.getAttribute('data-upi-id');
            const groupId = btn.getAttribute('data-group-id');
            const amount = btn.getAttribute('data-amount');

            if (!confirm(`Confirm UPI settlement of ₹${amount} to ${payeeName} (${upiId})?`)) {
                return;
            }

            btn.disabled = true;
            btn.innerText = 'Processing UPI...';

            try {
                const response = await fetch('/api/settle/upi', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-Fundora-User-Id': payerId
                    },
                    body: JSON.stringify({
                        groupId: parseInt(groupId),
                        payerUserId: parseInt(payerId),
                        payeeUserId: parseInt(payeeId),
                        amount: parseFloat(amount),
                        upiId: upiId,
                        paymentMode: 'UPI'
                    })
                });

                if (response.ok) {
                    const data = await response.json();
                    showToast(`🎉 Settled ₹${amount} via UPI to ${payeeName}! Ref: ${data.transactionReference}`, 'success');
                    setTimeout(() => {
                        window.location.reload();
                    }, 1500);
                } else {
                    showToast('UPI settlement error. Please verify balance.', 'error');
                    btn.disabled = false;
                    btn.innerText = 'Pay via UPI ⚡';
                }
            } catch (err) {
                console.error("UPI error:", err);
                showToast('Network error during settlement.', 'error');
                btn.disabled = false;
                btn.innerText = 'Pay via UPI ⚡';
            }
        });
    });
}

/**
 * Module IV: Toast Notification Component dynamically injected into DOM
 */
function showToast(message, type = 'info') {
    let container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.innerHTML = `<span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transition = 'opacity 0.5s ease';
        setTimeout(() => toast.remove(), 500);
    }, 4000);
}
