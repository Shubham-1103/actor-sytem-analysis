package src.service;

import org.springframework.stereotype.Service;

@Service
public class TestService {
    public void logTest(){
        System.out.println("Test Service can be injected..!");
    }
}
