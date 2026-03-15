package service;

import java.io.*;
import java.util.ArrayList;

public class DataStore {

    public static void save(String file, Object data) {
        try {
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(data);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object load(String file) {
        try {
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(file));
            Object obj = in.readObject();
            in.close();
            return obj;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}