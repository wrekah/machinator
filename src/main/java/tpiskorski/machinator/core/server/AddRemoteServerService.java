package tpiskorski.machinator.core.server;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AddRemoteServerService {
    public void add(Server server) throws IOException {
        throw new RuntimeException("Remote servers are not implemented yet");
    }
}
