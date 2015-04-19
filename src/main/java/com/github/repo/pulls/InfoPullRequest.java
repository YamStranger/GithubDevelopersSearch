package com.github.repo.pulls;

import com.jcabi.github.Github;
import com.util.JsonUtil;

import javax.json.JsonObject;

/**
 * User: YamStranger
 * Date: 4/14/15
 * Time: 10:10 PM
 */
public class InfoPullRequest implements PullRequest {
    private final Github github;
    private final JsonObject data;
    private String id;
    private String created;
    private String login;

    public InfoPullRequest(Github github, JsonObject json) {
        this.github = github;
        this.data = json;
    }

    @Override
    public String id() {
        if (this.id == null) {
            this.id = new JsonUtil(this.data.get("id")).string();
        }
        return this.id;
    }

    @Override
    public String created() {
        if (this.created == null) {
            this.created = new JsonUtil(this.data.get("created_at")).string();
        }
        return this.created;
    }

    @Override
    public String login() {
        if (this.login == null) {
            this.login = new JsonUtil(this.data.getJsonObject("user").get("login")).string();

        }
        return this.login;
    }
}
