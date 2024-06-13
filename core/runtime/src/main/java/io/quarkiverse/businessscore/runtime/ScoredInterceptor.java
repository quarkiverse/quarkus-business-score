package io.quarkiverse.businessscore.runtime;

import static jakarta.interceptor.Interceptor.Priority.PLATFORM_BEFORE;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkiverse.businessscore.Scored;
import io.quarkus.arc.ArcInvocationContext;

@Scored
@Priority(PLATFORM_BEFORE)
@Interceptor
public class ScoredInterceptor {

    @Inject
    BusinessScore businessScore;

    @AroundInvoke
    Object score(ArcInvocationContext context) throws Exception {
        Scored scored = context.findIterceptorBinding(Scored.class);
        try {
            Object ret = context.proceed();
            businessScore.score(scored.value(), scored.labels());
            return ret;
        } catch (Exception e) {
            if (scored.scoreOnException()) {
                businessScore.score(scored.value(), scored.labels());
            }
            throw e;
        }
    }
}
