package com.search;

import java.util.*;

/**
 * User: YamStranger
 * Date: 4/15/15
 * Time: 2:19 PM
 */
public class Result {
    public Result() {

    }

    public String developerName = "";
    public String developerEmail = "";
    public String joinDate = "";
    public String webPage = "";
    public String developerId = "";
    public String country = "";
    public int contributedTo = 0;
    public int followers = 0;
    public int stars = 0;
    public int following = 0;
    public int pullsLastMonth = 0;
    public int contributionsLastYear = 0;
    public int contributionsOverAll = 0;
    public int contributionsLastMonth = 0;
    public int longestStreak = 0;
    public int currentStreak = 0;
    private TreeSet<Repository> repositories = new TreeSet<Repository>();

    public void addRepository(Repository repository) {
        this.repositories.add(repository);
        this.pullsLastMonth += repository.developerPullsLastMonth;
        this.contributionsLastYear += repository.developerCommitsLastYear;
        this.contributionsOverAll += repository.developerCommitsOverAll;
        this.contributionsLastMonth += repository.developerCommitsLastMonth;
    }

    public List<Repository> get(int first) {
        List<Repository> reps = new ArrayList<>(first);
        Iterator<Repository> repositoryIterator = repositories.iterator();
        while (--first >= 0 && repositoryIterator.hasNext()) {
            reps.add(repositoryIterator.next());
        }
        return reps;
    }

    public static class Repository implements Comparable {
        public int contributors = 0;
        public int commitsOverAll = 0;
        public int commitsLastMonth = 0;
        public int developerCommitsOverAll = 0;
        public int developerCommitsLastMonth = 0;
        public int developerCommitsLastYear = 0;
        public int developerPullsLastMonth = 0;
        public int branches = 0;
        public int releases = 0;
        public String name = "";
        public String description = "";
         String startDate = "";

        @Override
        public int compareTo(Object o) {
            int result = ((Repository) o).contributors - this.contributors;
            if (result == 0) {
                result = this.name.compareTo(((Repository) o).name);
            }
            return result;
        }
    }

}
