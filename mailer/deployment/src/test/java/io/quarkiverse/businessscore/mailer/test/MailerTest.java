package io.quarkiverse.businessscore.mailer.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.QuarkusUnitTest;
import io.vertx.ext.mail.MailMessage;

public class MailerTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot(root -> {
            })
            .overrideConfigKey("quarkus.mailer.mock", "true")
            .overrideConfigKey("quarkus.business-score.mailer.to", "quarkus-reactive@quarkus.io")
            .overrideConfigKey("quarkus.business-score.mailer.from", "zombie-detector@quarkus.io");

    @Inject
    MockMailbox mockMailbox;

    @Inject
    Event<BusinessScore.ZombieStatus> event;

    @Test
    public void testMailSent() {
        event.fire(new BusinessScore.ZombieStatus(true, 5, 10, Duration.ofHours(6)));
        assertEquals(1, mockMailbox.getMailMessagesSentTo("quarkus-reactive@quarkus.io").size());
        MailMessage message = mockMailbox.getMailMessagesSentTo("quarkus-reactive@quarkus.io").get(0);
        assertEquals("zombie-detector@quarkus.io", message.getFrom());
        assertTrue(message.getSubject().startsWith("[ZOMBIE DETECTED] quarkus-business-score-mailer-deployment:"));
        assertTrue(message.getText().contains("Hello Quarkus user, an application zombie was detected!"));
        assertTrue(message.getText()
                .contains("The business score [5] does not reach the zombie threshold [10] in the time window [PT6H]."));
        assertTrue(message.getHtml().contains("<p>Hello Quarkus user, an application zombie was detected!</p>"));
        assertTrue(message.getHtml().contains("The business score <strong>[5]</strong>"));
    }

}
