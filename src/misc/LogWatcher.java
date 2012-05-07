package misc;

/**
 * Created with IntelliJ IDEA.
 * User: greenben
 * Date: 06/05/12
 * Time: 17:19
 */

//import java.nio.file.WatchService;

import java.io.*;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public abstract class LogWatcher {

    private String logPath;
    private String watchExpr;
    private Boolean isWorking = false;
    private Timer timer;
    private BufferedReader bufferedReader;
    private DataInputStream dataInputStream;

    public LogWatcher(String logPath, String watchExpr) {
        this.logPath = logPath;
        this.watchExpr = watchExpr;

       openFile();
    }

    private void openFile() {
        // Open the file that is the first
        // command line parameter
        FileInputStream fstream;
        try {
            fstream = new FileInputStream(logPath);
        } catch (FileNotFoundException e) {
            return;
        }
        // Get the object of DataInputStream
        this.dataInputStream = new DataInputStream(fstream);
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.dataInputStream));
    }

    private void closeFile() {
        try {
            this.dataInputStream.close();
            this.bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void checkLog(File file) {

        String strLine;
        //Read File Line By Line
        try {
            while ((strLine = this.bufferedReader.readLine()) != null)   {
                // Print the content on the console
                if (!this.watchExpr.equals("") && strLine.contains(this.watchExpr)) {
                    System.out.println (strLine);
                    this.onChange(strLine + " @ (" + new Date().toString() + ")");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWorking(boolean isWorking) {
        this.isWorking = isWorking;

        if (this.timer != null) {
            timer.cancel();
        }
        if (isWorking) {
            TimerTask task = new FileWatcher(new File(this.logPath)) {
                @Override
                protected void onChange(File file) {
                    checkLog(file);
                }
            };

            this.timer = new Timer();
            // repeat the check every second
            timer.schedule(task, new Date(), 1000);
        }
    }

    public boolean isWorking() {
        return this.isWorking;
    }

    public void setLogPath(String logPath) {
        closeFile();
        this.logPath = logPath;
        openFile();
    }

    public void setWatchExpr(String watchExpr) {
        this.watchExpr = watchExpr;
    }

    protected abstract void onChange(String message);
}
