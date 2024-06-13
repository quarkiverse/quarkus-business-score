package io.quarkiverse.businessscore;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * The business score of the current application.
 * <p>
 * Quarkus provides a CDI bean with the {@link jakarta.inject.Singleton} scope that implements this interface.
 */
public interface BusinessScore {

    /**
     * Increase the business score.
     *
     * @param value
     * @param labels
     * @see Score
     */
    void score(int value, String... labels);

    /**
     * Reset the score, i.e. remove all score records.
     *
     * @see Score
     */
    void reset();

    /**
     *
     * @return the sum of all score records in the current time window
     * @see #getTimeWindow()
     * @see Score
     */
    long getCurrent();

    /**
     * Check the "zombie" status of the current application. An application is considered a zombie if the current score is below
     * the current zombie threshold in the current time window. Note that {@link ZombieStatus#isZombie} returns {@code false} if
     * the application did not run for at least a duration of the current time window.
     *
     * @return the zombie status
     * @see #getTimeWindow()
     * @see #getZombieThreshold()
     */
    ZombieStatus test();

    /**
     * The point at which the application is considered a zombie.
     *
     * @return the threshold
     */
    long getZombieThreshold();

    /**
     *
     * @param value
     */
    void setZombieThreshold(long value);

    /**
     * The amount of time a score record is kept alive.
     *
     * @return the time window
     */
    Duration getTimeWindow();

    /**
     *
     * @param value
     */
    void setTimeWindow(Duration value);

    /**
     *
     * @return the list of records in the current time window
     */
    List<Score> getCurrentRecords();

    /**
     * A score record.
     *
     * @see BusinessScore#score(int, String...)
     * @see Scored
     */
    record Score(int value, long timestamp, Set<String> labels) {
    }

    /**
     * A zombie status.
     */
    record ZombieStatus(boolean isZombie, long score, long threshold, Duration timeWindow) {
    }

}
