package io.quarkiverse.businessscore.scheduler.runtime;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkiverse.businessscore.BusinessScore.ZombieStatus;
import io.quarkus.scheduler.Scheduled;

public class BusinessScoreScheduler {

    public final String IDENTITY = "business-score-test";

    private static final Logger LOG = Logger.getLogger(BusinessScoreScheduler.class);

    @Inject
    BusinessScore score;

    @Inject
    Event<ZombieStatus> event;

    @Scheduled(identity = IDENTITY, cron = "${quarkus.business-score.scheduler.cron:0 0 */6 * * *}")
    void test() {
        ZombieStatus status = score.test();
        if (status.isZombie()) {
            LOG.warnf(
                    "\nxxxxxxxxxxxxxxx\n"
                            + "Zombie detected\n"
                            + "xxxxxxxxxxxxxxx\n" +
                            "The business score [%s] does not reach the zombie threshold [%s] in the time window [%s]",
                    status.score(), status.threshold(), status.timeWindow());
            event.fire(status);
        }
    }

}
