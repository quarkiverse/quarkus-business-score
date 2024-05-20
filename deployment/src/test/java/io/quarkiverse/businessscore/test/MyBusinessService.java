package io.quarkiverse.businessscore.test;

import jakarta.inject.Singleton;

import io.quarkiverse.businessscore.Scored;

@Singleton
public class MyBusinessService {

    @Scored
    public void doSomething() {
    }
}
