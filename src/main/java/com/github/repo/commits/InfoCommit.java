package com.github.repo.commits;


import com.jcabi.github.Github;
import com.util.JsonUtil;

import javax.json.JsonObject;

/**
 * User: YamStranger
 * Date: 4/14/15
 * Time: 6:59 PM
 */
public class InfoCommit implements Commit {
    private final Github github;
    private final JsonObject data;

    public InfoCommit(Github github, JsonObject json) {
        this.github = github;
        this.data = json;
    }

    @Override
    public String email() {
        return new JsonUtil(this.data.getJsonObject("commit")
                .getJsonObject("author")
                .get("email")).string();
    }

    @Override
    public String created() {
        return new JsonUtil(this.data.getJsonObject("commit").getJsonObject("author").get("date")).string();
    }
}
