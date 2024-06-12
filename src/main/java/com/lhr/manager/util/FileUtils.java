package com.lhr.manager.util;

import java.io.*;

public class FileUtils {

    public static byte[] readToByteArray(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];


        nRead = is.read(data, 2, data.length - 2);
        if (nRead != -1) {
            buffer.write(new byte[] {'\n', '\n'}, 0, 2);
            buffer.write(data, 2, nRead);
        }

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }
}
