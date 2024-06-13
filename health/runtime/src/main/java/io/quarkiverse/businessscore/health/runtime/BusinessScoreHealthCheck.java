package io.quarkiverse.businessscore.health.runtime;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import io.quarkiverse.businessscore.BusinessScore;
import io.smallrye.health.api.Wellness;

@Wellness
@Singleton
public class BusinessScoreHealthCheck implements HealthCheck {

    @Inject
    BusinessScore score;

    @Override
    public HealthCheckResponse call() {
        BusinessScore.ZombieStatus status = score.test();
        return HealthCheckResponse.named("business-score")
                .status(!status.isZombie())
                .withData("score", status.score())
                .withData("zombieThreshold", status.threshold())
                .withData("timeWindow", status.timeWindow().toString())
                .build();
    }

}
