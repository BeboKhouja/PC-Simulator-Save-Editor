package com.mokkachocolata.project.pcsimsaveeditor;

import org.jchmlib.app.ChmWeb;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

public class MainGUI {
    private String selectList[] = {
            "Encrypt",
            "Decrypt"
    };
    private JButton selectSaveFile;
    private ChmWeb chmWeb = new ChmWeb();
    private JButton selectOutputFile;
    private JTextArea Output;
    private JTextArea Input;
    private JButton decryptButton;
    private JPanel panel;
    private JButton copyToClipboardButton;
    private JButton guideButton;

    private String getEncryptedDecryptedString(String decryptEncrypt) {
        int key = 0x81;
        ArrayList<String> charArray = new ArrayList<>();
        Arrays.stream(decryptEncrypt.split("")).forEach(ee -> { charArray.add(Character.toString(Character.codePointAt(ee, 0) ^ key));});
        String[] CharacterArrayed = charArray.toArray(new String[charArray.size()]);
        String output = "";
        for (int i = 0; i < CharacterArrayed.length; i++) {
            output = output + CharacterArrayed[i];
        }
        return output;
    }

    public MainGUI() {
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Output.setText(getEncryptedDecryptedString(Input.getText()));
            }
        });
        selectSaveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String home = System.getProperty("user.home");
                File downloads = new File(home+"/Downloads/");
                File selected = null;
                JFileChooser fileChooser = new JFileChooser(downloads);
                FileFilter filter = new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        if (pathname.isDirectory()) {
                            return true;
                        } else {
                            String filename = pathname.getName().toLowerCase();
                            return filename.endsWith(".pc") ;
                        }
                    }

                    @Override
                    public String getDescription() {
                        return ".pc (PC Simulator save file)";
                    }
                };
                fileChooser.addChoosableFileFilter(filter);
                fileChooser.setFileFilter(filter);
                fileChooser.setDialogTitle("Open PC Simulator save file...");
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    selected = fileChooser.getSelectedFile();
                    try {
                        Output.setText(getEncryptedDecryptedString(Files.readString(selected.toPath())));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        selectOutputFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String home = System.getProperty("user.home");
                File downloads = new File(home+"/Downloads/");
                File selected = null;
                JFileChooser fileChooser = new JFileChooser(downloads);
                FileFilter filter = new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        if (pathname.isDirectory()) {
                            return true;
                        } else {
                            String filename = pathname.getName().toLowerCase();
                            return filename.endsWith(".pc") ;
                        }
                    }

                    @Override
                    public String getDescription() {
                        return ".pc (PC Simulator save file)";
                    }
                };
                fileChooser.addChoosableFileFilter(filter);
                fileChooser.setFileFilter(filter);
                fileChooser.setDialogTitle("Save to...");
                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    selected = fileChooser.getSelectedFile();
                    try {
                        FileWriter writer = new FileWriter(selected);
                        writer.write(getEncryptedDecryptedString(Output.getText()));
                        writer.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        copyToClipboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringSelection stringSelection = new StringSelection(Output.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
        });
        guideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Path chmPath = null;
                try {
                    chmPath = Files.createTempFile(null, ".chm");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                try (InputStream chmResource =
                             getClass().getResourceAsStream("/resources/PCSimulatorSaveEditor.chm")) {

                    Files.copy(chmResource, chmPath,
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                chmWeb.serveChmFile(17727, chmPath.toFile().getPath());
                try {
                    Desktop.getDesktop().browse(URI.create("http://localhost:" + chmWeb.getServerPort() + "/@index.html"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
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
