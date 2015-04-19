package com.search;

import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;

/**
 * User: YamStranger
 * Date: 4/15/15
 * Time: 4:18 PM
 */
public class Storage extends Thread {
    public static Logger logger = LoggerFactory.getLogger(Storage.class);
    private BlockingQueue<Result> results;
    private Path storage;
    public static String separator = ";";
    public static String valueSeparator = "\"";


    public Storage(Path file, BlockingQueue<Result> results) {
        this.storage = file;
        this.results = results;
    }

    @Override
    public void run() {
        setName("Storage-"+getName());
        System.out.println("started "+getName());
        /*init file*/
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        StringBuilder idBuilder = new StringBuilder();
        idBuilder.append(calendar.get(Calendar.MONTH)+1).append(".");
        idBuilder.append(calendar.get(Calendar.DAY_OF_MONTH)).append(".");
        idBuilder.append(calendar.get(Calendar.YEAR)).append("_");
        idBuilder.append(calendar.get(Calendar.HOUR_OF_DAY)).append(".");
        idBuilder.append(calendar.get(Calendar.MINUTE)).append(".");
        idBuilder.append(calendar.get(Calendar.SECOND)).append(".");
        idBuilder.append(calendar.get(Calendar.MILLISECOND)).append(".");
        String fileId = idBuilder.toString();
        if (Files.exists(this.storage)) {
            if (Files.isDirectory(this.storage)) {
                storage = this.storage.resolve(fileId + this.storage.getFileName());
            } else {
              storage = Paths.get(fileId + this.storage.getFileName());
            }
        }


        try (CSVWriter csv = new CSVWriter(new FileWriter(storage.toAbsolutePath().toFile(), true));) {
            System.out.println("Storage writes result into " + this.storage.toAbsolutePath());
            final LinkedList<String> line = new LinkedList();
            while (!isInterrupted()) {
                Result current = results.take();
                System.out.println(getName()+": taked result "+current.developerId);
                for (Result.Repository repository : current.get(10)) {
                    line.add(current.developerName);
                    line.add(current.developerEmail);
                    line.add(current.joinDate);
                    line.add(current.webPage);
                    line.add(current.developerId);
                    line.add(current.country);
                    line.add(String.valueOf(current.contributionsLastYear));
                    line.add(String.valueOf(current.contributionsOverAll));
                    line.add(String.valueOf(current.contributionsLastMonth));
                    line.add(String.valueOf(current.pullsLastMonth));
                    line.add(String.valueOf(current.longestStreak));
                    line.add(String.valueOf(current.currentStreak));
                    line.add(String.valueOf(current.contributedTo));
                    line.add(String.valueOf(current.followers));
                    line.add(String.valueOf(current.stars));
                    line.add(String.valueOf(current.following));
                    line.add(repository.name);
                    line.add(repository.description);
                    line.add(repository.startDate);
                    line.add(String.valueOf(repository.contributors));
                    line.add(String.valueOf(repository.commitsOverAll));
                    line.add(String.valueOf(repository.commitsLastMonth));
                    line.add(String.valueOf(repository.branches));
                    line.add(String.valueOf(repository.releases));
                    line.add(String.valueOf(repository.developerCommitsLastMonth));
                    line.add(String.valueOf(repository.developerCommitsOverAll));
                    csv.writeNext(line.toArray(new String[line.size()]), true);
                    line.clear();
                }
                csv.flush();
                sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            logger.error("processing storage", e);
            this.interrupt();
            //exception handling left as an exercise for the reader
        }
    }
}
