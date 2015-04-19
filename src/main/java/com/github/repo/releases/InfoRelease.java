package com.github.repo.releases;

import com.jcabi.github.Github;

import javax.json.JsonObject;

/**
 * User: YamStranger
 * Date: 4/15/15
 * Time: 1:24 PM
 */
public class InfoRelease implements Release {
    private final Github github;
    private final JsonObject data;

    public InfoRelease(Github github, JsonObject json) {
        this.github = github;
        this.data = json;
    }

}
