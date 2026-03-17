// ─── STATE ───────────────────────────────────────────────────────────────────
const API = '';
let token = localStorage.getItem('ft_token') || '';
let currentUser = null;
let allTransactions = [];
let pieChart = null, barChart = null, trendExpChart = null, trendIncChart = null, catChart = null;

const CATEGORIES = {
  INCOME: ['SALARY','FREELANCE','INVESTMENT','BONUS','OTHER_INCOME'],
  EXPENSE: ['RENT','FOOD','TRANSPORT','ENTERTAINMENT','SHOPPING',
            'UTILITIES','HEALTHCARE','EDUCATION','TRAVEL','INSURANCE','SAVINGS','OTHER_EXPENSE'],
  ALL: ['SALARY','FREELANCE','INVESTMENT','BONUS','OTHER_INCOME',
        'RENT','FOOD','TRANSPORT','ENTERTAINMENT','SHOPPING',
        'UTILITIES','HEALTHCARE','EDUCATION','TRAVEL','INSURANCE','SAVINGS','OTHER_EXPENSE']
};

const CAT_COLORS = {
  RENT:'#f87171', FOOD:'#fb923c', TRANSPORT:'#fbbf24', ENTERTAINMENT:'#a3e635',
  SHOPPING:'#34d399', UTILITIES:'#22d3ee', HEALTHCARE:'#60a5fa', EDUCATION:'#818cf8',
  TRAVEL:'#c084fc', INSURANCE:'#f472b6', SAVINGS:'#4ade80', OTHER_EXPENSE:'#94a3b8',
  SALARY:'#4ade80', FREELANCE:'#34d399', INVESTMENT:'#22d3ee', BONUS:'#fbbf24', OTHER_INCOME:'#a3e635'
};

// ─── HTTP HELPERS ─────────────────────────────────────────────────────────────
async function http(method, path, body) {
  const res = await fetch(API + path, {
    method,
    headers: { 'Content-Type':'application/json', ...(token ? {'Authorization':'Bearer '+token} : {}) },
    body: body ? JSON.stringify(body) : undefined
  });
  if (res.status === 401) { doLogout(); return null; }
  const text = await res.text();
  try { return JSON.parse(text); } catch { return text; }
}
const get  = path       => http('GET',    path);
const post = (path, b)  => http('POST',   path, b);
const put  = (path, b)  => http('PUT',    path, b);
const del  = path       => http('DELETE', path);
const patch= (path, b)  => http('PATCH',  path, b);

async function getBlob(path) {
  const res = await fetch(API + path, { headers: {'Authorization':'Bearer '+token} });
  return res;
}

// ─── AUTH ─────────────────────────────────────────────────────────────────────
function switchAuthTab(tab) {
  document.querySelectorAll('.auth-tab').forEach((t,i) => t.classList.toggle('active', (i===0&&tab==='login')||(i===1&&tab==='register')));
  document.getElementById('login-form').style.display = tab==='login' ? '' : 'none';
  document.getElementById('register-form').style.display = tab==='register' ? '' : 'none';
}

async function doLogin() {
  const email = document.getElementById('login-email').value;
  const password = document.getElementById('login-password').value;
  const res = await post('/api/auth/login', { email, password });
  if (res?.token) {
    token = res.token; localStorage.setItem('ft_token', token);
    await initApp(res);
  } else {
    document.getElementById('login-error').textContent = res?.error || 'Login failed';
  }
}

async function doRegister() {
  const body = {
    name: document.getElementById('reg-name').value,
    email: document.getElementById('reg-email').value,
    password: document.getElementById('reg-password').value,
    monthlyIncome: parseFloat(document.getElementById('reg-income').value) || 0,
    currency: document.getElementById('reg-currency').value
  };
  const res = await post('/api/auth/register', body);
  if (res?.token) {
    token = res.token; localStorage.setItem('ft_token', token);
    await initApp(res);
  } else {
    document.getElementById('register-error').textContent = res?.error || 'Registration failed';
  }
}

function doLogout() {
  token = ''; localStorage.removeItem('ft_token');
  document.getElementById('app').style.display = 'none';
  document.getElementById('auth-screen').style.display = 'flex';
  currentUser = null; allTransactions = [];
}

// ─── APP INIT ─────────────────────────────────────────────────────────────────
async function initApp(authData) {
  document.getElementById('auth-screen').style.display = 'none';
  document.getElementById('app').style.display = 'flex';

  currentUser = await get('/api/auth/me');
  if (!currentUser) return;

  document.getElementById('sidebar-name').textContent = currentUser.name;
  document.getElementById('sidebar-role').textContent = currentUser.role;
  document.getElementById('sidebar-avatar').textContent = currentUser.name.charAt(0).toUpperCase();

  populateCategorySelects();
  setDefaultDates();
  await loadDashboard();
}

function populateCategorySelects() {
  // Transaction category - dynamic based on type
  updateTxCategories();
  document.getElementById('tx-type').addEventListener('change', updateTxCategories);

  // Budget categories (expenses only)
  const budgetCat = document.getElementById('budget-category');
  budgetCat.innerHTML = CATEGORIES.EXPENSE.map(c => `<option value="${c}">${fmtCat(c)}</option>`).join('');

  // Filter category
  const filterCat = document.getElementById('tx-filter-cat');
  filterCat.innerHTML = '<option value="">All Categories</option>' +
    CATEGORIES.ALL.map(c => `<option value="${c}">${fmtCat(c)}</option>`).join('');
}

function updateTxCategories() {
  const type = document.getElementById('tx-type').value;
  const cats = CATEGORIES[type] || CATEGORIES.ALL;
  document.getElementById('tx-category').innerHTML =
    cats.map(c => `<option value="${c}">${fmtCat(c)}</option>`).join('');
}

function setDefaultDates() {
  const today = new Date().toISOString().split('T')[0];
  const firstDay = new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0];
  const lastDay = new Date(new Date().getFullYear(), new Date().getMonth()+1, 0).toISOString().split('T')[0];
  document.getElementById('tx-date').value = today;
  document.getElementById('budget-start').value = firstDay;
  document.getElementById('budget-end').value = lastDay;
}

// ─── NAVIGATION ───────────────────────────────────────────────────────────────
async function showPage(name) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
  document.getElementById('page-'+name).classList.add('active');
  const idx = ['dashboard','transactions','budget','savings','trends','export','forum','profile'].indexOf(name);
  document.querySelectorAll('.nav-item')[idx]?.classList.add('active');

  if (name === 'dashboard') await loadDashboard();
  else if (name === 'transactions') await loadTransactions();
  else if (name === 'budget') await loadBudgets();
  else if (name === 'savings') await loadGoals();
  else if (name === 'trends') await loadTrends();
  else if (name === 'forum') await loadForum();
  else if (name === 'profile') loadProfile();
}

// ─── DASHBOARD ────────────────────────────────────────────────────────────────
async function loadDashboard() {
  const now = new Date();
  const [summary, goals, transactions] = await Promise.all([
    get(`/api/transactions/summary?year=${now.getFullYear()}&month=${now.getMonth()+1}`),
    get('/api/savings-goals'),
    get('/api/transactions')
  ]);

  const fmt = v => `${currentUser?.currency||'$'} ${Number(v||0).toLocaleString('en', {minimumFractionDigits:2,maximumFractionDigits:2})}`;

  document.getElementById('dash-income').textContent = fmt(summary?.totalIncome);
  document.getElementById('dash-expense').textContent = fmt(summary?.totalExpenses);
  const bal = (summary?.netBalance || 0);
  const balEl = document.getElementById('dash-balance');
  balEl.textContent = fmt(bal);
  balEl.className = 'stat-value ' + (bal >= 0 ? 'positive' : 'negative');
  document.getElementById('dash-goals').textContent = goals?.filter(g => g.status === 'IN_PROGRESS').length || 0;

  const month = now.toLocaleString('default', { month:'long', year:'numeric' });
  document.getElementById('dash-subtitle').textContent = `Financial snapshot — ${month}`;

  // Pie chart
  if (summary?.expensesByCategory) {
    const labels = Object.keys(summary.expensesByCategory).map(fmtCat);
    const data   = Object.values(summary.expensesByCategory).map(Number);
    const colors = Object.keys(summary.expensesByCategory).map(k => CAT_COLORS[k] || '#94a3b8');
    if (pieChart) pieChart.destroy();
    pieChart = new Chart(document.getElementById('pie-chart'), {
      type:'doughnut',
      data: { labels, datasets:[{ data, backgroundColor:colors, borderWidth:0, hoverOffset:8 }] },
      options: { plugins:{ legend:{ position:'right', labels:{ color:'#94a3b8', font:{size:11} } } }, cutout:'65%' }
    });
  }

  // Bar chart – last 6 months
  const [expTrend, incTrend] = await Promise.all([
    get('/api/transactions/trends?type=EXPENSE'),
    get('/api/transactions/trends?type=INCOME')
  ]);
  const months = Object.keys(expTrend || {}).slice(0,6).reverse();
  if (barChart) barChart.destroy();
  barChart = new Chart(document.getElementById('bar-chart'), {
    type:'bar',
    data: {
      labels: months.map(m => { const [y,mo]=m.split('-'); return new Date(y,mo-1).toLocaleString('default',{month:'short'}); }),
      datasets: [
        { label:'Income', data: months.map(m => incTrend?.[m]||0), backgroundColor:'rgba(74,222,128,0.7)', borderRadius:4 },
        { label:'Expenses', data: months.map(m => expTrend?.[m]||0), backgroundColor:'rgba(248,113,113,0.7)', borderRadius:4 }
      ]
    },
    options: { plugins:{ legend:{ labels:{ color:'#94a3b8' } } }, scales:{ x:{ ticks:{ color:'#94a3b8' }, grid:{color:'rgba(255,255,255,0.05)'} }, y:{ ticks:{ color:'#94a3b8' }, grid:{color:'rgba(255,255,255,0.05)'} } } }
  });

  // Recent transactions table
  allTransactions = transactions || [];
  const tbody = document.getElementById('dash-transactions');
  tbody.innerHTML = allTransactions.slice(0,8).map(t => `
    <tr>
      <td>${t.date}</td>
      <td>${t.description || '—'}</td>
      <td><span class="badge badge-info">${fmtCat(t.category)}</span></td>
      <td><span class="badge ${t.type==='INCOME'?'badge-income':'badge-expense'}">${t.type}</span></td>
      <td style="font-weight:600;color:${t.type==='INCOME'?'var(--green)':'var(--red)'}">${t.type==='INCOME'?'+':'-'} ${fmt(t.amount)}</td>
    </tr>`).join('') || '<tr><td colspan="5" style="text-align:center;color:var(--muted);padding:32px">No transactions yet</td></tr>';
}

// ─── TRANSACTIONS ─────────────────────────────────────────────────────────────
async function loadTransactions() {
  allTransactions = await get('/api/transactions') || [];
  renderTransactions();
}

function filterTransactions() { renderTransactions(); }

function renderTransactions() {
  const type   = document.getElementById('tx-filter-type').value;
  const cat    = document.getElementById('tx-filter-cat').value;
  const search = document.getElementById('tx-search').value.toLowerCase();
  const fmt = v => `${currentUser?.currency||'$'} ${Number(v).toLocaleString('en',{minimumFractionDigits:2,maximumFractionDigits:2})}`;

  const filtered = allTransactions.filter(t =>
    (!type   || t.type === type) &&
    (!cat    || t.category === cat) &&
    (!search || (t.description||'').toLowerCase().includes(search) || t.category.toLowerCase().includes(search))
  );

  document.getElementById('tx-table').innerHTML = filtered.map(t => `
    <tr>
      <td>${t.date}</td>
      <td>${t.description || '—'}</td>
      <td><span class="badge badge-info">${fmtCat(t.category)}</span></td>
      <td><span class="badge ${t.type==='INCOME'?'badge-income':'badge-expense'}">${t.type}</span></td>
      <td style="font-weight:600;color:${t.type==='INCOME'?'var(--green)':'var(--red)'}">${t.type==='INCOME'?'+':'-'} ${fmt(t.amount)}</td>
      <td>
        <button class="btn btn-ghost btn-sm" onclick="editTx(${t.id})">Edit</button>
        <button class="btn btn-danger btn-sm" onclick="deleteTx(${t.id})" style="margin-left:4px">Delete</button>
      </td>
    </tr>`).join('') || '<tr><td colspan="6" style="text-align:center;color:var(--muted);padding:32px">No transactions found</td></tr>';
}

function openTxModal(id) {
  document.getElementById('tx-modal-title').textContent = 'Add Transaction';
  document.getElementById('tx-edit-id').value = '';
  document.getElementById('tx-type').value = 'EXPENSE';
  document.getElementById('tx-amount').value = '';
  document.getElementById('tx-desc').value = '';
  document.getElementById('tx-account').value = '';
  document.getElementById('tx-date').value = new Date().toISOString().split('T')[0];
  updateTxCategories();
  openModal('tx-modal');
}

function editTx(id) {
  const t = allTransactions.find(x => x.id === id);
  if (!t) return;
  document.getElementById('tx-modal-title').textContent = 'Edit Transaction';
  document.getElementById('tx-edit-id').value = id;
  document.getElementById('tx-type').value = t.type;
  updateTxCategories();
  document.getElementById('tx-category').value = t.category;
  document.getElementById('tx-amount').value = t.amount;
  document.getElementById('tx-desc').value = t.description || '';
  document.getElementById('tx-account').value = t.account || '';
  document.getElementById('tx-date').value = t.date;
  openModal('tx-modal');
}

async function saveTx() {
  const id = document.getElementById('tx-edit-id').value;
  const body = {
    type: document.getElementById('tx-type').value,
    amount: parseFloat(document.getElementById('tx-amount').value),
    category: document.getElementById('tx-category').value,
    description: document.getElementById('tx-desc').value,
    account: document.getElementById('tx-account').value,
    date: document.getElementById('tx-date').value
  };
  const res = id ? await put(`/api/transactions/${id}`, body) : await post('/api/transactions', body);
  if (res?.id) {
    closeModal('tx-modal');
    toast('Transaction saved!', 'success');
    await loadTransactions();
  } else { toast('Failed to save transaction', 'error'); }
}

async function deleteTx(id) {
  if (!confirm('Delete this transaction?')) return;
  await del(`/api/transactions/${id}`);
  toast('Transaction deleted', 'success');
  await loadTransactions();
}

// ─── BUDGET ───────────────────────────────────────────────────────────────────
async function loadBudgets() {
  const budgets = await get('/api/budgets') || [];
  const fmt = v => `${currentUser?.currency||'$'} ${Number(v||0).toLocaleString('en',{minimumFractionDigits:2,maximumFractionDigits:2})}`;
  document.getElementById('budget-list').innerHTML = budgets.length ? budgets.map(b => {
    const pct = Math.min(b.progressPercentage, 100);
    const over = b.progressPercentage > 100;
    return `<div class="card" style="margin-bottom:14px">
      <div style="display:flex;justify-content:space-between;align-items:start;margin-bottom:10px">
        <div>
          <div style="font-family:Syne,sans-serif;font-weight:700;font-size:1rem">${fmtCat(b.category)}</div>
          <div style="font-size:.82rem;color:var(--muted);margin-top:2px">${b.startDate} – ${b.endDate}</div>
        </div>
        <div style="text-align:right">
          <div style="font-weight:700;color:${over?'var(--red)':'var(--text)'}">${fmt(b.spentAmount)} <span style="color:var(--muted);font-weight:400">/ ${fmt(b.budgetAmount)}</span></div>
          <div style="font-size:.82rem;color:${b.remainingAmount<0?'var(--red)':'var(--green)'}">${b.remainingAmount<0?'Over by '+fmt(-b.remainingAmount):'Remaining: '+fmt(b.remainingAmount)}</div>
        </div>
      </div>
      <div class="progress-bar"><div class="progress-fill ${over?'over':''}" style="width:${pct}%"></div></div>
      <div style="display:flex;justify-content:space-between;margin-top:10px">
        <div style="font-size:.8rem;color:var(--muted)">${b.progressPercentage.toFixed(1)}% used</div>
        <button class="btn btn-danger btn-sm" onclick="deleteBudget(${b.id})">Remove</button>
      </div>
    </div>`;
  }).join('') : `<div class="empty-state"><p>No budgets set yet. Add one to start tracking!</p></div>`;
}

function openBudgetModal() { openModal('budget-modal'); }

async function saveBudget() {
  const body = {
    category: document.getElementById('budget-category').value,
    amount: parseFloat(document.getElementById('budget-amount').value),
    startDate: document.getElementById('budget-start').value,
    endDate: document.getElementById('budget-end').value
  };
  const res = await post('/api/budgets', body);
  if (res?.id) { closeModal('budget-modal'); toast('Budget saved!', 'success'); await loadBudgets(); }
  else { toast('Failed to save budget', 'error'); }
}

async function deleteBudget(id) {
  if (!confirm('Remove this budget?')) return;
  await del(`/api/budgets/${id}`);
  toast('Budget removed', 'success');
  await loadBudgets();
}

// ─── SAVINGS GOALS ────────────────────────────────────────────────────────────
async function loadGoals() {
  const goals = await get('/api/savings-goals') || [];
  const fmt = v => `${currentUser?.currency||'$'} ${Number(v||0).toLocaleString('en',{minimumFractionDigits:2,maximumFractionDigits:2})}`;
  document.getElementById('goals-grid').innerHTML = goals.length ? goals.map(g => {
    const pct = Math.min(g.progressPercentage, 100);
    const statusColor = g.status==='COMPLETED' ? 'var(--green)' : g.status==='PAUSED' ? 'var(--muted)' : 'var(--accent)';
    return `<div class="goal-card">
      <div style="display:flex;justify-content:space-between;align-items:start">
        <div class="goal-name">${g.goalName}</div>
        <span class="badge" style="background:rgba(255,255,255,0.06);color:${statusColor}">${g.status}</span>
      </div>
      ${g.description ? `<div style="font-size:.83rem;color:var(--muted);margin-top:4px">${g.description}</div>` : ''}
      ${g.deadline ? `<div style="font-size:.8rem;color:var(--muted);margin-top:4px">🗓 Deadline: ${g.deadline}</div>` : ''}
      <div class="progress-bar" style="margin-top:14px"><div class="progress-fill" style="width:${pct}%;background:${statusColor}"></div></div>
      <div class="goal-amounts">
        <span>${fmt(g.currentAmount)} saved</span>
        <span style="font-weight:600">${pct.toFixed(0)}%</span>
        <span>Goal: ${fmt(g.targetAmount)}</span>
      </div>
      <div class="goal-actions">
        ${g.status!=='COMPLETED'?`<button class="btn btn-accent btn-sm" onclick="openAddAmount(${g.id})">+ Add</button>`:''}
        <button class="btn btn-danger btn-sm" onclick="deleteGoal(${g.id})">Remove</button>
      </div>
    </div>`;
  }).join('') : `<div class="empty-state" style="grid-column:1/-1"><p>No savings goals yet. Create one to start tracking!</p></div>`;
}

function openGoalModal() { openModal('goal-modal'); }

async function saveGoal() {
  const body = {
    goalName: document.getElementById('goal-name').value,
    targetAmount: parseFloat(document.getElementById('goal-target').value),
    currentAmount: parseFloat(document.getElementById('goal-current').value) || 0,
    deadline: document.getElementById('goal-deadline').value || null,
    description: document.getElementById('goal-desc').value
  };
  const res = await post('/api/savings-goals', body);
  if (res?.id) { closeModal('goal-modal'); toast('Goal created!', 'success'); await loadGoals(); }
  else { toast('Failed to create goal', 'error'); }
}

function openAddAmount(id) {
  document.getElementById('add-amount-goal-id').value = id;
  document.getElementById('add-amount-value').value = '';
  openModal('add-amount-modal');
}

async function submitAddAmount() {
  const id = document.getElementById('add-amount-goal-id').value;
  const amount = parseFloat(document.getElementById('add-amount-value').value);
  const res = await patch(`/api/savings-goals/${id}/add-amount`, { amount });
  if (res?.id) { closeModal('add-amount-modal'); toast('Amount added!', 'success'); await loadGoals(); }
  else { toast('Failed to add amount', 'error'); }
}

async function deleteGoal(id) {
  if (!confirm('Remove this savings goal?')) return;
  await del(`/api/savings-goals/${id}`);
  toast('Goal removed', 'success');
  await loadGoals();
}

// ─── TRENDS ───────────────────────────────────────────────────────────────────
async function loadTrends() {
  const now = new Date();
  const [expTrend, incTrend, summary] = await Promise.all([
    get('/api/transactions/trends?type=EXPENSE'),
    get('/api/transactions/trends?type=INCOME'),
    get(`/api/transactions/summary?year=${now.getFullYear()}&month=${now.getMonth()+1}`)
  ]);

  const mkMonths = obj => {
    const keys = Object.keys(obj||{}).slice(0,6).reverse();
    return { labels: keys.map(m => { const [y,mo]=m.split('-'); return new Date(y,mo-1).toLocaleString('default',{month:'short',year:'2-digit'}); }), data: keys.map(k => obj[k]||0) };
  };

  const chartOpts = color => ({
    plugins:{ legend:{display:false} },
    scales:{
      x:{ ticks:{color:'#94a3b8'}, grid:{color:'rgba(255,255,255,0.05)'} },
      y:{ ticks:{color:'#94a3b8'}, grid:{color:'rgba(255,255,255,0.05)'} }
    },
    elements:{ line:{ tension:.4 }, point:{ radius:4, hoverRadius:6 } }
  });

  const exp = mkMonths(expTrend);
  if (trendExpChart) trendExpChart.destroy();
  trendExpChart = new Chart(document.getElementById('trend-expense-chart'), {
    type:'line',
    data:{ labels:exp.labels, datasets:[{ label:'Expenses', data:exp.data, borderColor:'#f87171', backgroundColor:'rgba(248,113,113,0.1)', fill:true }] },
    options: chartOpts('#f87171')
  });

  const inc = mkMonths(incTrend);
  if (trendIncChart) trendIncChart.destroy();
  trendIncChart = new Chart(document.getElementById('trend-income-chart'), {
    type:'line',
    data:{ labels:inc.labels, datasets:[{ label:'Income', data:inc.data, borderColor:'#4ade80', backgroundColor:'rgba(74,222,128,0.1)', fill:true }] },
    options: chartOpts('#4ade80')
  });

  if (summary?.expensesByCategory) {
    const labels = Object.keys(summary.expensesByCategory).map(fmtCat);
    const data   = Object.values(summary.expensesByCategory).map(Number);
    const colors = Object.keys(summary.expensesByCategory).map(k => CAT_COLORS[k]||'#94a3b8');
    if (catChart) catChart.destroy();
    catChart = new Chart(document.getElementById('trend-category-chart'), {
      type:'bar',
      data:{ labels, datasets:[{ data, backgroundColor:colors, borderRadius:6, borderSkipped:false }] },
      options:{ plugins:{legend:{display:false}}, scales:{ x:{ticks:{color:'#94a3b8'},grid:{color:'rgba(255,255,255,0.05)'}}, y:{ticks:{color:'#94a3b8'},grid:{color:'rgba(255,255,255,0.05)'}} } }
    });
  }
}

// ─── EXPORT ───────────────────────────────────────────────────────────────────
async function exportData(format) {
  toast(`Preparing ${format.toUpperCase()} export…`, 'info');
  const res = await getBlob(`/api/export/${format}`);
  if (res.ok) {
    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = `finance-${new Date().toISOString().split('T')[0]}.${format}`;
    a.click(); URL.revokeObjectURL(url);
    toast(`${format.toUpperCase()} downloaded!`, 'success');
  } else { toast('Export failed', 'error'); }
}

// ─── FORUM ────────────────────────────────────────────────────────────────────
async function loadForum() {
  const posts = await get('/api/forum/posts') || [];
  document.getElementById('forum-posts').innerHTML = posts.length ? posts.map(p => `
    <div class="forum-post" id="fp-${p.id}">
      <div class="post-meta">
        <div class="post-avatar">${p.authorName.charAt(0).toUpperCase()}</div>
        <div>
          <div class="post-author">${p.authorName}</div>
          <div class="post-time">${timeAgo(p.createdAt)}</div>
        </div>
      </div>
      <div class="post-title">${p.title}</div>
      <div class="post-content">${p.content}</div>
      <div class="post-actions">
        <button class="post-action" onclick="likePost(${p.id})">
          <svg width="14" height="14" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M14 9V5a3 3 0 00-3-3l-4 9v11h11.28a2 2 0 002-1.7l1.38-9a2 2 0 00-2-2.3H14z"/><path d="M7 22H4a2 2 0 01-2-2v-7a2 2 0 012-2h3"/></svg>
          ${p.likes} Likes
        </button>
        <button class="post-action" onclick="toggleComments(${p.id})">
          <svg width="14" height="14" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/></svg>
          ${p.commentCount} Comments
        </button>
      </div>
      <div class="comments-section" id="comments-${p.id}" style="display:none"></div>
    </div>`).join('') : `<div class="empty-state"><p>No posts yet. Be the first to share a tip!</p></div>`;
}

async function toggleComments(postId) {
  const el = document.getElementById(`comments-${postId}`);
  if (el.style.display === 'none') {
    el.style.display = 'block';
    const comments = await get(`/api/forum/posts/${postId}/comments`) || [];
    el.innerHTML = comments.map(c => `
      <div class="comment">
        <div class="post-meta" style="margin-bottom:6px">
          <div class="post-avatar" style="width:26px;height:26px;font-size:.7rem">${c.authorName.charAt(0).toUpperCase()}</div>
          <div>
            <span class="post-author" style="font-size:.85rem">${c.authorName}</span>
            <span class="post-time" style="margin-left:6px">${timeAgo(c.createdAt)}</span>
          </div>
        </div>
        <div style="font-size:.88rem;color:var(--muted);line-height:1.5">${c.comment}</div>
      </div>`).join('') +
      `<div style="margin-top:12px;display:flex;gap:8px">
        <input class="form-control" type="text" id="comment-input-${postId}" placeholder="Write a comment…" style="flex:1"/>
        <button class="btn btn-accent btn-sm" onclick="submitComment(${postId})">Post</button>
      </div>`;
  } else { el.style.display = 'none'; }
}

async function submitComment(postId) {
  const comment = document.getElementById(`comment-input-${postId}`).value;
  if (!comment.trim()) return;
  const res = await post(`/api/forum/posts/${postId}/comments`, { comment });
  if (res?.id) { await toggleComments(postId); toggleComments(postId); toast('Comment posted!', 'success'); }
}

async function likePost(id) {
  await post(`/api/forum/posts/${id}/like`);
  await loadForum();
}

function openPostModal() { openModal('post-modal'); }

async function savePost() {
  const res = await post('/api/forum/posts', {
    title: document.getElementById('post-title').value,
    content: document.getElementById('post-content').value
  });
  if (res?.id) { closeModal('post-modal'); toast('Post published!', 'success'); await loadForum(); }
  else { toast('Failed to publish post', 'error'); }
}

// ─── PROFILE ──────────────────────────────────────────────────────────────────
function loadProfile() {
  if (!currentUser) return;
  document.getElementById('prof-name').value = currentUser.name || '';
  document.getElementById('prof-income').value = currentUser.monthlyIncome || '';
  document.getElementById('prof-target').value = currentUser.targetExpenses || '';
  document.getElementById('prof-savings').value = currentUser.savingsTarget || '';
  document.getElementById('prof-currency').value = currentUser.currency || 'USD';
}

async function saveProfile() {
  const res = await put('/api/auth/profile', {
    name: document.getElementById('prof-name').value,
    monthlyIncome: parseFloat(document.getElementById('prof-income').value) || 0,
    targetExpenses: parseFloat(document.getElementById('prof-target').value) || 0,
    savingsTarget: parseFloat(document.getElementById('prof-savings').value) || 0,
    currency: document.getElementById('prof-currency').value
  });
  if (res?.id) {
    currentUser = { ...currentUser, ...res };
    document.getElementById('sidebar-name').textContent = res.name;
    toast('Profile updated!', 'success');
  } else { toast('Failed to update profile', 'error'); }
}

// ─── MODALS ───────────────────────────────────────────────────────────────────
function openModal(id) {
  document.getElementById(id).classList.add('open');
  document.body.style.overflow = 'hidden';
}
function closeModal(id) {
  document.getElementById(id).classList.remove('open');
  document.body.style.overflow = '';
}
document.querySelectorAll('.modal-overlay').forEach(o => {
  o.addEventListener('click', e => { if (e.target === o) closeModal(o.id); });
});

// ─── UTILS ────────────────────────────────────────────────────────────────────
function fmtCat(cat) {
  if (!cat) return '';
  return cat.replace(/_/g,' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
}

function timeAgo(dateStr) {
  const diff = (Date.now() - new Date(dateStr)) / 1000;
  if (diff < 60) return 'just now';
  if (diff < 3600) return Math.floor(diff/60) + 'm ago';
  if (diff < 86400) return Math.floor(diff/3600) + 'h ago';
  return Math.floor(diff/86400) + 'd ago';
}

function toast(msg, type='info') {
  const t = document.createElement('div');
  t.className = `toast toast-${type}`;
  t.textContent = msg;
  document.getElementById('toast-container').appendChild(t);
  setTimeout(() => t.remove(), 3500);
}

// ─── BOOTSTRAP ────────────────────────────────────────────────────────────────
(async () => {
  if (token) {
    const user = await get('/api/auth/me');
    if (user?.id) {
      document.getElementById('auth-screen').style.display = 'none';
      document.getElementById('app').style.display = 'flex';
      currentUser = user;
      document.getElementById('sidebar-name').textContent = user.name;
      document.getElementById('sidebar-role').textContent = user.role;
      document.getElementById('sidebar-avatar').textContent = user.name.charAt(0).toUpperCase();
      populateCategorySelects();
      setDefaultDates();
      await loadDashboard();
    } else {
      localStorage.removeItem('ft_token');
      token = '';
    }
  }
})();
