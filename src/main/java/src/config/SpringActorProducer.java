package src.config;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import akka.actor.typed.javadsl.AbstractBehavior;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;


public class SpringActorProducer  {

    private final ApplicationContext applicationContext;
    private final String actorBeanName;
    private  Object[] args;

    public SpringActorProducer(ApplicationContext applicationContext, String actorBeanName, Object... args) {
        this.applicationContext = applicationContext;
        this.actorBeanName = actorBeanName;
        this.args = args;
    }
    public SpringActorProducer(ApplicationContext applicationContext, String actorBeanName) {
        this.applicationContext = applicationContext;
        this.actorBeanName = actorBeanName;
    }

    public AbstractBehavior<?> produce() {
        if (args == null) {
            return (AbstractBehavior<?>) applicationContext.getBean(actorBeanName);
        } else {
            return (AbstractBehavior<?>) applicationContext.getBean(actorBeanName, args);
        }
    }
    public Class<? extends AbstractBehavior<?>> actorClass() {
        return (Class<? extends AbstractBehavior<?>>) applicationContext
                .getType(actorBeanName);
    }
}
