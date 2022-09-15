package src.commands.ownerRacer.impl.owner;

import akka.actor.typed.ActorRef;
import src.commands.ownerRacer.RaceControllerCommand;
import src.commands.ownerRacer.RacerCommand;

public class RacerFinishedCommand implements RaceControllerCommand {
    private static final long serialVersionUID = 1L;
    private ActorRef<RacerCommand> racer;

    public ActorRef<RacerCommand> getRacer() {
        return racer;
    }

    public void setRacer(ActorRef<RacerCommand> racer) {
        this.racer = racer;
    }

    public RacerFinishedCommand(ActorRef<RacerCommand> racer) {
        this.racer = racer;
    }
}
