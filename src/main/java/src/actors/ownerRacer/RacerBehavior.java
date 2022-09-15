package src.actors.ownerRacer;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import src.commands.ownerRacer.RacerCommand;
import src.commands.ownerRacer.impl.owner.RacerFinishedCommand;
import src.commands.ownerRacer.impl.owner.RacerUpdateCommand;
import src.commands.ownerRacer.impl.racer.PositionCommand;
import src.commands.ownerRacer.impl.racer.StartCommand;

import java.util.Random;

public class RacerBehavior extends AbstractBehavior<RacerCommand> {
    private final double defaultAverageSpeed = 48.2;
    private int averageSpeedAdjustmentFactor;
    private double currentSpeed = 0;
    private Random random;

    private RacerBehavior(ActorContext<RacerCommand> context) {
        super(context);
    }

    public static Behavior<RacerCommand> create() {
        return Behaviors.setup(RacerBehavior::new);
    }

    @Override
    public Receive<RacerCommand> createReceive() {
        return notYetStarted();
    }

    public Receive<RacerCommand> notYetStarted() {
        return newReceiveBuilder()
                .onMessage(StartCommand.class, message -> {
                    this.random = new Random();
                    this.averageSpeedAdjustmentFactor = random.nextInt(30) - 10;
                    return running(message.getRaceLength(), 0);
                })
                .onMessage(PositionCommand.class, message -> {
                    message.getActorRef().tell(new RacerUpdateCommand(getContext().getSelf(), 0));
                    return Behaviors.same();
                })
                .build();
    }

    public Receive<RacerCommand> running(int raceLength, int currentPosition) {
        return newReceiveBuilder()
                .onMessage(PositionCommand.class, message -> {
                    determineNextSpeed(raceLength, currentPosition);
                    int newPosition = currentPosition;
                    newPosition += getDistanceMovedPerSecond();
                    if (newPosition > raceLength)
                        newPosition = raceLength;
                    message.getActorRef().tell(new RacerUpdateCommand(getContext().getSelf(), newPosition));
                    if (newPosition == raceLength)
                        return completed(raceLength);
                    return running(raceLength, newPosition);
                })
                .build();
    }

    public Receive<RacerCommand> completed(int raceLength) {
        return newReceiveBuilder()
                .onMessage(PositionCommand.class, message -> {
                    message.getActorRef().tell(new RacerUpdateCommand(getContext().getSelf(), raceLength));
                    message.getActorRef().tell(new RacerFinishedCommand(getContext().getSelf()));
                    //return Behaviors.ignore(); // actor is alive but it will not accept any messages.
                    return waitingToStop();
                })
                .build();
    }
    public Receive<RacerCommand> waitingToStop(){
        return newReceiveBuilder()
                .onAnyMessage(message-> Behaviors.same())
                .onSignal(PostStop.class, signal->{
                    getContext().getLog().info("Termination started...!");
                    return Behaviors.same();
                })
                .build();
    }

    private double getMaxSpeed() {
        return defaultAverageSpeed * (1 + ((double) averageSpeedAdjustmentFactor / 100));
    }

    private double getDistanceMovedPerSecond() {
        return currentSpeed * 1000 / 3600;
    }

    private void determineNextSpeed(int raceLength, int currentPosition) {
        if (currentPosition < (raceLength / 4)) {
            currentSpeed = currentSpeed + (((getMaxSpeed() - currentSpeed) / 10) * random.nextDouble());
        } else {
            currentSpeed = currentSpeed * (0.5 + random.nextDouble());
        }

        if (currentSpeed > getMaxSpeed())
            currentSpeed = getMaxSpeed();

        if (currentSpeed < 5)
            currentSpeed = 5;

        if (currentPosition > (raceLength / 2) && currentSpeed < getMaxSpeed() / 2) {
            currentSpeed = getMaxSpeed() / 2;
        }
    }
}
