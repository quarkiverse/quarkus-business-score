package io.quarkiverse.businessscore.selfcheck.runtime;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkiverse.businessscore.BusinessScore.ZombieStatus;
import io.quarkus.runtime.Quarkus;
import io.quarkus.scheduler.Scheduled;

public class BusinessScoreSelfCheck {

    public static final String IDENTITY = "business-score-test";

    private static final String CRON = "${quarkus.business-score.self-check.cron:" + BusinessScoreSelfCheckConfig.DEFAULT_CRON
            + "}";

    private static final Logger LOG = Logger.getLogger(BusinessScoreSelfCheck.class);

    @Inject
    BusinessScore score;

    @Inject
    BusinessScoreSelfCheckConfig config;

    @Inject
    Event<ZombieStatus> event;

    @Scheduled(identity = IDENTITY, cron = CRON)
    void test() {
        ZombieStatus status = score.test();
        if (status.isZombie()) {
            LOG.warnf(
                    """

                            =================
                            | Zombie detected
                            =================
                            The business score [%s] did not reach the zombie threshold [%s] in the time window [%s]

                            """,
                    status.score(), status.threshold(), status.timeWindow());
            try {
                event.fire(status);
            } catch (Exception e) {
                LOG.errorf(e, "Unable to notify ZombieStatus observers: %s", status);
            }
            if (config.autoExit()) {
                LOG.warnf("""

                        =================================
                        | Automatic shutdown initiated...
                        =================================
                        """);
                Quarkus.asyncExit();
            }
        }
    }

}
