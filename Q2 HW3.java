import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class SavingsAccount {
    private int balance;
    private boolean preferredWithdrawalInProgress;
    private final Lock lock = new ReentrantLock();
    private final Condition sufficientBalance = lock.newCondition();
    private final Condition noPreferredWithdrawal = lock.newCondition();

    public SavingsAccount(int initialBalance) {
        this.balance = initialBalance;
        this.preferredWithdrawalInProgress = false;
    }

    public void deposit(int k) {
        lock.lock();
        try {
            balance += k;
            sufficientBalance.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(int k, boolean isPreferred) throws InterruptedException {
        lock.lock();
        try {
            if (isPreferred) {
                while (balance < k || preferredWithdrawalInProgress) {
                    noPreferredWithdrawal.await();
                }
                preferredWithdrawalInProgress = true;
            } else {
                while (balance < k || preferredWithdrawalInProgress) {
                    sufficientBalance.await();
                }
            }
            balance -= k;
        } finally {
            lock.unlock();
        }
    }

    public void finishPreferredWithdrawal() {
        lock.lock();
        try {
            preferredWithdrawalInProgress = false;
            noPreferredWithdrawal.signalAll();
        } finally {
            lock.unlock();
        }
    }
}

