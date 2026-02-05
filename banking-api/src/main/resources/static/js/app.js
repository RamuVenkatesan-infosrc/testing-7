// Use relative path when served from same origin, or absolute for standalone
const API_BASE_URL = new URL('/api', window.location.origin).href;

// Global state
let allAccounts = [];
let currentTransactionTab = 'deposit';
let confirmCallback = null;

// Theme management
function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('theme', newTheme);
    
    const themeIcon = document.querySelector('.theme-toggle i');
    themeIcon.className = newTheme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
}

// Initialize theme
function initTheme() {
    const savedTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);
    const themeIcon = document.querySelector('.theme-toggle i');
    if (themeIcon) {
        themeIcon.className = savedTheme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
    }
}

// Tab management
function showTab(tabName) {
    // Hide all tabs
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
    });

    // Show selected tab
    document.getElementById(`${tabName}-tab`).classList.add('active');
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach((item, index) => {
        const tabs = ['dashboard', 'accounts', 'transactions', 'transfer'];
        if (tabs[index] === tabName) {
            item.classList.add('active');
        }
    });

    // Load data when switching tabs
    if (tabName === 'dashboard') {
        loadDashboard();
    } else if (tabName === 'accounts') {
        loadAccounts();
    }
}

// Toast notification
function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    const toastIcon = toast.querySelector('.toast-icon');
    const toastMessage = toast.querySelector('.toast-message');
    
    const icons = {
        success: 'fa-check-circle',
        error: 'fa-exclamation-circle',
        info: 'fa-info-circle'
    };
    
    toastIcon.className = `toast-icon fas ${icons[type]}`;
    toastMessage.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 5000);
}

function hideToast() {
    document.getElementById('toast').classList.remove('show');
}

// Loading overlay
function showLoading() {
    document.getElementById('loadingOverlay').classList.add('show');
}

function hideLoading() {
    document.getElementById('loadingOverlay').classList.remove('show');
}

// Confirmation modal
function showConfirmModal(title, message, callback) {
    document.getElementById('confirmTitle').textContent = title;
    document.getElementById('confirmMessage').textContent = message;
    document.getElementById('confirmModal').classList.add('show');
    confirmCallback = callback;
}

function hideConfirmModal() {
    document.getElementById('confirmModal').classList.remove('show');
    confirmCallback = null;
}

function confirmAction() {
    if (confirmCallback && typeof confirmCallback === 'function') {
        confirmCallback();
    }
    hideConfirmModal();
}

// API Helper
async function apiCall(endpoint, method = 'GET', body = null) {
    try {
        const options = {
            method,
            headers: {
                'Content-Type': 'application/json',
            }
        };
        if (body) {
            options.body = JSON.stringify(body);
        }
        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
        if (!response.ok) {
            const errorText = await response.text();
            let errorMessage = `HTTP error! status: ${response.status}`;
            try {
                const errorJson = JSON.parse(errorText);
                errorMessage = errorJson.message || errorText;
            } catch {
                errorMessage = errorText || errorMessage;
            }
            throw new Error(errorMessage);
        }
        return await response.json();
    } catch (error) {
        showToast(`Error: ${error.message}`, 'error');
        throw error;
    }
}

// Format currency
function formatCurrency(amount, currency = 'USD') {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: currency
    }).format(amount);
}

// Format account ID (short version)
function formatAccountId(accountId) {
    return accountId ? `${accountId.substring(0, 4)}-${accountId.substring(4, 8)}-${accountId.substring(8, 12)}-${accountId.substring(12, 16)}` : '';
}

// Populate account dropdowns
function populateAccountDropdowns() {
    const selects = ['depositAccountId', 'withdrawAccountId', 'fromAccountId', 'toAccountId', 'historyAccountId'];
    selects.forEach(selectId => {
        const select = document.getElementById(selectId);
        if (select) {
            const currentValue = select.value;
            select.innerHTML = '<option value="">Select account</option>';
            allAccounts.forEach(account => {
                const option = document.createElement('option');
                option.value = account.accountId;
                option.textContent = `${formatAccountId(account.accountId)} - ${DOMPurify.sanitize(account.accountType)} (${formatCurrency(account.balance, account.currency)})`;
                if (account.accountId === currentValue) {
                    option.selected = true;
                }
                select.appendChild(option);
            });
        }
    });
}

// Load Dashboard
async function loadDashboard() {
    try {
        showLoading();
        const accounts = await apiCall('/accounts');
        allAccounts = accounts;
        
        // Calculate stats
        const totalAccounts = accounts.length;
        const activeAccounts = accounts.filter(acc => acc.active).length;
        const totalBalance = accounts.reduce((sum, acc) => sum + acc.balance, 0);
        
        // Update header stats
        document.getElementById('headerTotalBalance').textContent = formatCurrency(totalBalance);
        document.getElementById('headerAccountCount').textContent = totalAccounts;
        
        // Update dashboard stats
        document.getElementById('totalAccounts').textContent = totalAccounts;
        document.getElementById('activeAccounts').textContent = activeAccounts;
        document.getElementById('totalBalance').textContent = formatCurrency(totalBalance);
        document.getElementById('totalTransactions').textContent = 'N/A'; // Could be calculated if needed
        
        // Display recent accounts
        const dashboardAccounts = document.getElementById('dashboardAccounts');
        if (accounts.length === 0) {
            dashboardAccounts.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-wallet"></i>
                    <p>No accounts found. Create your first account to get started.</p>
                </div>
            `;
        } else {
            dashboardAccounts.innerHTML = accounts.slice(0, 4).map(account => `
                <div class="account-card">
                    <div class="account-header">
                        <span class="account-type">${DOMPurify.sanitize(account.accountType)}</span>
                        <span class="account-status ${account.active ? 'active' : 'inactive'}">
                            ${account.active ? 'Active' : 'Inactive'}
                        </span>
                    </div>
                    <div class="account-balance">
                        <div class="account-balance-label">Available Balance</div>
                        <div class="account-balance-amount">${formatCurrency(account.balance, account.currency)}</div>
                    </div>
                    <div class="account-id">
                        <strong>Account:</strong> ${formatAccountId(account.accountId)}
                    </div>
                </div>
            `).join('');
        }
        
        // Load recent activity (placeholder for now)
        const recentActivity = document.getElementById('recentActivity');
        recentActivity.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-chart-line"></i>
                <p>No recent activity</p>
            </div>
        `;
        
    } catch (error) {
        console.error('Error loading dashboard:', error);
    } finally {
        hideLoading();
    }
}

// Account Management
document.getElementById('createAccountForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        showLoading();
        const account = await apiCall('/accounts', 'POST', {
            customerId: document.getElementById('customerId').value,
            accountType: document.getElementById('accountType').value,
            initialBalance: parseFloat(document.getElementById('initialBalance').value),
            currency: document.getElementById('currency').value
        });
        showToast(`Account created successfully! Account: ${formatAccountId(account.accountId)}`, 'success');
        document.getElementById('createAccountForm').reset();
        await loadAccounts();
        await loadDashboard();
    } catch (error) {
        // Error already shown by apiCall
    } finally {
        hideLoading();
    }
});

async function loadAccounts() {
    try {
        showLoading();
        const accounts = await apiCall('/accounts');
        allAccounts = accounts;
        populateAccountDropdowns();
        
        const accountsList = document.getElementById('accountsList');
        if (accounts.length === 0) {
            accountsList.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-wallet"></i>
                    <p>No accounts found. Create your first account to get started.</p>
                </div>
            `;
            return;
        }
        
        displayFilteredAccounts(accounts);
    } catch (error) {
        console.error('Error loading accounts:', error);
    } finally {
        hideLoading();
    }
}

document.getElementById('customerAccountsForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        const customerId = document.getElementById('customerIdSearch').value;
        const accounts = await apiCall(`/accounts/customer/${customerId}`);
        const customerAccountsList = document.getElementById('customerAccountsList');
        if (accounts.length === 0) {
            customerAccountsList.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-search"></i>
                    <p>No accounts found for customer ${DOMPurify.sanitize(customerId)}.</p>
                </div>
            `;
            return;
        }
        customerAccountsList.innerHTML = accounts.map(account => `
            <div class="account-card">
                <div class="account-header">
                    <span class="account-type">${DOMPurify.sanitize(account.accountType)}</span>
                    <span class="account-status ${account.active ? 'active' : 'inactive'}">
                        ${account.active ? 'Active' : 'Inactive'}
                    </span>
                </div>
                <div class="account-balance">
                    <div class="account-balance-label">Available Balance</div>
                    <div class="account-balance-amount">${formatCurrency(account.balance, account.currency)}</div>
                </div>
                <div class="account-id">
                    <strong>Account:</strong> ${formatAccountId(account.accountId)}
                </div>
            </div>
        `).join('');
    } catch (error) {
        // Error already shown by apiCall
    }
});

// Transaction Management
document.getElementById('depositForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        showLoading();
        const accountId = document.getElementById('depositAccountId').value;
        const account = allAccounts.find(acc => acc.accountId === accountId);
        const currency = account ? account.currency : 'USD';
        
        const transaction = await apiCall('/transactions/deposit', 'POST', {
            accountId: accountId,
            amount: parseFloat(document.getElementById('depositAmount').value),
            currency: currency,
            description: document.getElementById('depositDescription').value
        });
        showToast(`Deposit successful! Amount: ${formatCurrency(transaction.amount, transaction.currency)}`, 'success');
        document.getElementById('depositForm').reset();
        await loadAccounts();
        await loadDashboard();
    } catch (error) {
        // Error already shown by apiCall
    } finally {
        hideLoading();
    }
});

document.getElementById('withdrawForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        showLoading();
        const accountId = document.getElementById('withdrawAccountId').value;
        const account = allAccounts.find(acc => acc.accountId === accountId);
        const currency = account ? account.currency : 'USD';
        
        const transaction = await apiCall('/transactions/withdraw', 'POST', {
            accountId: accountId,
            amount: parseFloat(document.getElementById('withdrawAmount').value),
            currency: currency,
            description: document.getElementById('withdrawDescription').value
        });
        showToast(`Withdrawal successful! Amount: ${formatCurrency(transaction.amount, transaction.currency)}`, 'success');
        document.getElementById('withdrawForm').reset();
        await loadAccounts();
        await loadDashboard();
    } catch (error) {
        // Error already shown by apiCall
    } finally {
        hideLoading();
    }
});

document.getElementById('transferForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    if (!validateTransferForm()) {
        return;
    }
    
    const fromAccountId = document.getElementById('fromAccountId').value;
    const toAccountId = document.getElementById('toAccountId').value;
    const amount = parseFloat(document.getElementById('transferAmount').value);
    const description = document.getElementById('transferDescription').value;
    
    showConfirmModal(
        'Confirm Transfer',
        `Transfer ${formatCurrency(amount)} from ${formatAccountId(fromAccountId)} to ${formatAccountId(toAccountId)}?`,
        async () => {
            try {
                showLoading();
                const fromAccount = allAccounts.find(acc => acc.accountId === fromAccountId);
                const currency = fromAccount ? fromAccount.currency : 'USD';
                
                const transaction = await apiCall('/transactions/transfer', 'POST', {
                    fromAccountId: fromAccountId,
                    toAccountId: toAccountId,
                    amount: amount,
                    currency: currency,
                    description: description
                });
                
                showToast(`Transfer successful! Amount: ${formatCurrency(transaction.amount, transaction.currency)}`, 'success');
                document.getElementById('transferForm').reset();
                document.getElementById('transferSummary').style.display = 'none';
                await loadAccounts();
                await loadDashboard();
            } catch (error) {
                // Error already shown by apiCall
            } finally {
                hideLoading();
            }
        }
    );
});

document.getElementById('transactionHistoryForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        const accountId = document.getElementById('historyAccountId').value;
        const transactions = await apiCall(`/transactions/account/${accountId}`);
        displayTransactionHistory(transactions);
    } catch (error) {
        // Error already shown by apiCall
    }
});