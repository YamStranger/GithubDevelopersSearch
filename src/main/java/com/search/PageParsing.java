package com.search;

import com.util.UrlParser;

import java.io.IOException;

/**
 * User: YamStranger
 * Date: 4/16/15
 * Time: 8:58 AM
 */
public class PageParsing {
    public static void main(String... arguments) throws IOException{
        String url = "https://github.com/uggedal";
        UrlParser parser = new UrlParser(url);
        parser.parse();
    }
}
