package tpiskorski.vboxcm.command;

import java.util.Arrays;
import java.util.List;

public class Command {

    private List<String> parts;

    public static Command of(String... parts) {
        Command command = new Command();
        command.setParts(Arrays.asList(parts));
        return command;
    }

    public List<String> getParts() {
        return parts;
    }

    public void setParts(List<String> parts) {
        this.parts = parts;
    }

    @Override public String toString() {
        return String.join(" ", parts);
    }
}
