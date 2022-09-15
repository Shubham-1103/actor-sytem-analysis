package src.commands.ownerRacer.impl.racer;

import akka.actor.typed.ActorRef;
import src.commands.ownerRacer.RaceControllerCommand;
import src.commands.ownerRacer.RacerCommand;

public class PositionCommand implements RacerCommand {
    private static final long serialVersionUID = 1L;
    private final ActorRef<RaceControllerCommand> actorRef;

    public PositionCommand(ActorRef<RaceControllerCommand> actorRef) {
        this.actorRef = actorRef;
    }

    public ActorRef<RaceControllerCommand> getActorRef() {
        return actorRef;
    }
}
