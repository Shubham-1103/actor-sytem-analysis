package src.commands.blockChain.impl;

import src.commands.blockChain.BlockChainManagerCommand;
import src.models.HashResult;

public class HashResultCommand implements BlockChainManagerCommand {
    private static final long serialVersionUID = 1L;
    private HashResult hashResult;

    public HashResult getHashResult() {
        return hashResult;
    }

    public HashResultCommand(HashResult hashResult) {
        this.hashResult = hashResult;
    }
}
