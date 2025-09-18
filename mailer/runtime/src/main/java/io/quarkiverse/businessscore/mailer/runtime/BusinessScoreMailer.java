package io.quarkiverse.businessscore.mailer.runtime;

import java.time.LocalDateTime;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Vetoed;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkus.mailer.MailTemplate.MailTemplateInstance;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.Qute;

@Singleton
public class BusinessScoreMailer {

    private static final Logger LOG = Logger.getLogger(BusinessScoreMailer.class);

    @Vetoed
    @CheckedTemplate(defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
    public record BusinessScoreMessage(BusinessScore.ZombieStatus status, LocalDateTime timestamp)
            implements
                MailTemplateInstance {
    };

    @Inject
    BusinessScoreMailerConfig config;

    @Inject
    Mailer mailer;

    void reportZombie(@Observes BusinessScore.ZombieStatus status) {
        if (status.isZombie()) {
            new BusinessScoreMessage(status, LocalDateTime.now())
                    .from(config.from())
                    .to(config.to())
                    .subject(Qute.fmt(config.subject())
                            .data("appName",
                                    ConfigProvider.getConfig().getOptionalValue("quarkus.application.name", String.class)
                                            .orElse("n/a"))
                            .data("appVersion",
                                    ConfigProvider.getConfig().getOptionalValue("quarkus.application.version", String.class)
                                            .orElse("n/a"))
                            .render())
                    .send().subscribe().with(i -> {
                        LOG.debugf("Sent business score notification to %s: %s", config.to(), status);
                    }, error -> {
                        LOG.errorf(error, "Unable to send business score notification to %s: %s", config.to(), status);
                    });
        }
    }

}
