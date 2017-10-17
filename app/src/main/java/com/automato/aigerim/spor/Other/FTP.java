package com.automato.aigerim.spor.Other;

/**
 * Created by HAOR on 16.10.2017.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FTP {
    FTPClient ftpClient = new FTPClient();
    private String FTPServer = "95.85.60.144";
    private Integer FTPPort = 21;
    private String FTPUser = "user";
    private String FTPPass = "ce87b6ee3a667fdba9dca027af";

    boolean Connect() throws IOException {
        ftpClient.connect(FTPServer, FTPPort);
        int replyCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            Log.w("FTP ERROR", "Operation failed. Server reply code: " + replyCode);
            return false;
        }
        ftpClient.enterLocalPassiveMode();
        return true;
    }

    Bitmap DownloadFile(String fileName) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = null;
        try {
            boolean result = ftpClient.retrieveFile(fileName, stream);
            if (result) {
                byte[] bitmapdata = stream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
            } else {
                throw new IOException("Не получилось скачать файл");
            }
        }finally {
            stream.close();
        }
        return bitmap;
    }

    boolean Login() throws IOException {
        boolean success = ftpClient.login(FTPUser, FTPPass);
        if (!success) {
            throw new IOException("Не получилось залогиниться на сервер");
        }
        return true;
    }

    void LogOut() throws IOException {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ex) {
            throw new IOException("Не получилось выйти с аккаунта", ex.fillInStackTrace());
        }
    }

    boolean DeleteFile(String FilePath) throws IOException {
        if (!ftpClient.deleteFile(FilePath))
            throw new IOException("Не получилось удалить файл");
        return true;
    }

    boolean CreateDir(String dir) throws IOException {
        if (!ftpClient.makeDirectory(dir))
            throw new IOException("Не получилось создать директорию");
        return true;
    }

    boolean CreateFile(String FileName, InputStream stream) throws IOException {
        try {
            boolean done = ftpClient.storeFile(FileName, stream);
            if (!done) {
                throw new IOException("Не получилось создать файл");
            }
            return true;
        } finally {
            stream.close();
        }
    }

    boolean UploadString(String FileName, String text) throws IOException {
        InputStream is = new ByteArrayInputStream(text.getBytes());

        OutputStream outputStream = ftpClient.storeFileStream(FileName);
        byte[] bytesIn = new byte[4096];
        int read = 0;

        while ((read = is.read(bytesIn)) != -1) {
            outputStream.write(bytesIn, 0, read);
        }
        is.close();
        outputStream.close();

        if (!ftpClient.completePendingCommand())
            throw new IOException("Не получилось загрузить эту строку на сервер");
        return true;
    }

    boolean ChangeDir(String path) throws IOException {
        if (!ftpClient.changeWorkingDirectory(path))
            throw new IOException("Не получилось перейти в другую директорию");
        return true;
    }

    boolean CheckDirectoryExists(String dirPath) throws IOException {
        try {
            boolean bool = ftpClient.changeWorkingDirectory(dirPath);
            int returnCode = ftpClient.getReplyCode();
            return !(!bool || returnCode == 550);
        } catch (IOException ex) {
            throw new IOException("Ошибка при проверке наличия дириктории", ex.fillInStackTrace());
        }
    }

    boolean CheckFileExists(String filePath) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = ftpClient.retrieveFileStream(filePath);
            int returnCode = ftpClient.getReplyCode();
            return !(inputStream == null || returnCode == 550);
        }catch (IOException ex){
            throw new IOException("Ошибка при проверке на наличия файла", ex.fillInStackTrace());
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }
}
