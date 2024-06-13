package io.quarkiverse.businessscore.http.runtime;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkiverse.businessscore.runtime.BusinessScoreConfig;
import io.quarkus.runtime.ApplicationConfig;

@Singleton
public class BusinessScoreHttp {

    @Inject
    BusinessScore score;

    @Inject
    ApplicationConfig appConfig;

    @Inject
    BusinessScoreConfig businessScoreConfig;

}
