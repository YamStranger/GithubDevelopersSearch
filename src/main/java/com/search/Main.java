package com.search;

import com.github.AccessException;
import com.github.user.RegisteredUsers;
import com.github.user.UserEntry;
import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.util.ArgumentsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Main class for all operation
 * Input params:
 * -Personal access tokens -key=value
 * <p>
 * <p>
 * <p>
 * User: YamStranger
 * Date: 4/14/15
 * Time: 10:08 AM
 */


public class Main {
    public static final PrintStream user = System.out;
    public static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) {
        Thread.currentThread().setName("Thread-Main");
        System.out.println("run in " + Paths.get("").toAbsolutePath());
        final Map<String, String> input = new ArgumentsParser(args).arguments();
        final String token = input.get("token");
        String file = input.get("file");
        if (file == null) {
            file = "result";
        }
        String last_id = input.get("last_id");

        final String delimeterAsString = input.get("delimeter");
        int delimeter = 1;
        if (delimeterAsString != null) {
            delimeter = Integer.valueOf(delimeterAsString);
        }
        final Github main = new RtGithub(token);

        if (last_id == null) {
            throw new IllegalArgumentException("last_id cant not be null");
        }

        final RegisteredUsers registered = new RegisteredUsers(main, last_id);

        BufferedReader bufferedReader;
        Map<String, Searcher> searcherMap = new HashMap<>();
        BlockingQueue<String> tasks = new LinkedBlockingQueue<>(100);
        BlockingQueue<Result> results = new LinkedBlockingQueue<>(100);

        Storage storage = new Storage(Paths.get(file), results);
        storage.start();

        final Path tokens = Paths.get("tokens");
        try {
            if (!Files.exists(tokens)) {
                Files.createFile(tokens);
            }
            Iterator<UserEntry> users = registered.iterator();
            //init
            while (!Thread.currentThread().isInterrupted()) {
                bufferedReader = Files.newBufferedReader(tokens);
                List<String> updates = new LinkedList<>();
                String read = null;
                while ((read = bufferedReader.readLine()) != null && read.trim().length() > 2) {
                    updates.add(read.trim());
                }
                bufferedReader.close();
                final Iterator<String> known = searcherMap.keySet().iterator();
                while (known.hasNext()) {
                    final String deleted = known.next();
                    if (!updates.contains(deleted)) {
                        final Thread thread = searcherMap.get(deleted);
                        thread.interrupt();
                        known.remove();
                    }
                }
                final Iterator<String> value = updates.iterator();
                while (value.hasNext()) {
                    final String added = value.next();
                    if (!searcherMap.containsKey(added)) {
                        final Searcher searcher = new Searcher(tasks, results, new RtGithub(added));
                        searcherMap.put(added, searcher);
                        searcher.start();
                        value.remove();
                    }
                }

                try {
                    if (tasks.remainingCapacity() > 0 && users.hasNext()) {
                        final UserEntry userEntry = users.next();
                        try {
                            Integer id = Integer.valueOf(userEntry.id());
                            if (id % delimeter == 0) {
                                System.out.println(Thread.currentThread().getName() + " pushed :" + userEntry.apiUrl() + ", id:" + userEntry.id());
                                tasks.put(userEntry.apiUrl());
                            }
                        } catch (NumberFormatException e) {
                            logger.error("skip user" + userEntry.login(), e);
                        }
                    } else {
                        System.out.println(Thread.currentThread().getName() + " tasks.remainingCapacity() :" + tasks.remainingCapacity());
                    }
                } catch (AccessException accessException) {
                    logger.error("user processing error", accessException);
                }

                Thread.sleep(1000);

/*
                for (final UserEntry userEntry : registered) {
                    Integer id = Integer.valueOf(userEntry.id());
                    if (id % delimeter == 0) {
                        tasks.remainingCapacity(userEntry.id());
                    }
                }
*/
            }
        } catch (IOException io) {
            logger.error("exception during reading file ", io);
        } catch (InterruptedException inter) {
            logger.error("exception during reading file ", inter);
        }
    }

}

