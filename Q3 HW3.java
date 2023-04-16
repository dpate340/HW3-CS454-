import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class SavingsAccount {
    private int balance;
    private final Lock lock = new ReentrantLock();

    public SavingsAccount(int initialBalance) {
        this.balance = initialBalance;
    }

    public void deposit(int k) {
        lock.lock();
        try {
            balance += k;
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(int k) throws InterruptedException {
        lock.lock();
        try {
            while (balance < k) {
                lock.wait();
            }
            balance -= k;
        } finally {
            lock.unlock();
        }
    }

    public void transfer(int k, SavingsAccount reserve) throws InterruptedException {
        lock.lock();
        try {
            reserve.withdraw(k);
            deposit(k);
        } finally {
            lock.unlock();
        }
    }
}
