package tpiskorski.machinator.action;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanDefinitions {

    @Bean
    public CommandExecutor commandExecutor() {
        return new CommandExecutor(new LocalExecutor(), new RemoteExecutor());
    }
}
