package src.controller;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import src.commands.managerWorker.ManagerCommand;
import src.commands.managerWorker.impl.InstructionCommand;
import src.service.QueryBalanceInfoService;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;


@Slf4j
@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private ActorSystem<ManagerCommand> actorSystem;

    @Autowired
    private QueryBalanceInfoService queryBalanceInfoService;

    private Map<String, String> results;


    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> runActors() throws ExecutionException, InterruptedException {

        long startTime = System.currentTimeMillis();
        // do not test with for loop as bean needs to be created every time.
        log.info("ActorModel in action fetching balance info started.....!");
        CompletionStage<ConcurrentHashMap<String, String>> fetchBalances = AskPattern.ask(actorSystem,
                (me) -> new InstructionCommand("fetchBalances", me),
                Duration.ofMillis(10000000), actorSystem.scheduler());

        // callbacks

//        fetchBalances.whenComplete((result, error) -> {
//            if (result != null) {
//                System.out.println("Printing the final response..!");
//                System.out.println(result);
//                this.results = result;
//            } else {
//                System.out.println("time out executing the downstream API's");
//            }
//        });
//         blocking call
        Map<String, String> stringStringMap = fetchBalances.toCompletableFuture().get();
        log.info("{}",stringStringMap);
        log.info("Time taken to process the request : %s%n", System.currentTimeMillis() - startTime + " ");
        log.info("Executing the main thread...!");
//         main thread waiting for task to complete


        return ResponseEntity.of(Optional.of(stringStringMap));
    }
}
