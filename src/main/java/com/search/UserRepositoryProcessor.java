package com.search;

import com.github.AccessException;
import com.github.repo.RepositoryAccessBlocked;
import com.github.repo.UserRepository;
import com.github.repo.commits.Commit;
import com.github.repo.pulls.PullRequest;
import com.github.user.UserEntry;
import com.util.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * User: YamStranger
 * Date: 4/15/15
 * Time: 3:55 PM
 */
public class UserRepositoryProcessor {
    public static Logger logger = LoggerFactory.getLogger(UserRepositoryProcessor.class);
    private final Result result;
    private final UserEntry userEntry;
    private final UserRepository userRepository;

    public UserRepositoryProcessor(Result result, UserEntry userEntry, UserRepository userRepository) {
        this.result = result;
        this.userEntry = userEntry;
        this.userRepository = userRepository;
    }

    public void process() throws IOException {
        final Result.Repository repository = new Result.Repository();
        /*fill repository data*/
        fillRepository(userRepository, repository);
        int totalCommits = 0;
        Iterator<Commit> commits = userRepository.commits().iterator();
        process(commits, this.userEntry, repository);

        Iterator<PullRequest> pulls = userRepository.pulls().iterator();
        process(pulls, this.userEntry.login(), repository);


        repository.branches = count(userRepository.branches());
        repository.releases = count(userRepository.releases());
        System.out.println(userEntry.login() + ":repository=" + userRepository.name() + ", total commits:" + totalCommits);
        result.addRepository(repository);
    }

    private int count(final Iterable countable) {
        int i = 0;
        for (final Object element : countable) {
            i += 1;
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
        }
        return i;
    }

    private void process(final Iterator<Commit> commits,
                         final UserEntry userEntry,
                         final Result.Repository repository) {
        final Set<String> comtributors = new HashSet<>();
        boolean exists = true;
        do {
            try {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                exists = commits.hasNext();
                if (exists) {
                    final Commit commit = commits.next();

                    final Calendar commitTime = new Dates(commit.created()).calendar();
                    final Calendar yearBefore = Calendar.getInstance(commitTime.getTimeZone());
                    for (int i = 0; i < 12; ++i) {
                        yearBefore.add(Calendar.MONTH, -1);
                    }
                    final Calendar monthBefore = Calendar.getInstance(commitTime.getTimeZone());
                    monthBefore.add(Calendar.MONTH, -1);

                    if (commitTime.after(yearBefore)) {
                    /*this year*/
                        if (commit.email().equals(userEntry.email())) {
                            repository.developerCommitsLastYear += 1;
                        }
                    }

                    if (commitTime.after(monthBefore)) {
                    /*this month*/
                        if (commit.email().equals(userEntry.email())) {
                            repository.developerCommitsLastMonth += 1;
                        }
                        repository.commitsLastMonth += 1;
                    }

                    /*over all*/
                    repository.commitsOverAll += 1;
                    if (commit.email().equals(userEntry.email())) {
                        repository.developerCommitsOverAll += 1;
                    }
                    comtributors.add(commit.email());
                }
            } catch (RepositoryAccessBlocked repositoryAccessBlocked) {
                logger.error("skip commit ", repositoryAccessBlocked);
            } catch (IllegalStateException accessException) {
                logger.error("skip commit ", accessException);
            } catch (AccessException accessException) {
                logger.error("AccessException  ", accessException);
                exists = false;
            }
        } while (exists);
        repository.contributors = comtributors.size();
    }

    private void process(final Iterator<PullRequest> pulls,
                         final String user,
                         final Result.Repository repository) {
        boolean exists = true;
        do {
            try {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                exists = pulls.hasNext();
                if (exists) {
                    final PullRequest pull = pulls.next();
                    final Calendar commitTime = new Dates(pull.created()).calendar();
                    final Calendar monthBefore = Calendar.getInstance(commitTime.getTimeZone());
                    monthBefore.add(Calendar.MONTH, -1);

                    if (commitTime.after(monthBefore)) {
                    /*this month*/
                        if (pull.login().equals(user)) {
                            repository.developerPullsLastMonth += 1;
                        }
                    }
                }
            } catch (RepositoryAccessBlocked repositoryAccessBlocked) {
                logger.error("skip pull request ", repositoryAccessBlocked);
            } catch (IllegalStateException accessException) {
                logger.error("skip pull request ", accessException);
            } catch (AccessException accessException) {
                logger.error("AccessException  ", accessException);
                exists = false;
            }
        } while (exists);
    }

    private void fillRepository(final UserRepository userRepository, final Result.Repository repository) {
        repository.description = userRepository.description();
        repository.name = userRepository.name();
        repository.startDate = userRepository.created();
    }
}
