package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryBalanceInfoService {
    @Autowired
    private TestService testService;

    public String queryBalance() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        testService.logTest();
        return "balanceAmount=200";
    }
}
