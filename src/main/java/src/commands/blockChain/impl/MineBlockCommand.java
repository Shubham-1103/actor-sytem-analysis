package src.commands.blockChain.impl;

import akka.actor.typed.ActorRef;
import src.commands.blockChain.BlockChainManagerCommand;
import src.models.Block;
import src.models.HashResult;

public class MineBlockCommand implements BlockChainManagerCommand {
    private static final long serialVersionUID = 1L;
    private Block block;
    private ActorRef<HashResult> sender;
    private int difficultyLevel;

    public MineBlockCommand(Block block, ActorRef<HashResult> sender, int difficultyLevel) {
        this.block = block;
        this.sender = sender;
        this.difficultyLevel = difficultyLevel;
    }

    public Block getBlock() {
        return block;
    }

    public ActorRef<HashResult> getSender() {
        return sender;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }
}
