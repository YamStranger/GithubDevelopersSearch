package com.github.repo.releases;

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
 * Time: 1:25 PM
 */
public class Releases implements Iterable<Release> {
    private static final Logger logger = LoggerFactory.getLogger(Releases.class);
    private final String url;
    private final Github github;

    public Releases(final Github github, final String url) {
        this.github = github;
        this.url = url;
    }

    @Override
    public Iterator<Release> iterator() {
        return new ReleasesIterator(this.github, this.url);
    }

    private class ReleasesIterator implements Iterator<Release> {
        private final Github github;
        private final String url;
        private int page = 0;
        private List<Release> releases;
        private final LimitHolder limit;

        public ReleasesIterator(final Github github, final String url) {
            this.github = github;
            this.url = url.replaceAll("^https://api.github.com", "")
                    .replaceAll("\\{.+\\}+$", "");
            this.releases = new LinkedList<>();
            this.limit = new LimitHolder(github);
        }

        @Override
        public boolean hasNext() {
            int errors = 10;
            while (errors-- > 0) {
                JsonResponse response = null;
                try {
                    if (this.page <= 0 || this.releases.isEmpty()) {
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
                                    this.releases.add(new InfoRelease(this.github, (JsonObject) entry));
                                }

                            }
                        }
                    }
                    if (!this.releases.isEmpty()) {
                        return true;
                    }
                } catch (IOException e) {
                    throw new AccessException("commits iterator" + this.url, e);
                } catch (JsonException e) {
/*                String message = "cant parse";
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
        public Release next() {
            final Iterator<Release> iterator = this.releases.iterator();
            final Release release = iterator.next();
            iterator.remove();
            return release;
        }

        public String getCurrentUrl() {
            return this.url + "?page=" + page;
        }
    }
}
