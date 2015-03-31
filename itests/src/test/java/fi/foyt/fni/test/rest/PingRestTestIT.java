package fi.foyt.fni.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class PingRestTestIT extends AbstractRestTest {
  
  @Test
  public void testPing() {
    given()
      .contentType("application/json")
      .get("/system/ping")
      .then()
      .statusCode(200)
      .assertThat()
      .content(is("pong"));
  }
  
}
