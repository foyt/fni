package fi.foyt.fni.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import fi.foyt.fni.persistence.model.system.SystemSettingKey;

public class SystemRestTestsIT extends AbstractRestTest {
  
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
  
  @Test
  public void testSystemSettings() {
    SystemSettingKey[] publicKeys = {
      SystemSettingKey.PUBLISH_GUIDE_EN,
      SystemSettingKey.PUBLISH_GUIDE_FI
    };
    
    for (SystemSettingKey key : SystemSettingKey.values()) {
      given()
        .contentType("application/json")
        .get("/system/settings/{KEY}", key)
        .then()
        .statusCode(ArrayUtils.contains(publicKeys, key) ? 200 : 403);
    }
  }
  
}
