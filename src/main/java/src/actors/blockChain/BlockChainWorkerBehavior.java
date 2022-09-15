package src.actors.blockChain;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import src.commands.blockChain.BlockChainWorkerCommand;
import src.commands.blockChain.impl.HashResultCommand;
import src.models.HashResult;

import static src.utils.BlockChainUtils.calculateHash;

public class BlockChainWorkerBehavior extends AbstractBehavior<BlockChainWorkerCommand> {
    private BlockChainWorkerBehavior(ActorContext<BlockChainWorkerCommand> context) {
        super(context);
    }

    public static Behavior<BlockChainWorkerCommand> create() {
        return Behaviors.setup(BlockChainWorkerBehavior::new);
    }

    @Override
    public Receive<BlockChainWorkerCommand> createReceive() {
        return newReceiveBuilder()
                .onAnyMessage(message -> {
                    String hash = new String(new char[message.getDifficulty()]).replace("\0", "X");
                    String target = new String(new char[message.getDifficulty()]).replace("\0", "0");

                    int nonce = message.getStartNonce();
                    while (!hash.substring(0, message.getDifficulty()).equals(target) && nonce < message.getStartNonce() + 1000) {
                        nonce++;
                        String dataToEncode = message.getBlock().getPreviousHash() + message.getBlock().getTransaction().getTimestamp() + nonce + message.getBlock().getTransaction();
                        hash = calculateHash(dataToEncode);
                    }
                    if (hash.substring(0, message.getDifficulty()).equals(target)) {
                        HashResult hashResult = new HashResult();
                        hashResult.foundAHash(hash, nonce);
                        // send the Hash Result to controller
                        message.getActorRef().tell(new HashResultCommand(hashResult));
                        return Behaviors.same();
                    } else {
                        return Behaviors.same();
                    }
                })
                .build();
    }
}
