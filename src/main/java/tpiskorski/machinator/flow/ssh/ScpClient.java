package tpiskorski.machinator.flow.ssh;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tpiskorski.machinator.flow.executor.ExecutionException;
import tpiskorski.machinator.flow.executor.RemoteContext;

import java.io.*;

public class ScpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScpClient.class);

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

    public void copyRemoteToLocal(RemoteContext remoteContext, String from, String to) throws JSchException, IOException {
        Session session = prepareSession(remoteContext);
        session.connect();
        copyRemoteToLocal(session, from, to);
    }

    public void copyLocalToRemote(RemoteContext remoteContext, String from, String to) throws JSchException, IOException {
        Session session = prepareSession(remoteContext);
        session.connect();
        copyLocalToRemote(session, from, to);
    }

    private void copyLocalToRemote(Session session, String from, String to) throws JSchException, IOException {
        String command = "scp " + "-p" + " -t " + to;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        channel.connect();

        if (checkStatus(in) != 0) {
            throw new ExecutionException("Scp client failure!");
        }

        File fromFile = new File(from);

        command = "T" + (fromFile.lastModified() / 1000) + " 0";
        command += (" " + (fromFile.lastModified() / 1000) + " 0\n");
        out.write(command.getBytes());
        out.flush();
        if (checkStatus(in) != 0) {
            throw new ExecutionException("Scp client failure!");
        }

        long filesize = fromFile.length();
        command = "C0644 " + filesize + " ";
        if (from.lastIndexOf('/') > 0) {
            command += from.substring(from.lastIndexOf('/') + 1);
        } else {
            command += from;
        }

        command += "\n";
        out.write(command.getBytes());
        out.flush();

        if (checkStatus(in) != 0) {
            throw new ExecutionException("Scp client failure!");
        }

        FileInputStream fis = new FileInputStream(from);
        byte[] buf = new byte[1024];
        while (true) {
            int len = fis.read(buf, 0, buf.length);
            if (len <= 0) break;
            out.write(buf, 0, len);
        }

        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();

        if (checkStatus(in) != 0) {
            throw new ExecutionException("Scp client failure!");
        }
        out.close();

        try {
            fis.close();
        } catch (Exception ex) {
            LOGGER.error("Scp client exception", ex);
        }

        channel.disconnect();
        session.disconnect();
    }

    private void copyRemoteToLocal(Session session, String from, String to) throws JSchException, IOException {
        String prefix = null;

        if (new File(to).isDirectory()) {
            prefix = to + File.separator;
        }

        String command = "scp -f " + from;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        channel.connect();

        byte[] buf = new byte[1024];

        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();

        while (true) {
            int c = checkStatus(in);
            if (c != 'C') {
                break;
            }

            in.read(buf, 0, 5);

            long filesize = 0L;
            while (true) {
                if (in.read(buf, 0, 1) < 0) {
                    break;
                }
                if (buf[0] == ' ') break;
                filesize = filesize * 10L + (long) (buf[0] - '0');
            }

            String file;
            for (int i = 0; ; i++) {
                in.read(buf, i, 1);
                if (buf[i] == (byte) 0x0a) {
                    file = new String(buf, 0, i);
                    break;
                }
            }

            LOGGER.info("file-size={} file={}", filesize, file);

            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            FileOutputStream fos = new FileOutputStream(prefix == null ? to : prefix + file);
            int foo;
            while (true) {
                if (buf.length < filesize) foo = buf.length;
                else foo = (int) filesize;
                foo = in.read(buf, 0, foo);
                if (foo < 0) {
                    break;
                }
                fos.write(buf, 0, foo);
                filesize -= foo;
                if (filesize == 0L) break;
            }

            if (checkStatus(in) != 0) {
                throw new ExecutionException("Scp client failure!");
            }

            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            try {
                fos.close();
            } catch (Exception ex) {
                LOGGER.error("Scp client exception", ex);
            }
        }

        channel.disconnect();
        session.disconnect();
    }

    /**
     * -1
     * 0 - success
     * 1 - error
     * 2 - fatal error
     **/
    private int checkStatus(InputStream in) throws IOException {
        int b = in.read();

        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuilder sb = new StringBuilder();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) {
                LOGGER.error("Scp client error {}", sb.toString());
            }
            if (b == 2) {
                LOGGER.error("Scp client fatal error {}", sb.toString());
            }
        }
        return b;
    }
}
