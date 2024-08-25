package com.mokkachocolata.library.pcsimsaveeditor;

import org.jchmlib.app.ChmWeb;

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
        ArrayList<String> charArray = new ArrayList<>();
        Stream<String> stream = Arrays.stream(text.split(""));
        stream.forEach(ee -> charArray.add(Character.toString(Character.codePointAt(ee, 0) ^ key)));
        stream.close();
        String[] CharacterArrayed = charArray.toArray(new String[0]);
        // Clear charArray, to help reduce memory footprint
        charArray.clear();
        StringBuilder output = new StringBuilder();
        for (String s : CharacterArrayed) {
            output.append(s);
        }
        Arrays.fill(CharacterArrayed, null);
        if (callGC) {
            System.gc();
        }
        return output.toString();
    }

    public String Decrypt(String text) {
        System.gc();
        int key = 0x81;
        ArrayList<String> charArray = new ArrayList<>();
        Stream<String> stream = Arrays.stream(text.split(""));
        stream.forEach(ee -> charArray.add(Character.toString(Character.codePointAt(ee, 0) ^ key)));
        stream.close();
        String[] CharacterArrayed = charArray.toArray(new String[0]);
        // Clear charArray, to help reduce memory footprint
        charArray.clear();
        StringBuilder output = new StringBuilder();
        for (String s : CharacterArrayed) {
            output.append(s);
        }
        Arrays.fill(CharacterArrayed, null);
        System.gc();
        return output.toString();
    }

    public void startHelpServer(int port) throws IOException {
        Path chmPath = Files.createTempFile(null, ".chm");
        InputStream chmResource =
                getClass().getResourceAsStream("/resources/PCSimulatorSaveEditor.chm");
            assert chmResource != null;
            Files.copy(chmResource, chmPath,
                    StandardCopyOption.REPLACE_EXISTING);
            ChmWeb chmWeb = new ChmWeb();
            chmWeb.serveChmFile(port, chmPath.toFile().getPath());
            lastPort = port;
    }

    public String fromCharCode(int charCode) {
        return String.valueOf(charCode);
    }

    public String getHelpServerURL(){
        return "https://localhost:" + lastPort + "\\@index.html";
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
