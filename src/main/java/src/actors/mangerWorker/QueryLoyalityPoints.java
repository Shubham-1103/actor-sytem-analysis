package src.actors.mangerWorker;

import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.extern.slf4j.Slf4j;
import src.commands.managerWorker.impl.BalanceResultCommand;
import src.commands.managerWorker.impl.WorkerCommand;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
public class QueryLoyalityPoints extends AbstractBehavior<WorkerCommand> {

    private final Executor executor;

    private QueryLoyalityPoints(ActorContext<WorkerCommand> context) {
        super(context);
        executor = context
                .getSystem()
                .dispatchers()
                .lookup(DispatcherSelector.fromConfig("my-blocking-dispatcher"));
    }

    public static Behavior<WorkerCommand> create() {
        return Behaviors.setup(QueryLoyalityPoints::new);
    }

    @Override
    public Receive<WorkerCommand> createReceive() {
        return newReceiveBuilder()
                .onAnyMessage(message -> {
                    if (message.getMessage().equals("QueryLoyalityPoints")) {
                        extracted(message);
                    }
                    return Behaviors.same();
                })
                .build();
    }

    private void extracted(WorkerCommand message) {
        CompletableFuture.supplyAsync(() -> {
                    log.info("Query Loyalty Points worker is executing with thread id: " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("Sending the computed results form loyalty point balance worker to Manger Parent...!");
                    message.getSender()
                            .tell(new BalanceResultCommand("loyaltyPointsService", "Balance:200",
                                    getContext().getSelf()));
                    return null;
                },executor)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                })
                .thenRun(() -> log.info("Last action of the Query loyalty points  worker!"));

    }
}
