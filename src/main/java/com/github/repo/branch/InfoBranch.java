package com.github.repo.branch;

import com.jcabi.github.Github;
import com.util.JsonUtil;

import javax.json.JsonObject;

/**
 * User: YamStranger
 * Date: 4/15/15
 * Time: 12:55 PM
 */
public class InfoBranch implements Branch {
    private final Github github;
    private final JsonObject data;

    public InfoBranch(Github github, JsonObject json) {
        this.github = github;
        this.data = json;
    }

    @Override
    public String name() {
        return new JsonUtil(this.data.get("name")).string();
    }
}
