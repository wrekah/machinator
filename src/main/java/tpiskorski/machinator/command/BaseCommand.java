package tpiskorski.machinator.command;

public enum BaseCommand {
    LIST_ALL_VMS("VBoxManage list vms"),
    IS_VBOX_INSTALLED("VBoxManage --version"),
    SHOW_VM_INFO("VBoxManage showvminfo --machinereadable"),
    START_VM("VBoxManage startvm --type headless"),
    EXPORT_VM("VBoxManage export  --output output.ovf lite");

    public String asString() {
        return baseCommand;
    }

    private final String baseCommand;

    BaseCommand(String baseCommand) {
        this.baseCommand = baseCommand;
    }
}
