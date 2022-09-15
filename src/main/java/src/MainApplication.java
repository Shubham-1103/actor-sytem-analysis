package src;

import src.actors.mangerWorker.ManagerBehavior;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import src.commands.managerWorker.ManagerCommand;
import src.commands.managerWorker.impl.InstructionCommand;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class MainApplication {
    public static void main(String[] args) {
//        ActorSystem<ManagerCommand> manger = ActorSystem.create(ManagerBehavior.create(), "Manger");
//        manger.tell(new InstructionCommand("test"));
//        manger.tell(new InstructionCommand("fetchBalances"));
//        manger.tell(new InstructionCommand("startPrime"));
//        CompletionStage<Map<String, String>> fetchBalances = AskPattern.ask(manger,
//                (me) -> new InstructionCommand("fetchBalances", me),
//                Duration.ofMillis(100000),
//                manger.scheduler());
//        fetchBalances.whenComplete((result, error) -> {
//            if (result != null) {
//                System.out.println(result);
//            } else {
//                System.out.println("time out executing the downstream API's");
//            }
//            manger.terminate();
//        });

//        ActorSystem<RaceControllerCommand> raceController = ActorSystem.create(RaceController.create(), "RaceSimulation");
//        raceController.tell(new StartRace());
    }
}
