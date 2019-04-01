package tpiskorski.vboxcm.command;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CommandFactory {

    private static final List<String> SHELL = List.of("sh", "-c");

    public Command make(BaseCommand baseCommand) {
        return make(baseCommand, "");
    }

    public Command make(BaseCommand baseCommand, String arg) {
        String command;
        if (arg.isEmpty()) {
            command = baseCommand.asString();
        } else {
            command = baseCommand.asString() + " " + arg;
        }

        List<String> parts = Stream.concat(SHELL.stream(), Stream.of(command)).collect(Collectors.toList());

        return Command.of(parts);
    }
}
