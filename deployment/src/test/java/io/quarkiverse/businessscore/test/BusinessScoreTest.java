package io.quarkiverse.businessscore.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkus.test.QuarkusUnitTest;

public class BusinessScoreTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withApplicationRoot(root -> root.addClass(MyBusinessService.class))
            .overrideConfigKey("quarkus.business-score.zombie-threshold", "10")
            .overrideConfigKey("quarkus.business-score.time-window", "10h");

    @Inject
    MyBusinessService service;

    @Inject
    BusinessScore businessScore;

    @Test
    public void testScore() {
        assertThrows(IllegalArgumentException.class, () -> businessScore.setZombieThreshold(-10));
        assertThrows(NullPointerException.class, () -> businessScore.setTimeWindow(null));

        // Test scoring with interceptor and API
        assertEquals(0, businessScore.getCurrent());
        assertFalse(businessScore.test().isZombie());
        service.doSomething();
        assertEquals(1, businessScore.getCurrent());
        businessScore.score(2, "foo");
        assertEquals(3, businessScore.getCurrent());
        // Test zombie status - score is 3 and the threshold is 10 but the app did not run long enoug (10 hours)
        BusinessScore.ZombieStatus s = businessScore.test();
        assertFalse(s.isZombie());
        assertEquals(3, s.score());
        assertEquals(10l, s.threshold());

        businessScore.reset();
        // Score has been reset
        assertTrue(businessScore.getCurrentRecords().isEmpty());
        assertEquals(0, businessScore.getCurrent());

        businessScore.score(1, "foo");
        businessScore.score(2, "bar");
        // Test negative value
        assertThrows(IllegalArgumentException.class, () -> businessScore.score(-10));

        assertEquals(3, businessScore.getCurrent());
        // Test records
        List<BusinessScore.Score> records = businessScore.getCurrentRecords();
        assertEquals(2, records.size());
        assertEquals(1, records.get(0).value());
        assertTrue(records.get(0).labels().contains("foo"));
        assertEquals(2, records.get(1).value());
        assertTrue(records.get(1).labels().contains("bar"));

        businessScore.reset();
        businessScore.setTimeWindow(Duration.ofMillis(1));
        assertTrue(businessScore.test().isZombie());
    }

}
