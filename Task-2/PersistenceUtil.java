package task2;



import java.io.*;
import java.util.List;

public class PersistenceUtil {
    public static void saveObject(Object obj, String path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(obj);
        }
    }

    public static Object loadObject(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return ois.readObject();
        }
    }

    // helper to save/restore user list or market
    @SuppressWarnings("unchecked")
    public static void saveUsers(List<User> users, String path) throws IOException {
        saveObject(users, path);
    }

    @SuppressWarnings("unchecked")
    public static List<User> loadUsers(String path) throws IOException, ClassNotFoundException {
        return (List<User>) loadObject(path);
    }
}
