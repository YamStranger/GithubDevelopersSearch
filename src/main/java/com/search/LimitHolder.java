package com.search;

import com.jcabi.github.Github;
import com.jcabi.github.Limit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * User: YamStranger
 * Date: 4/15/15
 * Time: 5:29 PM
 */
public class LimitHolder {
    public static Logger logger = LoggerFactory.getLogger(LimitHolder.class);
    private Github github;
    private int total = 0;
    private int remaining = 0;

    public LimitHolder(final Github github) {
        this.github = github;
    }

    /**
     * wait untill limit restore
     */
    public void check() {
        try {
            if (this.remaining == 0) {
                Limit.Smart limit = new Limit.Smart(this.github.limits().get("core"));
                this.remaining = limit.remaining();
                this.total = limit.limit();
            }
            if (this.total/10+10 >= this.remaining) {
                Limit.Smart limit = new Limit.Smart(this.github.limits().get("core"));
                this.remaining = limit.remaining();
                this.total = limit.limit();
                if (limit.remaining() <= 10) {
                    System.out.println("limit reached");
                    while (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(10000);
                        limit = new Limit.Smart(this.github.limits().get("core"));
                        if (limit.remaining() >= 10) {
                            break;
                        }
                    }
                    System.out.println("remaining restored");
                }
            } else {
                this.remaining -= 1;
            }
        } catch (InterruptedException | IOException exception) {
            logger.error("during wait", exception);
            Thread.currentThread().interrupt();
        }
    }

}
