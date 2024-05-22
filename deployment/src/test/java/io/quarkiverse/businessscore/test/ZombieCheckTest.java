package io.quarkiverse.businessscore.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkus.test.QuarkusUnitTest;

public class ZombieCheckTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withApplicationRoot(root -> root.addClass(Notification.class))
            .overrideConfigKey("quarkus.business-score.time-window", "0")
            .overrideConfigKey("quarkus.business-score.zombie-check-interval", "1");

    @Inject
    BusinessScore businessScore;

    @Test
    public void testCheck() throws InterruptedException {
        assertTrue(Notification.ZOMBIE_LATCH.await(5, TimeUnit.SECONDS));
        assertTrue(businessScore.test().isZombie());
    }

    public static class Notification {

        static final CountDownLatch ZOMBIE_LATCH = new CountDownLatch(1);

        void onZombie(@Observes BusinessScore.ZombieStatus status) {
            if (status.isZombie()) {
                ZOMBIE_LATCH.countDown();
            }
        }

    }

}
