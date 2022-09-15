package src;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import src.config.SpringExtension;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan("src.*")
public class AkkaSpringApp {
    public static void main(String[] args) {
        SpringApplication.run(AkkaSpringApp.class);
    }
//    @PostConstruct
//    public void setUp(){
//        ActorSystem system = ActorSystem.create("actor-system", ConfigFactory.load());
//        SpringExtension.getInstance().get(system).initialize(context);
//    }
}
