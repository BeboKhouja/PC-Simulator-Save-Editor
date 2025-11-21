package com.mokkachocolata.library.pcsimsaveeditor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class PCSimSave {
    /**
     * Decrypt/encrypt the provided string.
     * @param text The string to decrypt/encrypt.
     */
    public static String Decrypt(String text) {
        final int key = 0x81;
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : text.toCharArray())
            stringBuilder.append((char) (c ^ key));
        return stringBuilder.toString();
    }
}
