package tpiskorski.machinator.lifecycle.state.serialize.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@SuppressWarnings("unchecked")
public class ObjectRestorer {

    public <E> List<E> restore(String fileName) throws IOException, ClassNotFoundException {
        Path filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            return List.of();
        }

        try (InputStream outFile = Files.newInputStream(filePath, StandardOpenOption.READ)) {
            ObjectInputStream objectInputStream = new ObjectInputStream(outFile);
            List<E> cast = (List<E>) (objectInputStream.readObject());
            objectInputStream.close();
            return cast;
        }
    }
}
