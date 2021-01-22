package net.minestom.server;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.squareup.javapoet.JavaFile;

/**
 * Interface representing a code generator
 */
public interface CodeGenerator {

    /**
     * Generates the Java code
     * @return
     */
    List<JavaFile> generate() throws IOException;

    default void generateTo(File targetFolder) throws IOException {
        List<JavaFile> code = generate();

        for(JavaFile file : code) {
            file.writeTo(targetFolder);
        }
    }

    static String decapitalize(String text) {
        char first = text.charAt(0);
        return ""+Character.toLowerCase(first)+text.substring(1);
    }
}
