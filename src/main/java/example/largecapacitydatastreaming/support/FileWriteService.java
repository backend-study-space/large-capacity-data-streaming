package example.largecapacitydatastreaming.support;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class FileWriteService<T> {

    Lock lock = new ReentrantLock();

    public void writeHeader(Class<T> type, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            createHeader(type, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeBody(Class<T> type, List<T> data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true); BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            for (T t : data) {
                createBody(t, type, bufferedWriter);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeBody(Class<T> type, T data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true); BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            createBody(data, type, bufferedWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeBody(T data, BufferedWriter writer) {
        if (data instanceof SerializableCustom) {
            try {
                createBody((SerializableCustom) data, writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void writeBody(Queue<T> data, BufferedWriter bufferedWriter) {
        try {
            lock.lock();
            while (!data.isEmpty()) {
                createBody((SerializableCustom) data.poll(), bufferedWriter);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    private void createHeader(Class<T> type, FileWriter writer) throws IOException {
        boolean isFirstField = true;

        for (Field field : type.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);

            writer.append(isFirstField ? "" : ",");
            writer.append(String.format("%s", column.Name()));

            if (isFirstField) {
                isFirstField = false;
            }
        }
        writer.append("\n");
    }

    private void createBody(SerializableCustom t, BufferedWriter writer) throws IOException {
        if (t != null)
            writer.append(t.serialize()).append(System.lineSeparator());
    }

    private void createBody(T t, Class<T> type, BufferedWriter writer) throws IOException {
        boolean isFirstField = true;

        for (Field declaredField : type.getDeclaredFields()) {
            Field field = getField(type, declaredField.getName());
            field.setAccessible(true);

            try {
                Object value = field.get(t);
                writer.append(isFirstField ? "" : ",");
                writer.append(String.format("%s", value.toString()));

                if (isFirstField) {
                    isFirstField = false;
                }
            } catch (IllegalAccessException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        writer.append("\n");
    }

    private Field getField(Class<T> type, String name) {
        try {
            return type.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
