package com.github.user;

import com.github.repo.UserRepository;

/**
 * User: YamStranger
 * Date: 4/14/15
 * Time: 12:37 PM
 */
public interface UserEntry {
    String id();

    String publicRepositories();

    String name();

    String email();

    String created();

    String gitHubUrl();
    String apiUrl();;
    String login();

    String location();

    String contributed();

    String followers();

    String following();

    Iterable<UserRepository> repositories();
}
