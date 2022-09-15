package src.commands.managerWorker.impl;

import akka.actor.typed.ActorRef;
import src.commands.managerWorker.ManagerCommand;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InstructionCommand implements ManagerCommand {
    private static final long serialVersionUID = 1L;

    private final String message;
    private ActorRef<ConcurrentHashMap<String, String>> responses;

    public InstructionCommand(String message, ActorRef<ConcurrentHashMap<String, String>> responses) {
        this.message = message;
        this.responses = responses;
    }

    public ActorRef<ConcurrentHashMap<String, String>> getResponses() {
        return responses;
    }

    public String getMessage() {
        return message;
    }
}
