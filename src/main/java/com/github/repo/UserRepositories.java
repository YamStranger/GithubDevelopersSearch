package com.github.repo;

import com.github.AccessException;
import com.jcabi.github.Github;
import com.jcabi.http.response.JsonResponse;
import com.search.LimitHolder;
import com.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * User: YamStranger
 * Date: 4/14/15
 * Time: 5:24 PM
 */
public class UserRepositories implements Iterable<UserRepository> {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositories.class);

    private final Github github;
    private final String url;

    public UserRepositories(final Github github, final String url) {
        this.github = github;
        this.url = url;
    }

    @Override
    public Iterator<UserRepository> iterator() {
        return new UserRepositories.RepositoryIterator(this.github, this.url);
    }

    private class RepositoryIterator implements Iterator<UserRepository> {
        private final Github github;
        private final String url;
        private int page = 0;
        private LinkedList<UserRepository> repositories;
        private UserRepository repository = null;
        final LimitHolder limit;

        public RepositoryIterator(Github github, String url) {
            this.github = github;
            this.url = url.replaceAll("^https://api.github.com", "")
                    .replaceAll("\\{.+\\}+$", "");
            this.repositories = new LinkedList<>();
            limit = new LimitHolder(this.github);
        }

        @Override
        public boolean hasNext() {
            int errors = 3;
            while (errors-- > 0) {
                try {
                    if (page <= 0 || this.repositories.isEmpty()) {
                        page++;
                        this.limit.check();
                        final JsonResponse jsonResponse = github.entry()
                                .uri().path(url)
                                .queryParam("page", page).back()
                                .fetch().as(JsonResponse.class);
                        System.out.println(jsonResponse.json().read().toString());
                        final JsonArray entries = jsonResponse.json().readArray();
                        if (entries.isEmpty()) { //no such page
                            return false;
                        }
                        for (final JsonValue entry : entries) {
                            if (Thread.currentThread().isInterrupted()) {
                                break;
                            }
                            if (!JsonValue.ValueType.OBJECT.equals(entry.getValueType())) {
                                logger.error("for"+getCurrentUrl()+"instead of object passed " + entry.getValueType() + ":" + entry.toString());
                            } else if (!new JsonUtil(((JsonObject) entry).get("message")).string().isEmpty()) {
                                logger.error("for"+getCurrentUrl()+"message instead of object" + entry);
                            } else {
                                this.repositories.add(new InfoRepository(github, (JsonObject) entry));
                            }
                        }
                    }
                    if(!this.repositories.isEmpty()){
                        return true;
                    }
                } catch (IOException e) {
                    throw new AccessException("exceptio during reading repository " + this.url, e);
                } catch (IllegalStateException illegalStateException) {
                    throw new IllegalStateException(getCurrentUrl(), illegalStateException);
                } catch (JsonException jsonException) {
                    logger.error("JsonException for " + getCurrentUrl(), jsonException);
                }
            }
            return false;
        }

        @Override
        public UserRepository next() {
            Iterator<UserRepository> iter=repositories.iterator();
            UserRepository repository = iter.next();
            iter.remove();
            return repository;
        }

        public String getCurrentUrl() {
            return this.url + "?page=" + page;
        }
    }
}
