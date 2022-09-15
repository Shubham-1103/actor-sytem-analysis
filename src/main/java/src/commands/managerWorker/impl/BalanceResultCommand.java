package src.commands.managerWorker.impl;

import akka.actor.typed.ActorRef;
import src.commands.managerWorker.ManagerCommand;

public class BalanceResultCommand implements ManagerCommand {
    private static final long serialVersionUID = 1L;
    private final String serviceName;
    private final String result;

    public ActorRef<WorkerCommand> getWorker() {
        return worker;
    }

    private ActorRef<WorkerCommand> worker;
    public String getServiceName() {
        return serviceName;
    }

    public String getResult() {
        return result;
    }

    public BalanceResultCommand(String serviceName, String result, ActorRef<WorkerCommand> worker) {
        this.serviceName = serviceName;
        this.result = result;
        this.worker = worker;
    }
}
