package com.automato.aigerim.spor.Other;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.automato.aigerim.spor.Models.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HAOR on 08.09.2017.
 */

public class Tools {

    public static boolean regex(String pattern, String text) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        return m.find();
    }

    public static String regex(String pattern, String text, int group) {
        String ret = "";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        if (m.find()) {
            ret = m.group(group);
        }
        return ret;
    }

    public static boolean isNullOrWhitespace(String s) {
        return s == null || isWhitespace(s);

    }

    public static void uploadUserPhoto(final Uri selectedImage, final Context context, final ProgressBar progressBar, final boolean join) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FTP ftp = new FTP();
                try {
                    ftp.Connect();
                    ftp.Login();
                    ftp.ChangeDir("www");

                    if (!ftp.CheckDirectoryExists("Photos")) {
                        ftp.CreateDir("Photos");
                        ftp.ChangeDir("Photos");
                    }

                    if (!ftp.CheckDirectoryExists("User")) {
                        ftp.CreateDir("User");
                        ftp.ChangeDir("User");
                    }
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(bitmapdata);

                    ftp.CreateFile(User.id + ".jpg", inputStream);
                    if (!User.hasImage) {
                        User.hasImage = true;
                        Api api = new Api(context);
                        api.updateUser();
                    }
                } catch (Exception e) {
                    if (!((Activity) context).isFinishing()) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Не удалось загрузить картинку на сервер", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    e.printStackTrace();
                } finally {
                    try {
                        ftp.LogOut();
                        if (!((Activity) context).isFinishing()) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressBar != null)
                                        progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    } catch (IOException ignored) {

                    }
                }
            }
        });
        thread.setDaemon(join);
        thread.start();
        if (join) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getRealPathFromUri(Context context, Uri selectedImage) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(selectedImage, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void uploadUserPhoto(final Uri selectedImage, final Context context) {
        uploadUserPhoto(selectedImage, context, null, false);
    }

    public static void uploadUserPhoto(final Uri selectedImage, final Context context, final boolean join) {
        uploadUserPhoto(selectedImage, context, null, join);
    }

    public static Bitmap downloadDisputePhoto(final Context context, final String dispute_photo) {
        FTP ftp = new FTP();
        Bitmap bitmap = null;
        try {
            ftp.Connect();
            ftp.Login();
            ftp.ChangeDir("www");

            if (!ftp.CheckDirectoryExists("Photos"))
                throw new IOException("Папки Photos не существует");

            if (!ftp.CheckDirectoryExists("Dispute"))
                throw new IOException("Папки Dispute не существует");

            bitmap = ftp.DownloadFile(dispute_photo);
        } catch (Exception e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Не удалось скачать картинку с сервера", Toast.LENGTH_SHORT).show();
                }
            });
        } finally {
            try {
                ftp.LogOut();
            } catch (IOException ignored) {

            }
        }
        return bitmap;
    }

    public static Bitmap downloadUserPhoto(final Context context) {
        FTP ftp = new FTP();
        Bitmap bitmap = null;
        try {
            ftp.Connect();
            ftp.Login();
            ftp.ChangeDir("www");

            if (!ftp.CheckDirectoryExists("Photos"))
                throw new IOException("Папки Photos не существует");

            if (!ftp.CheckDirectoryExists("User"))
                throw new IOException("Папки User не существует");

            bitmap = ftp.DownloadFile(User.id + ".jpg");
        } catch (Exception e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Не удалось скачать картинку с сервера", Toast.LENGTH_SHORT).show();
                }
            });
        } finally {
            try {
                ftp.LogOut();
            } catch (IOException ignored) {

            }
        }
        return bitmap;
    }

    private static boolean isWhitespace(String s) {
        int length = s.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(s.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public static void uploadDisputePhoto(final Context context, final Uri selectedImage, final String name) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FTP ftp = new FTP();
                try {
                    ftp.Connect();
                    ftp.Login();
                    ftp.ChangeDir("www");

                    if (!ftp.CheckDirectoryExists("Photos")) {
                        ftp.CreateDir("Photos");
                        ftp.ChangeDir("Photos");
                    }

                    if (!ftp.CheckDirectoryExists("Dispute")) {
                        ftp.CreateDir("Dispute");
                        ftp.ChangeDir("Dispute");
                    }

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(bitmapdata);

                    ftp.CreateFile(name, inputStream);
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Не удалось загрузить картинку на сервер", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                } finally {
                    try {
                        ftp.LogOut();
                    } catch (IOException ignored) {

                    }
                }
            }
        });
        thread.setDaemon(false);
        thread.start();
    }
}
