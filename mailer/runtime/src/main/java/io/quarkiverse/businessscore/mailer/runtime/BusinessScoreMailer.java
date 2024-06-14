package io.quarkiverse.businessscore.mailer.runtime;

import java.time.LocalDateTime;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Vetoed;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkus.mailer.MailTemplate.MailTemplateInstance;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.Qute;
import io.quarkus.runtime.ApplicationConfig;

@Singleton
public class BusinessScoreMailer {

    @Vetoed
    @CheckedTemplate(defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
    public record BusinessScoreMessage(BusinessScore.ZombieStatus status, LocalDateTime timestamp)
            implements
                MailTemplateInstance {
    };

    @Inject
    BusinessScoreMailerConfig config;

    @Inject
    ApplicationConfig appConfig;

    @Inject
    Mailer mailer;

    void reportZombie(@Observes BusinessScore.ZombieStatus status) {
        if (status.isZombie()) {
            new BusinessScoreMessage(status, LocalDateTime.now())
                    .from(config.from())
                    .to(config.to())
                    .subject(Qute.fmt(config.subject())
                            .data("appName", appConfig.name.orElse("n/a"))
                            .data("appVersion", appConfig.version.orElse("n/a"))
                            .render())
                    .send().await().indefinitely();
        }
    }

}
