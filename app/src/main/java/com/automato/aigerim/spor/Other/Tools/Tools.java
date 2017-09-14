package com.automato.aigerim.spor.Other.Tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HAOR on 08.09.2017.
 */

public class Tools {

    public boolean regex(String pattern, String text) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        return m.find();
    }

    public String regex(String pattern, String text, int group) {
        String ret = "";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        if (m.find()) {
            ret = m.group(group);
        }
        return ret;
    }

    public boolean isNullOrWhitespace(String s) {
        return s == null || isWhitespace(s);

    }

    private boolean isWhitespace(String s) {
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
}
