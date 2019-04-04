package tpiskorski.vboxcm.shutdown.state;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

class ObjectPersister {

    <E> void persist(String fileName, E toPersist) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(toPersist);
        objectOutputStream.flush();
        objectOutputStream.close();
    }
}
