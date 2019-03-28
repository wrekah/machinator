package tpiskorski.vboxcm.command;

public enum Commands {
    LIST_ALL_VMS(Command.of("sh", "-c", "VBoxManage list vms")),
    IS_VBOX_INSTALLED(Command.of("sh", "-c", "VBoxManage --version"));

    private final Command command;

    Commands(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }
}
