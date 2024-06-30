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
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu fileMenu = new JMenu("File");
    private final JMenu helpMenu = new JMenu("Help");
    private final JMenu optionsMenu = new JMenu("Options");
    private final JMenuItem open = new JMenuItem("Open");
    private final JMenuItem save = new JMenuItem("Save");
    private final JMenuItem help = new JMenuItem("Help");
    private final JCheckBoxMenuItem encryptWhenSaving = new JCheckBoxMenuItem("Automatically encrypt when saving file");
    private final JCheckBoxMenuItem decryptWhenOpening = new JCheckBoxMenuItem("Automatically decrypt when opening file");
    private final ChmWeb chmWeb = new ChmWeb();
    private JTextArea Output;
    private JTextArea Input;
    private JButton decryptButton;
    private JPanel panel;
    private JButton copyToClipboardButton;

    private String getEncryptedDecryptedString(String decryptEncrypt) throws InterruptedException {
        PerformOperation crypt = new PerformOperation();
        Thread secondThread = new Thread(crypt);
        crypt.setDecryptEncrypt(decryptEncrypt);
        secondThread.start();
        secondThread.join();
        return crypt.getText();
    }

    public MainGUI() {
        decryptButton.addActionListener(_ -> {
            try {
                Output.setText(getEncryptedDecryptedString(Input.getText()));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        copyToClipboardButton.addActionListener(_ -> {
            StringSelection stringSelection = new StringSelection(Output.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String home = System.getProperty("user.home");
                File downloads = new File(home+"/Downloads/");
                File selected;
                JFileChooser fileChooser = getjFileChooser(downloads);
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    selected = fileChooser.getSelectedFile();
                    try {
                        Output.setText(decryptWhenChecked(Files.readString(selected.toPath())));
                    } catch (IOException | InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            public String decryptWhenChecked(String arg) throws InterruptedException {
                if(decryptWhenOpening.isSelected()){
                    return getEncryptedDecryptedString(arg);
                } else {
                    return arg;
                }
            }

            private static JFileChooser getjFileChooser(File downloads) {
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
                return fileChooser;
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String home = System.getProperty("user.home");
                File downloads = new File(home+"/Downloads/");
                File selected;
                JFileChooser fileChooser = getjFileChooser(downloads);
                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    selected = fileChooser.getSelectedFile();
                    try {
                        FileWriter writer = new FileWriter(selected);
                        writer.write(encryptWhenChecked(Output.getText()));
                        writer.close();
                    } catch (IOException | InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            public String encryptWhenChecked(String arg) throws InterruptedException {
                if(encryptWhenSaving.isSelected()){
                    return getEncryptedDecryptedString(arg);
                } else {
                    return arg;
                }
            }

            private static JFileChooser getjFileChooser(File downloads) {
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
                return fileChooser;
            }
        });
        help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Path chmPath;
                try {
                    chmPath = Files.createTempFile(null, ".chm");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                try (InputStream chmResource =
                             getClass().getResourceAsStream("/resources/PCSimulatorSaveEditor.chm")) {

                    assert chmResource != null;
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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception ex) {
            System.exit(-1);
        }
        MainGUI gui = new MainGUI();
        JFrame frame = new JFrame("PC Simulator Save Editor");
        frame.setJMenuBar(gui.menuBar);
        // Menu bar add menus
        gui.menuBar.add(gui.fileMenu);
        gui.menuBar.add(gui.optionsMenu);
        gui.menuBar.add(gui.helpMenu);
        // Menus add items
        gui.fileMenu.add(gui.open);
        gui.fileMenu.add(gui.save);
        gui.decryptWhenOpening.setSelected(true);
        gui.encryptWhenSaving.setSelected(true);
        gui.optionsMenu.add(gui.decryptWhenOpening);
        gui.optionsMenu.add(gui.encryptWhenSaving);
        gui.helpMenu.add(gui.help);

        frame.setContentPane(gui.panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

class PerformOperation implements Runnable {
    public String getText() {
        return text;
    }


    private volatile String decryptEncrypt;

    private volatile String text;

    @Override
    public void run() {
        int key = 0x81;
        ArrayList<String> charArray = new ArrayList<>();
        Arrays.stream(decryptEncrypt.split("")).forEach(ee -> charArray.add(Character.toString(Character.codePointAt(ee, 0) ^ key)));
        String[] CharacterArrayed = charArray.toArray(new String[0]);
        StringBuilder output = new StringBuilder();
        for (String s : CharacterArrayed) {
            output.append(s);
        }
        text = output.toString();
    }

    public void setDecryptEncrypt(String decryptEncrypt) {
        this.decryptEncrypt = decryptEncrypt;
    }
}
