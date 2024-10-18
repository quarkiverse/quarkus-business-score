package io.quarkiverse.businessscore.scheduler.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.businessscore.selfcheck.runtime.BusinessScoreSelfCheck;
import io.quarkus.scheduler.Scheduler;
import io.quarkus.test.QuarkusUnitTest;

public class DefaultCronTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withEmptyApplication();

    @Inject
    Scheduler scheduler;

    @Test
    public void testCheck() {
        assertNotNull(scheduler.getScheduledJob(BusinessScoreSelfCheck.IDENTITY).getNextFireTime());
    }

}
