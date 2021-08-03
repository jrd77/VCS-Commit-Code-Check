package com.github.jrd77.codecheck.util;


import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/7/20 20:22
 */
public class IoUtil {

    public static <T extends Collection<String>> T readLines(InputStream in, Charset charset, T collection) {
        return readLines(getReaderByCharset(in, charset), (T) collection);
    }

    public static <T extends Collection<String>> T readLines(Reader reader, T collection) {
        readLineStr(reader, collection::add);
        return collection;
    }

    private static void readLineStr(Reader reader, LineHandler lineHandler) throws IORuntimeException {
        Assert.notNull(reader);
        Assert.notNull(lineHandler);
        BufferedReader bReader = getReader(reader);

        try {
            String line;
            while ((line = bReader.readLine()) != null) {
                lineHandler.handle(line);
            }

        } catch (IOException var5) {
            throw new IORuntimeException(var5);
        }
    }

    public static BufferedReader getReader(Reader reader) {
        if (null == reader) {
            return null;
        } else {
            return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
        }
    }
//    public static void readLines(InputStream in, Charset charset, LineHandler lineHandler) throws IORuntimeException {
//        readLines(getReaderByCharset(in, charset), (LineHandler)lineHandler);
//    }

    public static BufferedReader getReaderByCharset(InputStream in, Charset charset) {
        if (null == in) {
            return null;
        } else {
            InputStreamReader reader;
            if (null == charset) {
                reader = new InputStreamReader(in);
            } else {
                reader = new InputStreamReader(in, charset);
            }

            return new BufferedReader(reader);
        }
    }

    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

    }

}
