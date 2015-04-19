package com.github.user;

import com.github.repo.UserRepositories;
import com.github.repo.UserRepository;
import com.jcabi.github.Github;
import com.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;

/**
 * User: YamStranger
 * Date: 4/14/15
 * Time: 12:39 PM
 */
class InfoUser implements UserEntry {
    private final JsonObject data;
    private final Github github;
    private static final Logger logger = LoggerFactory.getLogger(InfoUser.class);
    private String id;
    private String repositoriesUrl;
    private String publicRepositories;
    private String name;
    private String email;
    private String login;

    public InfoUser(final Github github, final JsonObject json) {
        this.data = json;
        logger.debug("InfoUser ", json);
        this.github = github;
    }

    public String id() {
        if (this.id == null) {
            this.id = new JsonUtil(this.data.get("id")).string();
        }
        return this.id;
    }

    public String repositoriesUrl() {
        if (this.repositoriesUrl == null) {
            this.repositoriesUrl = new JsonUtil(this.data.get("repos_url")).string();
        }
        return this.repositoriesUrl;
    }

    @Override
    public Iterable<UserRepository> repositories() {
        return new UserRepositories(this.github, repositoriesUrl());
    }

    @Override
    public String publicRepositories() {
        if (this.publicRepositories == null) {
            this.publicRepositories = new JsonUtil(this.data.get("public_repos")).string();
        }
        return this.publicRepositories;
    }

    @Override
    public String name() {
        if (this.name == null) {
            this.name = new JsonUtil(this.data.get("name")).string();
        }
        return this.name;
    }

    @Override
    public String email() {
        if (this.email == null) {
            this.email = new JsonUtil(this.data.get("email")).string();
        }
        return this.email;
    }

    @Override
    public String created() {
        return new JsonUtil(this.data.get("created_at")).string();
    }

    @Override
    public String gitHubUrl() {
        return new JsonUtil(this.data.get("html_url")).string();
    }

    @Override
    public String apiUrl() {
        return new JsonUtil(this.data.get("url")).string();
    }

    @Override
    public String login() {
        if (this.login == null) {
            this.login = new JsonUtil(this.data.get("login")).string();
        }
        return this.login;
    }

    @Override
    public String location() {
        return new JsonUtil(this.data.get("location")).string();
    }

    @Override
    public String contributed() {
        return new JsonUtil(this.data.get("public_repos")).string();
    }

    @Override
    public String followers() {
        return new JsonUtil(this.data.get("followers")).string();
    }

    @Override
    public String following() {
        return new JsonUtil(this.data.get("following")).string();
    }
}
