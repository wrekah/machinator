package tpiskorski.machinator.flow.command;

import java.util.List;

public class Command {

    private List<String> parts;

    public static Command of(List<String> parts) {
        Command command = new Command();
        command.setParts(parts);
        return command;
    }

    public List<String> getParts() {
        return parts;
    }

    public void setParts(List<String> parts) {
        this.parts = parts;
    }

    public String toEscapedString() {
        String lastPart = "\"" + parts.get(parts.size() - 1) + "\"";
        parts.remove(parts.size() - 1);
        parts.add(lastPart);
        return String.join(" ", parts);
    }

    @Override public String toString() {
        return String.join(" ", parts);
    }
}
