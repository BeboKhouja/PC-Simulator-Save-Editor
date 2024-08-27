package com.mokkachocolata.library.pcsimsaveeditor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class MainFunctions implements Runnable {

    public String input;

    public String Output;

    private int lastPort = 17727;

    /**
     * Decrypt/encrypt the provided string.
     * @param text The string to decrypt/encrypt.
     */
    public String Decrypt(String text, boolean callGC) {
        if (callGC){
            System.gc();
        }
        int key = 0x81;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            stringBuilder.append((char) (input.charAt(i) ^ key));
        }
        if (callGC) {
            System.gc();
        }
        return stringBuilder.toString();
    }

    public String Decrypt(String text) {
        System.gc();
        int key = 0x81;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            stringBuilder.append((char) (input.charAt(i) ^ key));
        }
        System.gc();
        return stringBuilder.toString();
    }

    public String fromCharCode(int charCode) {
        return String.valueOf(charCode);
    }

    @Override
    public void run() {
        System.gc();
        int key = 0x81;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            stringBuilder.append((char) (input.charAt(i) ^ key));
        }
        Output = stringBuilder.toString();
        System.gc();
    }
}
