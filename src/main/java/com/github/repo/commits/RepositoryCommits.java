package com.github.repo.commits;

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
import java.util.List;


/**
 * User: YamStranger
 * Date: 4/14/15
 * Time: 7:07 PM
 */
public class RepositoryCommits implements Iterable<Commit> {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryCommits.class);
    private final Github github;
    private final String url;

    /**
     * example
     * https://api.github.com/repos/mojombo/bert/commits?page=2
     *
     * @param github gitHub instance
     * @param url    url to repository commits from gitHub API
     */
    public RepositoryCommits(final Github github, String url) {
        this.github = github;
        this.url = url;
    }

    @Override
    public Iterator<Commit> iterator() {
        return new CommitsIterator(github, url);
    }

    private class CommitsIterator implements Iterator<Commit> {
        private final Github github;
        private final String url;
        private int page = 0;
        private List<Commit> commits;
        private final LimitHolder limit;

        public CommitsIterator(final Github github, final String url) {
            this.github = github;
            this.url = url.replaceAll("^https://api.github.com", "")
                    .replaceAll("\\{.+\\}+$", "");
            this.commits = new LinkedList<>();
            this.limit = new LimitHolder(github);
        }

        @Override
        public boolean hasNext() {
            JsonResponse response = null;
            int errors = 3;
            while (errors-- > 0) {
                try {
                    if (this.page <= 0 || this.commits.isEmpty()) {
                        this.page++;
                        //read current page
                        this.limit.check();
                        response = this.github.entry()
                                .uri().path(this.url)
                                .queryParam("page", this.page).back()
                                .fetch().as(JsonResponse.class);
                        final JsonArray entries = response.json().readArray();
                        if (entries.isEmpty()) { //no such page
                            return false;
                        } else {
                            for (final JsonValue entry : entries) {
                                if (Thread.currentThread().isInterrupted()) {
                                    break;
                                }
                                if (!JsonValue.ValueType.OBJECT.equals(entry.getValueType())) {
                                    logger.error("for"+getCurrentUrl()+"instead of object passed " + entry.getValueType() + ":" + entry.toString());
                                } else if (!new JsonUtil(((JsonObject) entry).get("message")).string().isEmpty()) {
                                    logger.error("for"+getCurrentUrl()+"message instead of object" + entry);
                                } else {
                                    this.commits.add(new InfoCommit(this.github, (JsonObject) entry));
                                }
                            }
                        }
                    }
                    if (!this.commits.isEmpty()) {
                        return true;
                    }
                } catch (IOException e) {
                    throw new AccessException("commits iterator" + this.url, e);
                } catch (JsonException e) {
/*
                    String message = "cant parse";
                    if (response != null) {
                        try {
                            JsonObject jsonObject = response.json().readObject();
                            message = jsonObject.toString();
                            if (String.valueOf(jsonObject.get("message"))
                                    .equals("Repository access blocked")) {
                                throw new RepositoryAccessBlocked(this.url + ":" + message);
                            }
                        } catch (JsonException messageException) {
                        }
                    }
*/
                    logger.error("JsonException for " + getCurrentUrl(), e);

                } catch (IllegalStateException illegalStateException) {
                    throw new IllegalStateException(getCurrentUrl(), illegalStateException);
                }
            }
            return false;
        }

        @Override
        public Commit next() {
            final Iterator<Commit> iterator = this.commits.iterator();
            final Commit commit = iterator.next();
            iterator.remove();
            return commit;
        }

        public String getCurrentUrl() {
            return this.url + "?page=" + page;
        }
    }


}
