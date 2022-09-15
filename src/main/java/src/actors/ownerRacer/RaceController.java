package src.actors.ownerRacer;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import src.commands.ownerRacer.RaceControllerCommand;
import src.commands.ownerRacer.RacerCommand;
import src.commands.ownerRacer.impl.owner.RacerFinishedCommand;
import src.commands.ownerRacer.impl.owner.RacerUpdateCommand;
import src.commands.ownerRacer.impl.owner.StartRace;
import src.commands.ownerRacer.impl.racer.PositionCommand;
import src.commands.ownerRacer.impl.racer.StartCommand;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RaceController extends AbstractBehavior<RaceControllerCommand> {
    private Map<ActorRef<RacerCommand>, Integer> currentPositions;
    private Map<ActorRef<RacerCommand>, Long> finishingTime;
    private long start;
    private Object TIMER_KEY;

    private RaceController(ActorContext<RaceControllerCommand> context) {
        super(context);
    }

    public static Behavior<RaceControllerCommand> create() {
        return Behaviors.setup(RaceController::new);
    }

    @Override
    public Receive<RaceControllerCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartRace.class, message -> {
                    start = System.currentTimeMillis();
                    currentPositions = new HashMap<>();
                    finishingTime = new HashMap<>();
                    for (int i = 0; i < 10; i++) {
                        ActorRef<RacerCommand> racer = getContext().spawn(RacerBehavior.create(), "racer" + i);
                        currentPositions.put(racer, 0);
                        racer.tell(new StartCommand(100));
                    }
                    return Behaviors.withTimers(timer -> {
                        timer.startTimerAtFixedRate(TIMER_KEY, new GetPositionsCommmand(), Duration.ofSeconds(1));
                        return Behaviors.same();
                    });
                }).onMessage(GetPositionsCommmand.class, message -> {
                    for (ActorRef<RacerCommand> racer : currentPositions.keySet()) {
                        racer.tell(new PositionCommand(getContext().getSelf()));

                        displayRace();
                    }
                    return Behaviors.same();
                })
                .onMessage(RacerUpdateCommand.class, message -> {
                    currentPositions.put(message.getRacer(), message.getPosition());
                    return Behaviors.same();
                })
                .onMessage(RacerFinishedCommand.class, message -> {
                    finishingTime.put(message.getRacer(), System.currentTimeMillis());
                    if (finishingTime.size() == 10) {
                        return raceCompleteMessageHandler();
                    }
                    return Behaviors.same();
                })
                .build();
    }

    public Receive<RaceControllerCommand> raceCompleteMessageHandler() {
        return newReceiveBuilder()
                .onMessage(GetPositionsCommmand.class, message -> {
                    for (ActorRef<RacerCommand> racer : currentPositions.keySet()) {
                        getContext().stop(racer);
                    }
                    displayResults();
                    return Behaviors.withTimers(timers -> {
                        timers.cancelAll();

                        return Behaviors.stopped();
                    });
                })
                .build();
    }
    private void displayResults(){
        finishingTime.values().stream().sorted().forEach(it -> {
            for (ActorRef<RacerCommand> key : finishingTime.keySet()) {
                if (Objects.equals(finishingTime.get(key), it)) {
                    System.out.println("src.Racer " + key.path().uid() + " finished in " + ( (double)it - start ) / 1000 + " seconds.");
                }
            }
        });
    }

    private void displayRace() {
        int displayLength = 160;
        for (int i = 0; i < 50; ++i) System.out.println();
        System.out.println("Race has been running for " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
        System.out.println("    " + new String(new char[displayLength]).replace('\0', '='));
        int i = 0;
        for (ActorRef<RacerCommand> racer : currentPositions.keySet()) {
            System.out.println(i + " : " + new String(new char[currentPositions.get(racer) * displayLength / 100]).replace('\0', '*'));
            i++;
        }
    }

    private class GetPositionsCommmand implements RaceControllerCommand {
        private static final long serialVersionUID = 1L;
    }
}
