package com.coffeinum.jarvis.contentprovider;

import java.net.MalformedURLException;
import java.net.URL;

public class Utils {

    public static URL convertToUrl(String s) {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
