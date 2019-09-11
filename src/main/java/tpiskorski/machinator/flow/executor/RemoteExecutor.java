package tpiskorski.machinator.flow.executor;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.command.CommandResultFactory;
import tpiskorski.machinator.flow.ssh.PasswordOnlyUserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class RemoteExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteExecutor.class);
    private static final int CONNECT_TIMEOUT = 10000;
    private CommandResultFactory commandResultFactory = new CommandResultFactory();

    public CommandResult execute(ExecutionContext executionContext) {
        try {
            Session session = prepareSession(executionContext);
            LOGGER.debug("About to connect");
            session.connect(CONNECT_TIMEOUT);
            LOGGER.debug("Connected");
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            LOGGER.debug("Channel created");
            channelExec.setCommand(executionContext.getCommand().toEscapedString());

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
            throw new RuntimeException(e);
        }
    }

    private Session prepareSession(ExecutionContext executionContext) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(executionContext.getUser(), executionContext.getAddress(), executionContext.getPort());

        UserInfo userInfo = new PasswordOnlyUserInfo(executionContext.getPassword());
        session.setPassword(executionContext.getPassword());
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
