import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class SavingsAccount {
    private int balance;
    private final Lock lock = new ReentrantLock();
    private final Condition sufficientBalance = lock.newCondition();

    public SavingsAccount(int initialBalance) {
        this.balance = initialBalance;
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

    public void withdraw(int k) throws InterruptedException {
        lock.lock();
        try {
            while (balance < k) {
                sufficientBalance.await();
            }
            balance -= k;
        } finally {
            lock.unlock();
        }
    }
}

