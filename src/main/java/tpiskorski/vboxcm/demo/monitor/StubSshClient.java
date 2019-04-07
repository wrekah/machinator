package tpiskorski.vboxcm.demo.monitor;

import tpiskorski.vboxcm.core.server.Server;
import tpiskorski.vboxcm.core.vm.VirtualMachine;
import tpiskorski.vboxcm.demo.generator.VmGenerator;

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
