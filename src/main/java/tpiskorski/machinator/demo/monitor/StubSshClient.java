package tpiskorski.machinator.demo.monitor;

import tpiskorski.machinator.core.server.Server;
import tpiskorski.machinator.core.vm.VirtualMachine;
import tpiskorski.machinator.demo.generator.VmGenerator;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class StubSshClient {

    private VmGenerator vmGenerator = new VmGenerator();

    List<VirtualMachine> getVms(Server server) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            int virtualMachinesNumber = ThreadLocalRandom.current().nextInt(1, 3);
            return vmGenerator.generateVirtualMachines(server, virtualMachinesNumber);
        } else {
            throw new SshException();
        }
    }
}
