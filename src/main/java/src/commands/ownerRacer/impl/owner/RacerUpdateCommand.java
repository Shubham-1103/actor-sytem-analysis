package src.commands.ownerRacer.impl.owner;

import akka.actor.typed.ActorRef;
import src.commands.ownerRacer.RaceControllerCommand;
import src.commands.ownerRacer.RacerCommand;

public class RacerUpdateCommand implements RaceControllerCommand {
    private static final long serialVersionUID = 1L;
    private final ActorRef<RacerCommand> racer;
    private final int position;

    public RacerUpdateCommand(ActorRef<RacerCommand> racer, int position) {
        this.racer = racer;
        this.position = position;
    }

    public ActorRef<RacerCommand> getRacer() {
        return racer;
    }

    public int getPosition() {
        return position;
    }
}
