package com.mokkachocolata.pcsimulatorsaveeditor.app;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.FileDialog;
import org.jchmlib.app.ChmWeb;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import org.eclipse.swt.widgets.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import org.eclipse.swt.browser.Browser;

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
    private JTextArea Output = new JTextArea();
    private JTextArea Input = new JTextArea();
    private final JScrollPane pane0 = new JScrollPane(Input);
    private final JScrollPane pane1 = new JScrollPane(Output);
    private JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pane0, pane1);
    private BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
    private final boolean PLATFORM = System.getProperty("os.name").startsWith("Windows");

    private String getEncryptedDecryptedString(String decryptEncrypt) throws InterruptedException {
        PerformOperation crypt = new PerformOperation();
        Thread secondThread = new Thread(crypt);
        crypt.setDecryptEncrypt(decryptEncrypt);
        secondThread.start();
        secondThread.join();
        return crypt.getText();
    }

    public MainGUI() {
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String home = System.getProperty("user.home");
                File downloads = new File(home+"/Downloads/");
                File selected;
                final Display display = new Display();
                FileDialog dialog = getFileDialog(display, downloads);
                display.dispose();
                if (!Objects.equals(dialog.getFileName(), "")) {
                    selected = new File(dialog.getFilterPath() + getWin32OrLinuxSeperator() + dialog.getFileName());
                    try {
                        Output.setText(decryptWhenChecked(Files.readString(selected.toPath())));
                    } catch (IOException | InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            private static FileDialog getFileDialog(Display display, File downloads) {
                final Shell shell = new Shell(display);
                FileDialog dialog = new FileDialog(shell, SWT.OPEN);
                dialog.setText("Open PC Simulator Save File");
                String[] filterNames = {"PC Simulator save file", "All Files"};
                String[] filterExtensions = {"*.pc","*"};
                dialog.setFilterNames(filterNames);
                dialog.setFilterExtensions(filterExtensions);
                dialog.setFilterPath(downloads.getPath());
                dialog.open();
                return dialog;
            }

            private String getWin32OrLinuxSeperator() {
                if (PLATFORM) {
                    return "\\";
                } else {
                    return "/";
                }
            }

            public String decryptWhenChecked(String arg) throws InterruptedException {
                if(decryptWhenOpening.isSelected()){
                    return getEncryptedDecryptedString(arg);
                } else {
                    return arg;
                }
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String home = System.getProperty("user.home");
                File downloads = new File(home+"/Downloads/");
                File selected;
                final Display display = new Display();
                final Shell shell = new Shell(display);
                FileDialog dialog = new FileDialog(shell, SWT.SAVE);
                dialog.setText("Save to");
                String[] filterNames = {"PC Simulator save file", "All Files"};
                String[] filterExtensions = {"*.pc","*"};
                dialog.setFilterNames(filterNames);
                dialog.setFilterExtensions(filterExtensions);
                dialog.setFilterPath(downloads.getPath());
                dialog.open();
                display.dispose();
                if (!Objects.equals(dialog.getFileName(), "")) {
                    selected = new File(dialog.getFilterPath() + getWin32OrLinuxSeperator() + dialog.getFileName());
                    try {
                        FileWriter writer = new FileWriter(selected);
                        writer.write(encryptWhenChecked(Output.getText()));
                        writer.close();
                        System.gc();
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

            private String getWin32OrLinuxSeperator() {
                if (PLATFORM) {
                    return "\\";
                } else {
                    return "/";
                }
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
                Display display = new Display();
                Shell shell = new Shell(display);
                shell.setLayout(new FillLayout());
                final Browser b = new Browser(shell, SWT.WEBKIT);
                shell.setText("CHM Viewer");
                shell.setSize(640,480);
                b.setUrl("http://localhost:" + chmWeb.getServerPort() + "/@index.html");
                shell.open();
            }
        });
        Input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                System.gc();
            }
            private String removeLastChar(String s) {
                return (s == null || s.isEmpty())
                        ? null
                        : (s.substring(0, s.length() - 1));
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    Input.setText(removeLastChar(Input.getText()));
                    try {
                        Output.setText(getEncryptedDecryptedString(Input.getText()));
                    } catch (InterruptedException ee) {
                        throw new RuntimeException(ee);
                    }
                }
            }
        });
        Output.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                System.gc();
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
        frame.setContentPane(gui.panel);
        gui.panel.setDividerLocation(320);
        gui.menuBar.add(gui.fileMenu);
        gui.menuBar.add(gui.optionsMenu);
        gui.menuBar.add(gui.helpMenu);
        gui.fileMenu.setMnemonic(KeyEvent.VK_F);
        gui.optionsMenu.setMnemonic(KeyEvent.VK_O);
        gui.helpMenu.setMnemonic(KeyEvent.VK_H);
        // Menus add items
        gui.fileMenu.add(gui.open);
        gui.open.setMnemonic(KeyEvent.VK_P);
        gui.open.setToolTipText("Open a PC Simulator save file, and outputs it to the Output textbox.");
        gui.fileMenu.add(gui.save);
        gui.save.setMnemonic(KeyEvent.VK_S);
        gui.save.setToolTipText("Saves the current output text to the specified file.");
        gui.decryptWhenOpening.setSelected(true);
        gui.decryptWhenOpening.setMnemonic(KeyEvent.VK_D);
        gui.decryptWhenOpening.setToolTipText("When checked, decrypts the file when opening.");
        gui.encryptWhenSaving.setSelected(true);
        gui.encryptWhenSaving.setMnemonic(KeyEvent.VK_E);
        gui.encryptWhenSaving.setToolTipText("When checked, encrypts the output text before saving it.");
        gui.optionsMenu.add(gui.decryptWhenOpening);
        gui.optionsMenu.add(gui.encryptWhenSaving);
        gui.helpMenu.add(gui.help);
        gui.help.setMnemonic(KeyEvent.VK_E);
        gui.help.setToolTipText("Opens the help document.");
        ;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(new Dimension(320,200));
        frame.setSize(new Dimension(640,480));
        frame.setVisible(true);
    }
}

class PerformOperation implements Runnable {
    public String getText() {
        return text;
    }


    private volatile String decryptEncrypt;

    private volatile String text;

    public boolean isWriteToTxtMode() {
        return writeToTxtMode;
    }

    public void setWriteToTxtMode(boolean writeToTxtMode) {
        this.writeToTxtMode = writeToTxtMode;
    }

    private volatile boolean writeToTxtMode = false;

    @Override
    public void run() {
        System.gc();
        int key = 0x81;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < decryptEncrypt.length(); i++) {
            stringBuilder.append((char) (decryptEncrypt.charAt(i) ^ key));
        }
        System.gc();
        text = stringBuilder.toString();
    }

    public void setDecryptEncrypt(String decryptEncrypt) {
        this.decryptEncrypt = decryptEncrypt;
    }
}
