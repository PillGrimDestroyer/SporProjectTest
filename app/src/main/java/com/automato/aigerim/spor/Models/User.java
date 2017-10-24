package com.automato.aigerim.spor.Models;

import java.util.HashMap;

public class User {
    public static String id;
    public static String name;
    public static String email;
    public static double money;
    public static boolean hasImage;
    public static String birthday;
    public static String gender;
    public static boolean confirmed = false;
    public static boolean receiveNotifications;
    public static HashMap<String, Notification> history;
}