/**
 * Fundora static demo engine (GitHub Pages / Vercel preview).
 * Mirrors the Spring Boot backend's seed data (schema.sql) and ledger maths
 * (equal splits + min-cash-flow debt simplification) entirely in the browser.
 * Changes are kept in localStorage so the dashboard and ledger pages stay in sync.
 */
const FundoraDemo = (() => {
    const STATE_KEY = 'fundora_demo_state_v2';
    const USER_KEY = 'fundora_demo_user_id';

    const USERS = [
        { id: 1, name: 'Dnyaneshwar Sangale', email: 'dnyaneshwar@fundora.app', phone: '9820112233', upi: 'dnyaneshwar@okhdfcbank', role: 'STUDENT', wallet: 2500 },
        { id: 2, name: 'Tejasvee Patil', email: 'tejasvee@fundora.app', phone: '9820223344', upi: 'tejasvee@icici', role: 'STUDENT', wallet: 1800 },
        { id: 3, name: 'Bharati Sharma', email: 'bharati@fundora.app', phone: '9820334455', upi: 'bharati@paytm', role: 'STUDENT', wallet: 3200 },
        { id: 4, name: 'Atharva Sawant', email: 'atharva@fundora.app', phone: '9820445566', upi: 'atharva@axisbank', role: 'STUDENT', wallet: 1200 }
    ];

    const seedState = () => ({
        groups: [
            { id: '1', name: 'Gokul Flat 402 (Powai)', description: 'Monthly Rent, WiFi, Cook, & Grocery Split', groupType: 'FLATMATE_RECURRING', members: [1, 2, 3, 4] },
            { id: '2', name: 'Goa Semester Break Trip', description: 'Villa, Beach Sports, Car Rental & Food', groupType: 'TRIP_EVENT', members: [1, 2, 3, 4] }
        ],
        expenses: [
            { id: 'e1', groupId: '1', title: 'Monthly D-Mart Groceries & Supplies', amount: 12000, category: 'GROCERIES', paidBy: 1, date: '2026-10-07', splits: { 1: 3000, 2: 3000, 3: 3000, 4: 3000 } },
            { id: 'e2', groupId: '1', title: 'High-Speed Fiber Internet Bill', amount: 2400, category: 'UTILITIES', paidBy: 2, date: '2026-10-06', splits: { 1: 600, 2: 600, 3: 600, 4: 600 } },
            { id: 'e3', groupId: '2', title: 'Goa Beach Resort Booking Advance', amount: 16000, category: 'ACCOMMODATION', paidBy: 3, date: '2026-10-05', splits: { 1: 4000, 2: 4000, 3: 4000, 4: 4000 } }
        ],
        settlements: [],
        goals: [
            { id: 'g1', groupId: '2', title: 'Goa Scuba Diving & Water Sports Fund', current: 14500, target: 20000 },
            { id: 'g2', groupId: '1', title: 'Living Room Smart TV Upgrade', current: 9000, target: 18000 }
        ],
        wallets: Object.fromEntries(USERS.map(u => [u.id, u.wallet]))
    });

    // Work in integer paise so floating point never leaks into balances
    const toPaise = (rupees) => Math.round(Number(rupees) * 100);
    const fromPaise = (paise) => paise / 100;

    function load() {
        try {
            const raw = localStorage.getItem(STATE_KEY);
            if (raw) return JSON.parse(raw);
        } catch (_) { /* storage blocked or corrupt: fall back to seed */ }
        return seedState();
    }

    let state = load();

    function save() {
        try { localStorage.setItem(STATE_KEY, JSON.stringify(state)); } catch (_) { /* private mode */ }
    }

    function reset() {
        state = seedState();
        save();
    }

    const user = (id) => USERS.find(u => u.id === Number(id));

    function currentUser() {
        let id = 1;
        try { id = Number(localStorage.getItem(USER_KEY)) || 1; } catch (_) { /* ignore */ }
        const u = user(id) || USERS[0];
        return { ...u, wallet: state.wallets[u.id] };
    }

    function signIn(id) {
        try { localStorage.setItem(USER_KEY, String(id)); } catch (_) { /* ignore */ }
    }

    function findUserByLogin(emailOrPhone) {
        const v = String(emailOrPhone || '').trim().toLowerCase();
        return USERS.find(u => u.email === v || u.phone === v) || null;
    }

    const groupsFor = (userId) => state.groups.filter(g => g.members.includes(Number(userId)));
    const group = (groupId) => state.groups.find(g => g.id === String(groupId)) || null;

    function ledger(groupId) {
        const g = group(groupId);
        if (!g) return null;
        const expenses = state.expenses.filter(e => e.groupId === g.id).sort((a, b) => b.date.localeCompare(a.date));
        const settlements = state.settlements.filter(s => s.groupId === g.id);

        // Net balance = paid - owed + settlements paid - settlements received
        const net = {};
        g.members.forEach(id => { net[id] = 0; });
        expenses.forEach(e => {
            net[e.paidBy] = (net[e.paidBy] || 0) + toPaise(e.amount);
            Object.entries(e.splits).forEach(([uid, share]) => { net[uid] = (net[uid] || 0) - toPaise(share); });
        });
        settlements.forEach(s => {
            net[s.from] = (net[s.from] || 0) + toPaise(s.amount);
            net[s.to] = (net[s.to] || 0) - toPaise(s.amount);
        });

        // Greedy min-cash-flow: repeatedly match the largest debtor with the largest creditor
        const byLargest = (a, b) => b.amt - a.amt || a.id - b.id;
        const debtors = [], creditors = [];
        Object.entries(net).forEach(([id, p]) => {
            if (p > 0) creditors.push({ id: Number(id), amt: p });
            if (p < 0) debtors.push({ id: Number(id), amt: -p });
        });
        const debts = [];
        while (debtors.length && creditors.length) {
            debtors.sort(byLargest); creditors.sort(byLargest);
            const d = debtors[0], c = creditors[0];
            const amt = Math.min(d.amt, c.amt);
            debts.push({ from: d.id, to: c.id, amount: fromPaise(amt) });
            d.amt -= amt; c.amt -= amt;
            if (!d.amt) debtors.shift();
            if (!c.amt) creditors.shift();
        }

        const balances = Object.fromEntries(Object.entries(net).map(([id, p]) => [id, fromPaise(p)]));
        const total = fromPaise(expenses.reduce((sum, e) => sum + toPaise(e.amount), 0));
        return { group: g, total, balances, debts, expenses, settlements };
    }

    function addExpense(groupId, paidBy, title, amount, category) {
        const g = group(groupId);
        const paise = toPaise(amount);
        if (!g) throw new Error('Group not found.');
        if (!title || !title.trim()) throw new Error('Expense title is required.');
        if (!(paise > 0)) throw new Error('Expense amount must be greater than zero.');

        // Equal split with leftover paise going to the first members (same as EqualExpenseSplitter)
        const members = [...g.members].sort((a, b) => a - b);
        const base = Math.floor(paise / members.length);
        let remainder = paise - base * members.length;
        const splits = {};
        members.forEach(id => { splits[id] = fromPaise(base + (remainder-- > 0 ? 1 : 0)); });

        state.expenses.push({
            id: 'e' + Date.now(), groupId: g.id, title: title.trim(), amount: fromPaise(paise),
            category, paidBy: Number(paidBy), date: new Date().toISOString().slice(0, 10), splits
        });
        save();
    }

    function settle(groupId, from, to, amount) {
        const paise = toPaise(amount);
        const wallet = toPaise(state.wallets[from]);
        if (paise > wallet) {
            throw new Error(`${user(from).name}'s wallet balance (₹${formatINR(fromPaise(wallet))}) is not enough to settle ₹${formatINR(amount)}.`);
        }
        state.wallets[from] = fromPaise(wallet - paise);
        state.wallets[to] = fromPaise(toPaise(state.wallets[to]) + paise);
        const ref = 'UPI/FUNDORA/' + Math.random().toString(16).slice(2, 10).toUpperCase();
        state.settlements.unshift({ groupId: String(groupId), from: Number(from), to: Number(to), amount: fromPaise(paise), ref });
        save();
        return ref;
    }

    function createGroup(name, description, groupType, creatorId, memberIds) {
        if (!name || !name.trim()) throw new Error('Group name is required.');
        const members = Array.from(new Set([Number(creatorId), ...memberIds.map(Number)]));
        const g = { id: 'grp_' + Date.now(), name: name.trim(), description: (description || '').trim(), groupType, members };
        state.groups.push(g);
        save();
        return g;
    }

    function goalsFor(groupId) {
        return state.goals.filter(goal => goal.groupId === String(groupId));
    }

    function contribute(goalId, amount) {
        const goal = state.goals.find(x => x.id === goalId);
        const paise = toPaise(amount);
        if (!goal || !(paise > 0)) throw new Error('Enter a valid contribution amount.');
        goal.current = fromPaise(toPaise(goal.current) + paise);
        save();
    }

    function formatINR(n) {
        return Number(n).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    function escapeHTML(value) {
        return String(value).replace(/[&<>"']/g, ch => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[ch]));
    }

    return {
        USERS, user, currentUser, signIn, findUserByLogin, groupsFor, group, ledger,
        addExpense, settle, createGroup, goalsFor, contribute, reset, formatINR, escapeHTML
    };
})();
