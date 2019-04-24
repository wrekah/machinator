package tpiskorski.machinator.command;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
public class CommandResultFactory {

    public CommandResult from(Process process) {
        if (process == null) {
            return new CommandResult("", "", true);
        }
        String std = stringify(process.getInputStream());
        String error = stringify(process.getErrorStream());

        return from(std, error);
    }

    private String stringify(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
            .lines()
            .collect(Collectors.joining("\n"));
    }

    public CommandResult from(String std, String err) {
        return new CommandResult(std, err, !err.isEmpty());
    }
}
