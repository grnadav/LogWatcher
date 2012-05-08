package misc;

import java.io.File;
import java.util.TimerTask;

public abstract class FileWatcher extends TimerTask {
    private long length;
    private File file;

    public FileWatcher( File file ) {
        this.file = file;
        this.length = file.length();
    }

    public final void run() {
        long length = file.length();

        if( this.length > length) {
            this.length = length;
            onFileReset();
            return;
        }
        if( this.length != length) {
            this.length = length;
            onChange(file);
        }
    }

    protected abstract void onChange( File file);
    protected abstract void onFileReset();
}