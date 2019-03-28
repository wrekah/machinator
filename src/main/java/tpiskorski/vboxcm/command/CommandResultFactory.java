package tpiskorski.vboxcm.command;

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

        if (!error.isEmpty()) {
            return new CommandResult(std, error, true);
        } else {
            return new CommandResult(std, error, false);
        }
    }

    private String stringify(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
            .lines()
            .collect(Collectors.joining("\n"));
    }
}
