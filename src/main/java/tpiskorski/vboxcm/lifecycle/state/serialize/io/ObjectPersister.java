package tpiskorski.vboxcm.lifecycle.state.serialize.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectPersister {

    public <E> void persist(String fileName, E toPersist) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(toPersist);
        objectOutputStream.flush();
        objectOutputStream.close();
    }
}
