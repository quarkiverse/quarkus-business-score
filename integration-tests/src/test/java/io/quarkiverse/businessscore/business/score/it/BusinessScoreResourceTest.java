package io.quarkiverse.businessscore.business.score.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class BusinessScoreResourceTest {

    @Test
    public void testHelloEndpoint() {
        given().when().post("/test")
                .then()
                .statusCode(204);

        given().when()
                .get("/q/business-score")
                .then().log().all()
                .body("zombie", is(false))
                .body("score", is(1))
                .body("initialZombieThreshold", is(10))
                .body("zombieThreshold", is(20))
                .body("initialTimeWindow", is("PT168H"))
                .body("timeWindow", is("PT72H"));

        given().when()
                .get("/q/business-score/records")
                .then().log().all()
                .body("records.size()", is(1));
    }
}