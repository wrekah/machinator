package tpiskorski.machinator.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.LocalExecutor;
import tpiskorski.machinator.flow.executor.RemoteExecutor;

@Configuration
public class BeanDefinitions {

    @Bean
    public CommandExecutor commandExecutor() {
        return new CommandExecutor(new LocalExecutor(), new RemoteExecutor());
    }
}
