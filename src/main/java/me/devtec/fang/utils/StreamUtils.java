package me.devtec.fang.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.devtec.fang.data.json.Reader;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class StreamUtils {
    public static String fromStream(File file) {
        try {
            return fromStream(new FileInputStream(file));
        } catch (Exception e) {
            return null;
        }
    }

    public static Object fromStreamObject(File file) {
        try {
            return fromStreamObject(new FileInputStream(file));
        } catch (Exception e) {
            return null;
        }
    }

    public static String fromStream(InputStream stream) {
        try {
            InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            String content;
            while ((content = br.readLine()) != null)
                sb.append(content+System.lineSeparator());
            br.close();
            isr.close();
            return sb.toString();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                stream.close();
            } catch (Exception e) {}
        }
    }

    public static InputStream toStream(String text) {
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(text);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }

    public static InputStream toStreamObject(Object obj) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(me.devtec.fang.data.json.Writer.write(obj));
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static Object fromStreamObject(InputStream stream) {
        try {
            return Reader.read(ByteStreams.newDataInput(ByteStreams.toByteArray(stream)).readUTF()); //object
        } catch (Exception e) {
            return null;
        } finally {
            try {
                stream.close();
            } catch (Exception e) {}
        }
    }
}