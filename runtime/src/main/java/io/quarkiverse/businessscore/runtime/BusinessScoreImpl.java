package io.quarkiverse.businessscore.runtime;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Event;
import jakarta.inject.Singleton;

import org.jboss.logging.Logger;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkus.runtime.ApplicationConfig;
import io.quarkus.runtime.Startup;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

@Startup
@Singleton
class BusinessScoreImpl implements BusinessScore {

    private static final Logger LOG = Logger.getLogger(BusinessScoreImpl.class);

    private final Lock lock = new ReentrantLock();
    private final List<Score> records;
    private final AtomicLong zombieThreshold;
    private final AtomicReference<Duration> timeWindow;
    private final long startTime;
    private final long timerId;
    private final Vertx vertx;
    final ApplicationConfig applicationConfig;
    final BusinessScoreConfig config;

    BusinessScoreImpl(BusinessScoreConfig config, ApplicationConfig applicationConfig, Vertx vertx,
            Event<ZombieStatus> event) {
        this.records = new ArrayList<>();
        this.zombieThreshold = new AtomicLong(config.zombieThreshold());
        this.timeWindow = new AtomicReference<>(config.timeWindow());
        this.applicationConfig = applicationConfig;
        this.config = config;
        this.startTime = System.currentTimeMillis();
        this.timerId = vertx.setPeriodic(config.zombieCheckInterval().toMillis(), new Handler<Long>() {

            @Override
            public void handle(Long timerId) {
                ZombieStatus status = test();
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
        });
        this.vertx = vertx;
    }

    @Override
    public void score(int value, String... labels) {
        if (value <= 0) {
            throw new IllegalArgumentException("Score value must be positive: " + value);
        }
        lock.lock();
        try {
            records.add(new Score(value, System.currentTimeMillis(), Set.of(labels)));
        } finally {
            lock.unlock();
        }
        LOG.debugf("Scored {} [labels: {}]", value, labels.length > 0 ? Arrays.toString(labels) : "[]");
    }

    @Override
    public void reset() {
        lock.lock();
        try {
            records.clear();
        } finally {
            lock.unlock();
        }
        LOG.debugf("The score has been reset");
    }

    @Override
    public long getCurrent() {
        int total = 0;
        long now = System.currentTimeMillis();
        long timeWindow = this.timeWindow.get().toMillis();
        lock.lock();
        try {
            // Iterate over the records in the reverse order
            ListIterator<Score> it = records.listIterator(records.size());
            while (it.hasPrevious()) {
                Score score = it.previous();
                if (isInWindow(score, now, timeWindow)) {
                    total += score.value();
                } else {
                    // Score record outside the given time window
                    // Records are ordered so it means that no other record can lie inside the current time window
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
        return total;
    }

    int compact() {
        long start = System.nanoTime();
        int removed = 0;
        long now = System.currentTimeMillis();
        long timeWindow = this.timeWindow.get().toMillis();
        lock.lock();
        try {
            for (Iterator<Score> it = records.iterator(); it.hasNext();) {
                if (!isInWindow(it.next(), now, timeWindow)) {
                    it.remove();
                    removed++;
                } else {
                    // Score record inside the given time window
                    // Records are ordered so it means that no other record can lie outside the current time window
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
        LOG.debugf("Done compacting in %s ms (removed %s records)", System.nanoTime() - start, removed);
        return removed;
    }

    @Override
    public long getZombieThreshold() {
        return zombieThreshold.get();
    }

    @Override
    public ZombieStatus test() {
        long now = System.currentTimeMillis();
        boolean didRunLongEnough = (now - startTime) >= timeWindow.get().toMillis();
        int total = 0;
        Duration timeWindow = this.timeWindow.get();
        long timeWindowMillis = timeWindow.toMillis();
        long threshold = zombieThreshold.get();
        lock.lock();
        try {
            // Iterate over the records in the reverse order
            ListIterator<Score> it = records.listIterator(records.size());
            while (it.hasPrevious()) {
                Score score = it.previous();
                if (isInWindow(score, now, timeWindowMillis)) {
                    total += score.value();
                } else {
                    // Score record outside the given time window
                    // Records are ordered so it means that no other record can increase the current score
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
        return new ZombieStatus(didRunLongEnough ? total < threshold : false, total, threshold, timeWindow);
    }

    private boolean isInWindow(Score score, long now, long timeWindow) {
        return (now - score.timestamp()) < timeWindow;
    }

    @Override
    public void setZombieThreshold(long value) {
        this.zombieThreshold.set(value);
    }

    @Override
    public Duration getTimeWindow() {
        return timeWindow.get();
    }

    @Override
    public void setTimeWindow(Duration value) {
        this.timeWindow.set(Objects.requireNonNull(value));
    }

    @Override
    public List<Score> getCurrentRecords() {
        List<Score> ret = new ArrayList<>(records.size());
        long now = System.currentTimeMillis();
        long timeWindow = this.timeWindow.get().toMillis();
        lock.lock();
        try {
            for (Score record : records) {
                if (isInWindow(record, now, timeWindow)) {
                    ret.add(record);
                }
            }
        } finally {
            lock.unlock();
        }
        return ret;
    }

    @PreDestroy
    void destroy() {
        vertx.cancelTimer(timerId);
    }

}
