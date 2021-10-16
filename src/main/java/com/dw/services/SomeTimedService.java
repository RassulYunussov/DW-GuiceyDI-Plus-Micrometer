package com.dw.services;



import io.dropwizard.lifecycle.Managed;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

@Singleton
@Slf4j
public class SomeTimedService implements Managed {

    @Timed
    public String returnString() throws InterruptedException {
        Thread.sleep(1000);
        return "Some String";
    }

    @Timed
    public CompletableFuture<String> returnStringAsync() {

        return CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Some String from Async";
        });
    }

    @Override
    public void start() {
        log.info("SomeTimedService Started");
    }

    @Override
    public void stop() {
        log.info("SomeTimedService Stopped");
    }
}
