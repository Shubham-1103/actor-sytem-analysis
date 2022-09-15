package src.actors.mangerWorker;

import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import src.commands.managerWorker.impl.BalanceResultCommand;
import src.commands.managerWorker.impl.WorkerCommand;
import src.service.QueryBalanceInfoService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class QueryBalanceInfo extends AbstractBehavior<WorkerCommand> {


    private final QueryBalanceInfoService queryBalanceInfoService;
    private final Executor executor;

    private QueryBalanceInfo(ActorContext<WorkerCommand> context, QueryBalanceInfoService queryBalanceInfoService) {
        super(context);
        this.queryBalanceInfoService = queryBalanceInfoService;
        executor = context.getSystem()
                .dispatchers()
                .lookup(DispatcherSelector.fromConfig("my-blocking-dispatcher"));
    }

    public static Behavior<WorkerCommand> create(QueryBalanceInfoService queryBalanceInfoService) {
        return Behaviors.setup(ctx -> new QueryBalanceInfo(ctx, queryBalanceInfoService));
    }

    @Override
    public Receive<WorkerCommand> createReceive() {
        return newReceiveBuilder()
                .onAnyMessage(message -> {
                    System.out.println("Query Wallet Service worker is executing with thread id: " + Thread.currentThread().getName());
                    if (message.getMessage().equals("QueryBalanceInfo")) {
                        triggerFutureBlockingOperation(message);
                    }
                    return Behaviors.same();
                })
                .build();
    }

    private void triggerFutureBlockingOperation(WorkerCommand message) {
        CompletableFuture.supplyAsync(() -> {
                    System.out.println("Query Wallet Service worker async task is executing with thread id: " + Thread.currentThread().getName());
                    return queryBalanceInfoService.queryBalance();
                },executor)
                .thenAccept(result -> {
                    System.out.println("result form Wallet Service ==== \n logged by QueryBalanceInfo Worker: " + result);
                    message.getSender()
                            .tell(new BalanceResultCommand("walletService", result, getContext().getSelf()));
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                })
                .thenRun(() -> System.out.println("Last action of the Query Balance info worker!"));

    }
}
