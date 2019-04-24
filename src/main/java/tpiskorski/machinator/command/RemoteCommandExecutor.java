package tpiskorski.machinator.command;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class RemoteCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteCommandExecutor.class);

    private CommandResultFactory commandResultFactory = new CommandResultFactory();

    public CommandResult execute(Command command, RemoteContext remoteContext) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(remoteContext.getUser(), remoteContext.getAddress(), remoteContext.getPort());

            UserInfo userInfo = new PasswordOnlyUserInfo(remoteContext.getPassword());
            session.setPassword(remoteContext.getPassword());
            session.setUserInfo(userInfo);

            session.connect();
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");

            channelExec.setCommand(command.toEscapedString());
            channelExec.connect();

            String stdout = read(channelExec.getInputStream());
            String stderr = read(channelExec.getErrStream());

            LOGGER.info("Exit status of remote command {}", channelExec.getExitStatus());

            channelExec.disconnect();
            session.disconnect();

            return commandResultFactory.from(stdout, stderr);
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String read(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder line = new StringBuilder();

        String tmp;
        while ((tmp = reader.readLine()) != null) {
            line.append(tmp).append("\n");
        }
        return line.toString();
    }
}
