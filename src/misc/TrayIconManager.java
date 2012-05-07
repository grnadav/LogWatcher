package misc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class TrayIconManager {
    private static StringBuilder logPath = new StringBuilder();
    private static StringBuilder watchExpr = new StringBuilder();

    private static LogWatcher logWatcher;
    private static boolean isWorking = false;

    public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        //Schedule a job for the event-dispatching thread:
        //adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon =
                new TrayIcon(createImage("bulb.gif", "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a popup menu components
        MenuItem setLogPathItem = new MenuItem("Set Log Path");
        MenuItem setWatchPhraseItem = new MenuItem("Set Watch Phrase");
        MenuItem startStopItem = new MenuItem("Start \\ Stop");
        MenuItem exitItem = new MenuItem("Exit");

        //Add components to popup menu
        popup.add(setLogPathItem);
        popup.add(setWatchPhraseItem);
        popup.addSeparator();
        popup.add(startStopItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        startStopItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isWorking = !isWorking;
                if (logWatcher == null && isWorking) {
                    logWatcher = new LogWatcher(logPath.toString(), watchExpr.toString()) {
                        @Override
                        protected void onChange(String message) {
                            trayIcon.displayMessage("File changed", message, TrayIcon.MessageType.NONE);
                        }
                    };
                }
                if (logWatcher != null) {
                    logWatcher.setWorking(isWorking);
                }
            }
        });

        setWatchPhraseItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyTextField textField = new MyTextField(watchExpr, "Watch expression", "Set watch expression"); //.setVisible(true);

                textField.addWindowListener(new DummyWindowListener() {
                    @Override
                    public void windowDeactivated(WindowEvent e) {
                        System.out.println("window closed");
                        System.out.println("watch expr:" + watchExpr.toString());
                        if (logWatcher != null) {
                            logWatcher.setWatchExpr(watchExpr.toString());
                        }
                    }
                });
                textField.setVisible(true);
            }
        });

        setLogPathItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MyTextField textField = new MyTextField(logPath, "Set Path", "Set log path"); //.setVisible(true);

                textField.addWindowListener(new DummyWindowListener() {
                    @Override
                    public void windowDeactivated(WindowEvent e) {
                        System.out.println("window closed");
                        System.out.println("logpath:" + logPath.toString());
                        if (logWatcher != null) {
                            logWatcher.setLogPath(logPath.toString());
                        }
                    }
                });
                textField.setVisible(true);
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
    }

    //Obtain the image URL
    protected static Image createImage(String path, String description) {
        URL imageURL = TrayIconManager.class.getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}