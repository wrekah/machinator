package tpiskorski.machinator.command;

public enum BaseCommand {
    LIST_ALL_VMS("VBoxManage list vms"),
    IS_VBOX_INSTALLED("VBoxManage --version"),
    SHOW_VM_INFO("VBoxManage showvminfo --machinereadable \"%s\""),

    EXPORT_VM("VBoxManage export --output %s.ova \"%s\""),
    IMPORT_VM("VBoxManage import %s.ova"),
    DELETE_VM("VBoxManage unregistervm \"%s\""),

    START_VM("VBoxManage startvm --type headless \"%s\""),
    POWER_OFF_VM("VBoxManage controlvm \"%s\" acpipowerbutton"),
    RESET_VM("VBoxManage controlvm \"%s\" reset"),
    TURN_OFF("VBoxManage controlvm \"%s\" poweroff");

    private final String baseCommand;

    BaseCommand(String baseCommand) {
        this.baseCommand = baseCommand;
    }

    public String asString() {
        return baseCommand;
    }
}
