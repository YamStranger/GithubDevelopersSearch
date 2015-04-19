package com.github.repo;

/**
 * User: YamStranger
 * Date: 4/15/15
 * Time: 11:27 AM
 */
public class RepositoryAccessBlocked extends RuntimeException {
    public RepositoryAccessBlocked(final String message) {
        super(message);
    }
}
