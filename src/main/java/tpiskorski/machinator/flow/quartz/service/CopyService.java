package tpiskorski.machinator.flow.quartz.service;

import com.jcraft.jsch.JSchException;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.executor.RemoteContext;
import tpiskorski.machinator.flow.ssh.ScpClient;
import tpiskorski.machinator.model.server.Server;

import java.io.IOException;

@Service
public class CopyService {

    private ScpClient scpClient = new ScpClient();

    public void copyRemoteToLocal(Server server, String s, String backupLocation, String backupName) throws IOException, JSchException {
        RemoteContext remoteContext = RemoteContext.of(server);

        scpClient.copyRemoteToLocal(remoteContext, "~/", backupLocation, backupName + ".ova");
    }

    public void copyLocalToRemote(Server server, String backupLocation, String tempFileNAme) throws IOException, JSchException {
        RemoteContext remoteContext = RemoteContext.of(server);

        scpClient.copyLocalToRemote(remoteContext, backupLocation, "~", tempFileNAme);
    }
}
