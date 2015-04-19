package com.github.repo;

import com.github.repo.branch.Branch;
import com.github.repo.branch.Branches;
import com.github.repo.commits.Commit;
import com.github.repo.commits.RepositoryCommits;
import com.github.repo.pulls.PullRequest;
import com.github.repo.pulls.PullRequests;
import com.github.repo.releases.Release;
import com.github.repo.releases.Releases;
import com.jcabi.github.Github;
import com.util.JsonUtil;

import javax.json.JsonObject;

/**
 * User: YamStranger
 * Date: 4/14/15
 * Time: 5:22 PM
 */
public class InfoRepository implements UserRepository {
    private final Github github;
    private final JsonObject data;

    public InfoRepository(final Github github, final JsonObject json) {
        this.data = json;
        this.github = github;
    }

    @Override
    public String forks() {
        return new JsonUtil(this.data.get("forks")).string();
    }


    @Override
    public String name() {
        return new JsonUtil(this.data.get("name")).string();
    }

    @Override
    public String description() {
        return new JsonUtil(this.data.get("description")).string();
    }

    @Override
    public String created() {
        return new JsonUtil(this.data.get("created_at")).string();
    }

    @Override
    public Iterable<PullRequest> pulls() {
        return new PullRequests(this.github, new JsonUtil(this.data.get("pulls_url")).string());
    }

    @Override
    public Iterable<Commit> commits() {
        return new RepositoryCommits(this.github, new JsonUtil(this.data.get("commits_url")).string());
    }

    @Override
    public Iterable<Branch> branches() {
        return new Branches(this.github, new JsonUtil(this.data.get("branches_url")).string());
    }

    @Override
    public Iterable<Release> releases() {
        return new Releases(this.github, new JsonUtil(this.data.get("releases_url")).string());
    }
}
