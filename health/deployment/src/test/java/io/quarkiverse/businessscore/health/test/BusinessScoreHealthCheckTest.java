package io.quarkiverse.businessscore.health.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class BusinessScoreHealthCheckTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withApplicationRoot(root -> {
            })
            .overrideConfigKey("quarkus.business-score.zombie-threshold", "10")
            .overrideConfigKey("quarkus.business-score.time-window", "10h");

    @Test
    public void testHelloEndpoint() {
        given().when()
                .get("/q/health/well")
                .then().log().all()
                .body("status", is("UP"))
                .body("checks.name", hasItem("business-score"))
                .body("checks.data.score", hasItem(0))
                .body("checks.data.zombieThreshold", hasItem(10))
                .body("checks.data.timeWindow", hasItem("PT10H"));

    }
}
