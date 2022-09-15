package src.commands.blockChain;

import akka.actor.typed.ActorRef;
import src.models.Block;

public class BlockChainWorkerCommand {
    private Block block;
    private int startNonce;
    private int difficulty;
    private ActorRef<BlockChainManagerCommand> actorRef;

    public BlockChainWorkerCommand(Block block, int startNonce, int difficulty, ActorRef<BlockChainManagerCommand> actorRef) {
        this.block = block;
        this.startNonce = startNonce;
        this.difficulty = difficulty;
        this.actorRef = actorRef;
    }

    public ActorRef<BlockChainManagerCommand> getActorRef() {
        return actorRef;
    }

    public Block getBlock() {
        return block;
    }

    public int getStartNonce() {
        return startNonce;
    }

    public int getDifficulty() {
        return difficulty;
    }
}
