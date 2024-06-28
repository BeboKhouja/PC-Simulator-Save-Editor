package com.mokkachocolata.project.pcsimsaveeditor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;

public class MainGUI {
    private String selectList[] = {
            "Encrypt",
            "Decrypt"
    };
    private JButton selectSaveFile;
    private JButton selectOutputFile;
    private JTextArea Output;
    private JTextArea Input;
    private JButton decryptButton;
    private JPanel panel;

    public MainGUI() {
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int key = 0x81;
                ArrayList<String> charArray = new ArrayList<>();
                Arrays.stream(Input.getText().split("")).forEach(ee -> { charArray.add(Character.toString(Character.codePointAt(ee, 0) ^ key));});
                String[] CharacterArrayed = charArray.toArray(new String[charArray.size()]);
                String output = "";
                for (int i = 0; i < CharacterArrayed.length; i++) {
                    output = output + CharacterArrayed[i];
                }
                Output.setText(output);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("PC Simulator Save Editor");
        frame.setContentPane(new MainGUI().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        new MainGUI().init();
        frame.pack();
        frame.setVisible(true);
    }
    private void init() {

    }
}
