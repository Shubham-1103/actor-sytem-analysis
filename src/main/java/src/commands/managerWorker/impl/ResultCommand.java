package src.commands.managerWorker.impl;

import src.commands.managerWorker.ManagerCommand;

import java.math.BigInteger;

public class ResultCommand implements ManagerCommand {
    public static final long serialVersionUID = 1L;
    private final BigInteger prime;

    public ResultCommand(BigInteger prime) {
        this.prime = prime;
    }

    public BigInteger getPrime() {
        return prime;
    }
}
