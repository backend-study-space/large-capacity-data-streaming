package example.largecapacitydatastreaming.support;

import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

@Component
public class FileWriteService<T> {

    public void writeHeader(Class<T> type, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            createHeader(type, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeBody(Class<T> type, List<T> data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            for (T t : data) {
                createBody(t, type, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeBody(Class<T> type, T data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            createBody(data, type, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    private void createBody(T t, Class<T> type, FileWriter writer) throws IOException {
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
