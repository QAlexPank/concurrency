package ru.gb.qa.concurrency;

import ru.gb.qa.concurrency.model.Account;
import ru.gb.qa.concurrency.service.MoneyTransferService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

 class MoneyTransferServiceTest {

    @Test
    public void concurrentTest() throws InterruptedException, ExecutionException {
        MoneyTransferService moneyTransferService = new MoneyTransferService();
        Account from = new Account(1L, 1_000_000L);
        Account to = new Account(2L, 1_000_000L);

        ExecutorService executorService = Executors.newFixedThreadPool(100);
        List<MoneyTransferTask> tasks = new ArrayList<>();

        for (int i = 0; i < 500_000; i++) {
            tasks.add(new MoneyTransferTask(moneyTransferService, from, to, 1L));
            tasks.add(new MoneyTransferTask(moneyTransferService, to, from, 1L));
        }

        List< Future<Long> > futures = executorService.invokeAll(tasks);

        for (Future<Long> future : futures) {
            future.get();
        }


        Assertions.assertEquals(from.getBalance(), 1_000_000L);
        Assertions.assertEquals(to.getBalance(), 1_000_000L);

    }

    static class MoneyTransferTask implements Callable<Long> {
        MoneyTransferService moneyTransferService;
        Account from;
        Account to;
        Long amount;

        public MoneyTransferTask(MoneyTransferService moneyTransferService, Account from, Account to, Long amount) {
            this.moneyTransferService = moneyTransferService;
            this.from = from;
            this.to = to;
            this.amount = amount;
        }

        @Override
        public Long call() throws Exception {
            moneyTransferService.transfer(from, to, amount);
            return from.getBalance();
        }
    }

}
