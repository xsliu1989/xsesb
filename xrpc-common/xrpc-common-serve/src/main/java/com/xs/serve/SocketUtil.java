package com.xs.serve;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class SocketUtil {

    //日志log
    private final static Logger log =  LoggerFactory.getLogger(SocketUtil.class);

    /**
     * Socket接收字节数据
     * @param in
     * @param len
     * @return
     * @throws IOException
     */
    public static byte[] receiveBytes(InputStream in, int len) throws IOException {
        byte[] data = new byte[len];
        int i = 0;
        while (i < len) {
            int r = in.read(data, i, len - i);
            if (r == -1) break;
            i += r;
        }
        return data;
    }

    /**
     * Socket接收字符串数据
     * @param in
     * @param len
     * @param charset
     * @return
     * @throws IOException
     */
    public static String receiveStr(InputStream in, int len, String charset) throws IOException {
        return new String(SocketUtil.receiveBytes(in, len), Charset.forName(charset));
    }

    /**
     * Socket写出
     * @param out
     * @param data
     * @throws IOException
     */
    public static void send(OutputStream out, byte[] data) throws IOException {
        out.write(data);
        out.flush();
    }
}
