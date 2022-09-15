package src.config;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import src.actors.mangerWorker.ManagerBehavior;
import src.commands.managerWorker.ManagerCommand;
import src.service.QueryBalanceInfoService;

import static src.config.SpringExtension.SPRING_EXTENSION_PROVIDER;


@Configuration
public class AkkaActorConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private QueryBalanceInfoService queryBalanceInfoService;

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ActorSystem<ManagerCommand> actorSystem() {
        ActorSystem<ManagerCommand> managerBehavior = ActorSystem.create(getGuardianBehavior(), "actor-system");
        SPRING_EXTENSION_PROVIDER.get(managerBehavior)
                .initialize(applicationContext);
        return managerBehavior;
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Behavior<ManagerCommand> getGuardianBehavior() {
        return ManagerBehavior.create(queryBalanceInfoService);
    }

    @Bean
    public Config akkaConfiguration() {
        return ConfigFactory.load();
    }
}
