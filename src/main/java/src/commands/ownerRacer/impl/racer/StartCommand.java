package src.commands.ownerRacer.impl.racer;

import src.commands.ownerRacer.RacerCommand;

public class StartCommand implements RacerCommand {
    private static final long serialVersionUID = 1L;
    private final int raceLength;

    public int getRaceLength() {
        return raceLength;
    }

    public StartCommand(int raceLength) {
        this.raceLength = raceLength;
    }
}
