package com.github.repo.branch;

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
 * Date: 4/15/15
 * Time: 12:55 PM
 */
public class Branches implements Iterable<Branch> {
    private static final Logger logger = LoggerFactory.getLogger(Branches.class);

    private final String url;
    private final Github github;

    public Branches(final Github github, final String url) {
        this.github = github;
        this.url = url;
    }

    @Override
    public Iterator<Branch> iterator() {
        return new RepositoryIterator(this.github, this.url);
    }

    private class RepositoryIterator implements Iterator<Branch> {
        private final String url;
        private final Github github;
        private int page = 0;
        private List<Branch> branches;
        private final LimitHolder limit;

        public RepositoryIterator(final Github github, final String url) {
            this.github = github;
            this.url = url.replaceAll("^https://api.github.com", "")
                    .replaceAll("\\{.+\\}+$", "");
            this.branches = new LinkedList<>();
            this.limit = new LimitHolder(github);
        }

        @Override
        public boolean hasNext() {
            int errors = 3;
            while (errors-- > 0) {
                JsonResponse response = null;
                try {
                    if (this.page <= 0 || this.branches.isEmpty()) {
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
                                    logger.error("instead of object passed " + entry.getValueType() + ":" + entry.toString());
                                } else if (!new JsonUtil(((JsonObject) entry).get("message")).string().isEmpty()) {
                                    logger.error("message instead of object" + entry);
                                } else {
                                    this.branches.add(new InfoBranch(this.github, (JsonObject) entry));
                                }

                            }
                        }
                    }
                    if (!this.branches.isEmpty()) {
                        return true;
                    }
                } catch (IOException e) {
                    throw new AccessException("commits iterator" + this.url, e);
                } catch (JsonException e) {
/*                    String message = "cant parse";
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
                    throw new AccessException("JsonException for " + this.url + " : "
                            + message, e);*/
                    logger.error("JsonException for " + getCurrentUrl(), e);
                } catch (IllegalStateException illegalStateException) {
                    throw new IllegalStateException(getCurrentUrl(), illegalStateException);
                }
            }
            return false;

        }

        @Override
        public Branch next() {
            final Iterator<Branch> iterator = this.branches.iterator();
            final Branch branch = iterator.next();
            iterator.remove();
            return branch;
        }

        public String getCurrentUrl() {
            return this.url + "?page=" + page;
        }
    }


}
