package src.actors.mangerWorker;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import src.commands.managerWorker.impl.ResultCommand;
import src.commands.managerWorker.impl.WorkerCommand;

import java.math.BigInteger;
import java.util.Random;

public class WorkerBehavior extends AbstractBehavior<WorkerCommand> {

    private WorkerBehavior(ActorContext<WorkerCommand> context) {
        super(context);
    }

    public static Behavior<WorkerCommand> create() {
        return Behaviors.setup(WorkerBehavior::new);
    }

    @Override
    public Receive<WorkerCommand> createReceive() {
        return handleMessagesWhenWeDontYetHavePrimeNumber();
    }

    public Receive<WorkerCommand> handleMessagesWhenWeDontYetHavePrimeNumber() {
        return newReceiveBuilder()
                .onAnyMessage(command -> {
                    BigInteger bigInteger = new BigInteger(2000, new Random());
                    BigInteger prime = bigInteger.nextProbablePrime();
                    command.getSender().tell(new ResultCommand(prime));
                    return handleMessagesWhenWeAlreadyHavePrimeNumber(prime);
                })
                .build();
    }

    public Receive<WorkerCommand> handleMessagesWhenWeAlreadyHavePrimeNumber(BigInteger prime) {
        return newReceiveBuilder()
                .onAnyMessage(command -> {
                    command.getSender().tell(new ResultCommand(prime));
                    return Behaviors.same();
                })
                .build();
    }
}
