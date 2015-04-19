package com.github.user;

import com.github.AccessException;
import com.jcabi.github.Github;
import com.jcabi.http.response.JsonResponse;
import com.search.LimitHolder;

import javax.json.JsonReader;
import java.io.IOException;

/**
 * User: YamStranger
 * Date: 4/15/15
 * Time: 9:38 PM
 */
public class UserReader {
    private final Github github;
    private final String url;
    private final LimitHolder limit;

    public UserReader(Github github, String url) {
        this.github = github;
        this.url = url.replaceAll("^https://api.github.com", "")
                .replaceAll("\\{.+\\}+$", "");
        this.limit = new LimitHolder(github);
    }

    public UserEntry load() {
        try {
            this.limit.check();
            final JsonResponse user = this.github.entry()
                    .uri().path(this.url)
                    .back()
                    .fetch().as(JsonResponse.class);
            final JsonReader userReader = user.json();
            return new InfoUser(github, userReader.readObject());
        } catch (final IOException e) {
            throw new AccessException("hasNext exception", e);
        }
    }

}
