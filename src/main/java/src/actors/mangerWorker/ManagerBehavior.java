package src.actors.mangerWorker;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.extern.slf4j.Slf4j;
import src.commands.managerWorker.ManagerCommand;
import src.commands.managerWorker.impl.BalanceResultCommand;
import src.commands.managerWorker.impl.InstructionCommand;
import src.commands.managerWorker.impl.NoResponseFromWorkerCommand;
import src.commands.managerWorker.impl.WorkerCommand;
import src.service.QueryBalanceInfoService;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class ManagerBehavior extends AbstractBehavior<ManagerCommand> {
    private QueryBalanceInfoService queryBalanceInfoService; // service used by worker

    private ConcurrentHashMap<String, String> responseFromDownStream = new ConcurrentHashMap<>(); // collection to store the response of the workers.

    private ActorRef<ConcurrentHashMap<String, String>> responses; // reference of the manger to send the response back

    private ActorRef<WorkerCommand> balanceWorker;  // balance worker reference
    private ActorRef<WorkerCommand> loyalityPointWorker; // loyaltyPoint worker reference

    private ManagerBehavior(ActorContext<ManagerCommand> context, QueryBalanceInfoService queryBalanceInfoService) {
        super(context);
        this.queryBalanceInfoService = queryBalanceInfoService;
    }

    public static Behavior<ManagerCommand> create(QueryBalanceInfoService queryBalanceInfoService) {
        return Behaviors.setup(ctx -> new ManagerBehavior(ctx, queryBalanceInfoService));

    }

    @Override
    public Receive<ManagerCommand> createReceive() {
        return idealMessageHandler();
    }

    public Receive<ManagerCommand> idealMessageHandler() {
        return newReceiveBuilder()
//                .onSignal(Terminated.class, handler -> Behaviors.same())
                .onMessage(InstructionCommand.class, command -> {
                    log.info("Manager worker is executing with thread id: " + Thread.currentThread().getName());
                    this.responses = command.getResponses();
                    if (command.getMessage().equals("fetchBalances")) {
                        log.info("Creating worker Actor for querying wallet balance");
                        if (this.balanceWorker == null) {
                            this.balanceWorker = getContext().spawnAnonymous(QueryBalanceInfo.create(queryBalanceInfoService), DispatcherSelector.fromConfig("my-blocking-dispatcher-test"));
                        }
                        this.balanceWorker.tell(new WorkerCommand("QueryBalanceInfo", getContext().getSelf()));
//                        askForData(balanceWorker, "QueryBalanceInfo");

                        log.info("Creating worker Actor for querying loyality points balance");
                        if (this.loyalityPointWorker == null) {
                            this.loyalityPointWorker = getContext().spawnAnonymous(QueryLoyalityPoints.create());
                        }
                        this.loyalityPointWorker.tell(new WorkerCommand("QueryLoyalityPoints", getContext().getSelf()));
//                       askForData(loyalityPointWorker, "QueryLoyalityPoints");
                    }
                    return Behaviors.same();
                })
                .onMessage(BalanceResultCommand.class, command -> {
                    responseFromDownStream.put(command.getServiceName(), command.getResult());
                    log.info("received {} responses from {} using {} worker", responseFromDownStream.size(), responseFromDownStream.keySet(), command.getWorker().path().name());
                    System.out.println();

                    if (this.responseFromDownStream.size() == 2) {
                        responses.tell(responseFromDownStream);
                        this.responseFromDownStream = new ConcurrentHashMap<>(); // making the collection empty once the
                        System.out.println();
//                        System.out.println("Stopping the worker actor...!");
//                        getContext().stop(this.balanceWorker);
//                        getContext().stop(this.loyalityPointWorker);
                    }
                    return Behaviors.same();
                })
                .onMessage(NoResponseFromWorkerCommand.class, message -> {
                    log.info("Retrying with worker " + message.getWorker().path());
                    askForData(message.getWorker(), message.getCommand());
                    return Behaviors.same();
                })
                .build();
    }

    public Receive<ManagerCommand> activeMessageHandler() {
        return newReceiveBuilder()
                .onSignal(Terminated.class, handler -> Behaviors.same())
                .onMessage(BalanceResultCommand.class, command -> {
                    responseFromDownStream.put(command.getServiceName(), command.getResult());
                   log.info("received {} responses from {}",
                            responseFromDownStream.size(),
                            responseFromDownStream.keySet());
                    if (responseFromDownStream.size() == 2) {
                        this.responses.tell(responseFromDownStream);
//                        return stashBuffer.unstashAll(idealMessageHandler());
                    }
                    return Behaviors.same();
                })
                // if the existing workers have not yet completed then we are queuing the next requests
                .onMessage(InstructionCommand.class, message -> {
                    log.info("Delaying the next requests...!");
                    //getContext().getSelf().tell(message);
//                    stashBuffer.stash(message);
                    return Behaviors.same();
                })
                .build();
    }

    private void askForData(ActorRef<WorkerCommand> worker, String command) {
        //1. What's the response type we expect to receive.
        //2. Where we want to send the message.
        //3. time out value of how long we want to wait
        //4. message that we want to send, its a single param lambda and param is the reference to the ManagerClass.
        //5. it is another lambda, which is used to decide the action if we get or do not get response.
        //   this lambda takes 2 params response, and throwable.
        getContext().ask(ManagerCommand.class,
                worker,
                Duration.ofSeconds(5),
                (me) -> new WorkerCommand(command, me),
                (res, err) -> {
                    if (null == res) {
                        System.out.println("Worker " + worker.path() + " failed to respond");
                        return new NoResponseFromWorkerCommand(worker, command);
                    } else {
                        return res;
                    }
                });
    }


}
