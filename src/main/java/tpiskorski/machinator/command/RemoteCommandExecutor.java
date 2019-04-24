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
            Session session = prepareSession(remoteContext);
            LOGGER.debug("About to connect");
            session.connect();
            LOGGER.debug("Connected");
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            LOGGER.debug("Channel created");
            channelExec.setCommand(command.toEscapedString());

            InputStream inputStream = channelExec.getInputStream();
            InputStream errStream = channelExec.getErrStream();

            channelExec.connect();
            LOGGER.debug("Channel connected");

            String stdout = read(inputStream);
            String stderr = read(errStream);

            LOGGER.debug("Exit status of remote command {}", channelExec.getExitStatus());

            channelExec.disconnect();
            LOGGER.debug("Channel disconnected");

            session.disconnect();
            LOGGER.debug("Session disconnected");

            return commandResultFactory.from(stdout, stderr);
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Session prepareSession(RemoteContext remoteContext) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(remoteContext.getUser(), remoteContext.getAddress(), remoteContext.getPort());

        UserInfo userInfo = new PasswordOnlyUserInfo(remoteContext.getPassword());
        session.setPassword(remoteContext.getPassword());
        session.setUserInfo(userInfo);

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "password");
        session.setConfig(config);

        return session;
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
