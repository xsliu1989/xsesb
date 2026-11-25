package com.xs.serve;

import java.io.*;

public class SerializeUtil {

    /**
     * 序列化
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {


        ObjectOutputStream oos = null;

        ByteArrayOutputStream baos = null;

        try {
            baos = new ByteArrayOutputStream();

            oos = new ObjectOutputStream(baos);

            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;

        try {

            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}