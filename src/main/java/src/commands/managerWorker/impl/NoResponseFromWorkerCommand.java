package src.commands.managerWorker.impl;

import akka.actor.typed.ActorRef;
import src.commands.managerWorker.ManagerCommand;

public class NoResponseFromWorkerCommand implements ManagerCommand {
    private static final long serialVersionUID = 1L;
    private ActorRef<WorkerCommand> worker;
    private String command;

    public String getCommand() {
        return command;
    }

    public ActorRef<WorkerCommand> getWorker() {
        return worker;
    }

    public NoResponseFromWorkerCommand(ActorRef<WorkerCommand> worker, String command) {
        this.worker = worker;
        this.command = command;
    }
}
