package tpiskorski.vboxcm.lifecycle.state.serialize.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ObjectPersister {

    public <E> void persist(String fileName, E toPersist) throws IOException {
        try (OutputStream outFile = Files.newOutputStream(Paths.get(fileName), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outFile);
            objectOutputStream.writeObject(toPersist);
            objectOutputStream.flush();
            objectOutputStream.close();
        }
    }
}
