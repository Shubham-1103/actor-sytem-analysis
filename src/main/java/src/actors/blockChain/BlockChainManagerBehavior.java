package src.actors.blockChain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import src.commands.blockChain.BlockChainManagerCommand;
import src.commands.blockChain.BlockChainWorkerCommand;
import src.commands.blockChain.impl.MineBlockCommand;
import src.models.Block;
import src.models.HashResult;

public class BlockChainManagerBehavior extends AbstractBehavior<BlockChainManagerCommand> {

    private ActorRef<HashResult> sender;
    private Block block;
    private int difficultyLevel;
    private int currentNonce = 0;
    private BlockChainManagerBehavior(ActorContext<BlockChainManagerCommand> context) {
        super(context);
    }

    public static Behavior<BlockChainManagerCommand> create() {
        return Behaviors.setup(BlockChainManagerBehavior::new);
    }

    @Override
    public Receive<BlockChainManagerCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(MineBlockCommand.class, command -> {
                    this.sender = command.getSender();
                    this.block = command.getBlock();
                    this.difficultyLevel = command.getDifficultyLevel();
                    for (int i = 0; i < 10; i++) {
                        startNextWorker();
                    }
                    return Behaviors.same();
                })
                .build();
    }

    private void startNextWorker() {
        ActorRef<BlockChainWorkerCommand> worker = getContext().spawn(BlockChainWorkerBehavior.create(), "worker" + currentNonce);
        worker.tell(new BlockChainWorkerCommand(block, currentNonce * 1000, difficultyLevel, getContext().getSelf()));
        currentNonce++;
    }
}
