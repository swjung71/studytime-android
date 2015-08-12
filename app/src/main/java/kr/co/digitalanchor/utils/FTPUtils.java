package kr.co.digitalanchor.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Thomas on 2015-08-12.
 */
public class FTPUtils {

    String SERVERIP = "14.63.225.89";
    int PORT = 21;
    String SERVERID = "anonymous";
    String SERVERPW = "nobody";
    FTPClient ftpClient = null;

    public boolean DownloadContents(String filename) {

        this.ftpClient = new FTPClient();

        connect();

        login(SERVERID, SERVERPW);

        cd("Root/pub");//input u r directory

        FTPFile[] files = list();

        if (files == null) {
            return false;
        }

        ArrayList<String> ImageIds_tmp = new ArrayList<String>();

        for (int i = 0; i < files.length; i++) {

            String fileName = files[i].getName();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

            long size = files[i].getSize();

            extension = extension.toUpperCase();

            if (size > 0) {

                for (int j = 0; j < size; j++) {

                    if (filename.equalsIgnoreCase(fileName.substring(0, fileName.indexOf(".")))) {

                        StringBuffer furl = new StringBuffer("/sdcard/temp/");
                        furl.append(fileName);
                        ImageIds_tmp.add(furl.toString());
                        get(fileName, fileName);
                    }
                }
            }
        }
        logout();
        disconnect();
        return true;
    }

    public boolean login(String user, String password) {
        try {
            this.connect();
            return this.ftpClient.login(user, password);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }


    private boolean logout() {
        try {
            return this.ftpClient.logout();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }

    public void connect() {
        try {
            this.ftpClient.connect(SERVERIP, PORT);
            int reply;
            reply = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                this.ftpClient.disconnect();
            }
        } catch (IOException ioe) {
            if (this.ftpClient.isConnected()) {
                try {
                    this.ftpClient.disconnect();
                } catch (IOException f) {
                    ;
                }
            }
        }
    }

    public FTPFile[] list() {
        FTPFile[] files = null;
        try {
            files = this.ftpClient.listFiles();
            return files;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public File get(String source, String target) {
        OutputStream output = null;
        try {
            StringBuffer furl = new StringBuffer("/sdcard/xxx/");
            File path = new File(furl.toString());
            if (!path.isDirectory()) {
                path.mkdirs();
            }

            furl.append(target);
            File local = new File(furl.toString());
            if (local.isFile()) {
                return null;
            }
            output = new FileOutputStream(local);
        } catch (FileNotFoundException fnfe) {
            ;
        }
        File file = new File(source);
        try {
            if (this.ftpClient.retrieveFile(source, output)) {
                return file;
            }
        } catch (IOException ioe) {
            ;
        }
        return null;
    }

    public void cd(String path) {
        try {
            this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.changeWorkingDirectory(path);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            this.ftpClient.disconnect();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
