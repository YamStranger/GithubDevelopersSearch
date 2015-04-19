package com.github.repo;

import com.github.repo.branch.Branch;
import com.github.repo.commits.Commit;
import com.github.repo.pulls.PullRequest;
import com.github.repo.releases.Release;

/**
 * User: YamStranger
 * Date: 4/14/15
 * Time: 5:22 PM
 */
public interface UserRepository {
    String forks();

    String name();

    String description();

    String created();


    Iterable<PullRequest> pulls();

    Iterable<Commit> commits();

    Iterable<Branch> branches();

    Iterable<Release> releases();

}
