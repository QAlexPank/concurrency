package ru.gb.qa.concurrency.service;

import ru.gb.qa.concurrency.model.Account;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MoneyTransferService {
    Lock locker = new ReentrantLock();
    Condition condition = locker.newCondition();

    public void transfer(Account from, Account to, Long amount) throws InterruptedException {
        locker.lock();
        try {
            while (from.getBalance() < amount) {
                System.out.println(String.format("Недостаточно денежных средств. Ожидаем поступление средств", from.getId(), amount - from.getBalance()));
                condition.await();
            }
            from.setBalance(from.getBalance() - amount);
            to.setBalance(to.getBalance() + amount);
        } finally {
            condition.signalAll();
            locker.unlock();
        }
    }
}

