package tpiskorski.vboxcm.shutdown.state.restore;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class ObjectRestorer {

    public <E> List<E> restore(Class<E> clazz, String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        List<E> cast = (List<E>) (objectInputStream.readObject());
        objectInputStream.close();
        return cast;
    }
}
