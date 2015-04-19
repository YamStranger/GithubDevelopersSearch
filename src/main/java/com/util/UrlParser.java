package com.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: YamStranger
 * Date: 4/15/15
 * Time: 2:56 PM
 */
public class UrlParser {
    private String url = "";
    private int longestStreak = -1;
    private int currentStreak = -1;
    private int contributionsLastYear = -1;

    public UrlParser(final String url) {
        this.url = url;
    }

    public void parse() throws IOException {
        if (longestStreak < 0 || currentStreak < 0 || contributionsLastYear < 0) {
            URLConnection con = new URL(this.url).openConnection();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                StringBuilder pageBuilder = new StringBuilder();
                String readed = null;
                while ((readed = bufferedReader.readLine()) != null) {
                    pageBuilder.append(readed);
                }
                String page = pageBuilder.toString();
                //System.out.println(page);
                Pattern pattern = Pattern.compile
                        ("((Contributions in the last year)+[^0-9]+(?<total>[0-9,.]+)).+" +
                                "((Longest streak)+[^0-9]+(?<longest>[0-9,.]+)).+" +
                                "((Current streak)+[^0-9]+(?<current>[0-9,.]+))");
                Matcher matcher = pattern.matcher(page);
                String contributions = "";
                String longest = "";
                String current = "";
                if (matcher.find()) {
                    contributions = matcher.group("total").replace(",","");
                    this.contributionsLastYear = Integer.valueOf(contributions);
                    longest = matcher.group("longest").replace(",","");
                    this.longestStreak = Integer.valueOf(longest);
                    current = matcher.group("current").replace(",","");
                    this.currentStreak = Integer.valueOf(current);
                }
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
    }

    public int longestStreak() throws IOException {
        parse();
        return this.longestStreak;
    }

    public int currentStreak() throws IOException {
        parse();
        return this.currentStreak;
    }

    public int contributionsLastYear() throws IOException {
        parse();
        return this.contributionsLastYear;
    }
}
