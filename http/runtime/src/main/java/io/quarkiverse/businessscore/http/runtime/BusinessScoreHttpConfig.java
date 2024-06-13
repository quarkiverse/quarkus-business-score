package io.quarkiverse.businessscore.http.runtime;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.business-score.http")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface BusinessScoreHttpConfig {

    /**
     * The root path for management endpoints.
     * <p>
     * By default, this value is resolved as a path relative to {@code quarkus.http.non-application-root-path}. If the
     * management interface is enabled, the value will be resolved as a path relative to
     * {@code quarkus.management.root-path}.
     */
    @WithDefault("business-score")
    String rootPath();

}
