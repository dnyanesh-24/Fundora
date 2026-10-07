/**
 * Module V: React Integration & SPA Component Design
 * Component: FundoraSavingsTracker & LiveBalanceCalculator
 */

const { useState, useEffect } = React;

function SavingsTracker({ initialGroupId }) {
    const [goals, setGoals] = useState([]);
    const [loading, setLoading] = useState(true);
    const [contributionAmounts, setContributionAmounts] = useState({});
    const [newGoalTitle, setNewGoalTitle] = useState('');
    const [newGoalTarget, setNewGoalTarget] = useState('');

    useEffect(() => {
        fetchGoals();
    }, [initialGroupId]);

    const fetchGoals = async () => {
        try {
            const res = await fetch(`/api/groups/${initialGroupId || 1}/savings`);
            if (res.ok) {
                const data = await res.json();
                setGoals(data);
            }
        } catch (err) {
            console.error("Failed to load savings goals:", err);
        } finally {
            setLoading(false);
        }
    };

    const handleContribute = async (goalId) => {
        const amount = contributionAmounts[goalId];
        if (!amount || parseFloat(amount) <= 0) {
            alert("Please enter a valid contribution amount.");
            return;
        }

        try {
            const res = await fetch(`/api/savings/${goalId}/contribute`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ amount: parseFloat(amount) })
            });

            if (res.ok) {
                setContributionAmounts(prev => ({ ...prev, [goalId]: '' }));
                fetchGoals();
            }
        } catch (err) {
            console.error("Contribution error:", err);
        }
    };

    const handleCreateGoal = async (e) => {
        e.preventDefault();
        if (!newGoalTitle || !newGoalTarget) return;

        try {
            const res = await fetch(`/api/groups/${initialGroupId || 1}/savings`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    title: newGoalTitle,
                    targetAmount: parseFloat(newGoalTarget),
                    deadline: new Date(Date.now() + 60*24*60*60*1000).toISOString().split('T')[0]
                })
            });

            if (res.ok) {
                setNewGoalTitle('');
                setNewGoalTarget('');
                fetchGoals();
            }
        } catch (err) {
            console.error("Create goal error:", err);
        }
    };

    if (loading) {
        return <div style={{ color: '#94a3b8' }}>Loading collaborative savings goals...</div>;
    }

    return (
        <div>
            {goals.map(goal => {
                const pct = Math.min(100, Math.round((goal.currentAmount / goal.targetAmount) * 100));
                return (
                    <div key={goal.id} style={{
                        background: '#334155',
                        padding: '1.25rem',
                        borderRadius: '10px',
                        marginBottom: '1rem',
                        border: '1px solid #475569'
                    }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                            <strong style={{ color: '#f8fafc', fontSize: '1.05rem' }}>{goal.title}</strong>
                            <span style={{ 
                                background: goal.status === 'COMPLETED' ? '#10b981' : '#06b6d4', 
                                color: '#fff', 
                                padding: '0.15rem 0.5rem', 
                                borderRadius: '6px',
                                fontSize: '0.75rem',
                                fontWeight: 'bold'
                            }}>
                                {goal.status}
                            </span>
                        </div>

                        {/* Progress Bar */}
                        <div style={{ 
                            background: '#1e293b', 
                            borderRadius: '9999px', 
                            height: '14px', 
                            overflow: 'hidden', 
                            marginBottom: '0.75rem' 
                        }}>
                            <div style={{
                                width: `${pct}%`,
                                background: 'linear-gradient(90deg, #4f46e5, #06b6d4)',
                                height: '100%',
                                transition: 'width 0.4s ease-in-out'
                            }}></div>
                        </div>

                        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', color: '#94a3b8', marginBottom: '0.75rem' }}>
                            <span>Saved: <strong style={{ color: '#10b981' }}>₹{goal.currentAmount.toLocaleString()}</strong></span>
                            <span>Target: ₹{goal.targetAmount.toLocaleString()} ({pct}%)</span>
                        </div>

                        {goal.status === 'ACTIVE' && (
                            <div style={{ display: 'flex', gap: '0.5rem' }}>
                                <input
                                    type="number"
                                    placeholder="Contribute ₹"
                                    value={contributionAmounts[goal.id] || ''}
                                    onChange={(e) => setContributionAmounts({ ...contributionAmounts, [goal.id]: e.target.value })}
                                    style={{
                                        flex: 1,
                                        padding: '0.45rem 0.75rem',
                                        background: '#0f172a',
                                        border: '1px solid #475569',
                                        borderRadius: '6px',
                                        color: '#fff'
                                    }}
                                />
                                <button
                                    onClick={() => handleContribute(goal.id)}
                                    style={{
                                        background: '#10b981',
                                        color: '#fff',
                                        border: 'none',
                                        borderRadius: '6px',
                                        padding: '0.45rem 0.9rem',
                                        cursor: 'pointer',
                                        fontWeight: '600'
                                    }}
                                >
                                    + Add
                                </button>
                            </div>
                        )}
                    </div>
                );
            })}

            {/* Quick Add Goal Form */}
            <form onSubmit={handleCreateGoal} style={{ marginTop: '1.25rem', paddingTop: '1rem', borderTop: '1px dashed #475569' }}>
                <h4 style={{ color: '#f8fafc', marginBottom: '0.75rem', fontSize: '0.95rem' }}>🎯 Create New Trip / Event Savings Goal</h4>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem' }}>
                    <input
                        type="text"
                        placeholder="Goal title (e.g. Goa Scuba Diving / Smart TV)"
                        value={newGoalTitle}
                        onChange={(e) => setNewGoalTitle(e.target.value)}
                        required
                        style={{
                            padding: '0.5rem 0.75rem',
                            background: '#0f172a',
                            border: '1px solid #475569',
                            borderRadius: '6px',
                            color: '#fff'
                        }}
                    />
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <input
                            type="number"
                            placeholder="Target ₹ (e.g. 15000)"
                            value={newGoalTarget}
                            onChange={(e) => setNewGoalTarget(e.target.value)}
                            required
                            style={{
                                flex: 1,
                                padding: '0.5rem 0.75rem',
                                background: '#0f172a',
                                border: '1px solid #475569',
                                borderRadius: '6px',
                                color: '#fff'
                            }}
                        />
                        <button
                            type="submit"
                            style={{
                                background: '#4f46e5',
                                color: '#fff',
                                border: 'none',
                                borderRadius: '6px',
                                padding: '0.5rem 1.2rem',
                                cursor: 'pointer',
                                fontWeight: '600'
                            }}
                        >
                            Create Goal
                        </button>
                    </div>
                </div>
            </form>
        </div>
    );
}

// Mount React Component to DOM container
const rootElement = document.getElementById('reactSavingsTrackerRoot');
if (rootElement) {
    const groupId = rootElement.getAttribute('data-group-id') || 1;
    ReactDOM.createRoot(rootElement).render(<SavingsTracker initialGroupId={groupId} />);
}
