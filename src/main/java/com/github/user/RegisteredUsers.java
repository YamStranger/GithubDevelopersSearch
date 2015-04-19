package com.github.user;

import com.github.AccessException;
import com.jcabi.github.Github;
import com.jcabi.http.response.JsonResponse;
import com.search.LimitHolder;
import com.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * User: YamStranger
 * Date: 4/14/15
 * Time: 12:39 PM
 */
public class RegisteredUsers implements Iterable<UserEntry> {
    public static Logger logger = LoggerFactory.getLogger(RegisteredUsers.class);
    final Github github;
    String last;

    public RegisteredUsers(Github github) {
        this(github, "1");
    }

    public RegisteredUsers(Github github, String last) {
        this.github = github;
        this.last = last;
    }

    @Override
    public Iterator<UserEntry> iterator() {
        return new UserEntryIterator(github, last);
    }

    private class UserEntryIterator implements Iterator<UserEntry> {
        private final Github github;
        private String last;
        private UserEntry current;
        private LinkedList<UserEntry> users;
        final LimitHolder limit;

        UserEntryIterator(Github github, String last) {
            this.github = github;
            this.last = last == null ? "1" : last;
            this.limit = new LimitHolder(this.github);
            this.users = new LinkedList<>();
        }

        @Override
        public boolean hasNext() {
            int errors = 3;
            while (errors-- > 0) {
                try {
                    if (this.users.isEmpty()) {
                        this.limit.check();
                        final JsonResponse response = this.github.entry()
                                .uri().path("/users")
                                .queryParam("since", this.last)
                                .back()
                                .fetch().as(JsonResponse.class);
                        read(response, this.users);
                    }
                    if (!this.users.isEmpty()) {
                        return true;
                    }
                } catch (final IOException e) {
                    throw new AccessException("hasNext exception", e);
                } catch (JsonException jsonException) {
                    logger.error("error during parsing ", jsonException);
                }
            }
            return false;
        }

        @Override
        public UserEntry next() {
            Iterator<UserEntry> iter = users.iterator();
            UserEntry userEntry = iter.next();
            iter.remove();
            return userEntry;
        }

        private void read(final JsonResponse response, LinkedList<UserEntry> users) throws IOException {
            final JsonReader reader = response.json();
            JsonArray array = reader.readArray();
            for (final JsonValue jsonValue : array) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                this.limit.check();
                final JsonResponse user = this.github.entry()
                        .uri().path("/users/" + ((JsonObject) jsonValue).getString("login"))
                        .back()
                        .fetch().as(JsonResponse.class);
                final JsonReader userReader = user.json();
                final JsonObject jsonObject = userReader.readObject();
                if (!new JsonUtil(jsonObject.get("message")).string().isEmpty()) {
                    logger.error("message instead of object" + jsonObject);
                } else {
                    UserEntry userEntry = new InfoUser(github, jsonObject);
                    users.add(userEntry);
                    this.last = userEntry.id();
                }
            }
        }
    }
}
