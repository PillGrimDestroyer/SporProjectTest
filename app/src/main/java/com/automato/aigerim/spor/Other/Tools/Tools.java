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
}
