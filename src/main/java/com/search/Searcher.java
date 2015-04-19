package com.search;

import com.github.repo.UserRepository;
import com.github.user.UserEntry;
import com.github.user.UserReader;
import com.jcabi.github.Github;
import com.util.Dates;
import com.util.UrlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.BlockingQueue;

/**
 * User: YamStranger
 * Date: 4/14/15
 * Time: 10:40 PM
 */
public class Searcher extends Thread {
    public static Logger logger = LoggerFactory.getLogger(Searcher.class);
    private BlockingQueue<String> users;
    private BlockingQueue<Result> results;
    public boolean interrupt = false;

    private Github github;

    public Searcher(BlockingQueue<String> queue, BlockingQueue<Result> results, Github github) {
        this.users = queue;
        this.github = github;
        this.results = results;
    }

    @Override
    public void run() {
        setName("Searcher-" + getName());
        System.out.println("started " + getName());
        try {
            while (!isInterrupted()) {
                String userUrl = this.users.take();
                System.out.println(getName() + " : take user " + userUrl);
                try {
/*
                Limit.Smart limit = new Limit.Smart(github.limits().get("core"));
                System.out.println("core: " + limit.limit() + ":" + limit.remaining() + ':' + limit.reset());
                Limit.Smart search = new Limit.Smart(github.limits().get("search"));
                System.out.println("search: " + limit.limit() + ":" + limit.remaining() + ':' + limit.json().getInt("reset"));
*/
                    int count = 0;
                    boolean accept = true;
                    long allUserCommits = 0;
                    long allUserCommitsThisYear = 0;


                    UserEntry userEntry = new UserReader(github, userUrl).load();
                    Dates start = new Dates();
                    System.out.println(getName() + " : started processing userEntry.login() " + userEntry.login() + ", id:" + userEntry.id());
                    if (userEntry.email() == null
                            || !userEntry.email().contains("@")) {
                        accept = false;
                    } else if (userEntry.contributed() == null
                            || Integer.parseInt(userEntry.contributed()) < 10) {
                        accept = false;
                    }
                    if (accept) {
                        /*parsing page*/
                        Result result = new Result();

                        /*fill user data*/
                        fillUserData(userEntry, result);

                        for (UserRepository userRepository : userEntry.repositories()) {
                            UserRepositoryProcessor processor =
                                    new UserRepositoryProcessor(result, userEntry, userRepository);
                            processor.process();
                            if (Thread.currentThread().isInterrupted()) {
                                break;
                            }
                            // break;//testing
                        }
                        boolean parsed = loadFromPage(userEntry.gitHubUrl(), result);
                        results.put(result);
                        System.out.println(getName() + " : push result id:"
                                + userEntry.id() + ", login:" + userEntry.login()
                                + " it takes " +
                                new Dates().difference(start, Calendar.MINUTE)
                                + " minutes");
                    }
                } catch (Exception e) {
                    logger.debug("user login exception", e);
                }
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }

    private boolean loadFromPage(String url, Result result) {
        boolean parsed = false;
        try {
            UrlParser urlParser = new UrlParser(url);
            int currentStrike = urlParser.currentStreak();
            int contributionsLastYear = urlParser.contributionsLastYear();
            int longestStreak = urlParser.longestStreak();
            if (contributionsLastYear >= 0 && currentStrike >= 0 && longestStreak >= 0) {
                parsed = true;
                result.currentStreak = currentStrike;
                result.longestStreak = longestStreak;
                result.contributionsLastYear = contributionsLastYear;
            } else {
                parsed = false;
            }
        } catch (IOException e) {
            logger.error("can't parse page", e);
        }
        return parsed;
    }


    private void fillUserData(UserEntry userEntry, Result result) {
        try {
            result.contributedTo = Integer.valueOf(userEntry.contributed());
        } catch (Exception e) {
            logger.error("error during parsing parameter contributedTo=\"" + userEntry.contributed() + "\"", e);
        }
        try {
            result.followers = Integer.valueOf(userEntry.followers());
        } catch (Exception e) {
            logger.error("error during parsing parameter followers=\"" + userEntry.followers() + "\"", e);
        }
        try {
            result.following = Integer.valueOf(userEntry.following());
        } catch (Exception e) {
            logger.error("error during parsing parameter following=\"" + userEntry.following() + "\"", e);
        }
        result.developerName = userEntry.name();
        result.developerId = userEntry.login();
        result.joinDate = userEntry.created();
        result.country = userEntry.location();
        result.developerEmail = userEntry.email();
        result.webPage = userEntry.gitHubUrl();
    }
}
