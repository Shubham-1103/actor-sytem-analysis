package src.commands.managerWorker.impl;

import akka.actor.typed.ActorRef;
import src.commands.managerWorker.ManagerCommand;

import java.io.Serializable;

public class WorkerCommand implements Serializable {
    public static final long serialVersionUID = 1L;
    private String message;
    private ActorRef<ManagerCommand> sender;

    public WorkerCommand(String message, ActorRef<ManagerCommand> sender) {
        this.message = message;
        this.sender = sender;
    }

    public ActorRef<ManagerCommand> getSender() {
        return sender;
    }

    public void setSender(ActorRef<ManagerCommand> sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
