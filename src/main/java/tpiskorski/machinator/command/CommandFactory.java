package tpiskorski.machinator.command;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CommandFactory {

    private static final List<String> SHELL = List.of("sh", "-c");

    public Command make(BaseCommand baseCommand) {
        String command = baseCommand.asString();
        List<String> parts = Stream.concat(SHELL.stream(), Stream.of(command)).collect(Collectors.toList());

        return Command.of(parts);
    }

    public Command makeWithArgs(BaseCommand baseCommand, String... args) {
        String command = String.format(baseCommand.asString(), (Object[]) args);
        List<String> parts = Stream.concat(SHELL.stream(), Stream.of(command)).collect(Collectors.toList());

        return Command.of(parts);
    }
}
