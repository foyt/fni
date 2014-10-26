package fi.foyt.fni.test.ui.base;

import org.junit.Test;

public class IllusionIndexTestsBase extends AbstractUITest {

  @Test
  public void testTitle() throws Exception {
    testTitle("/illusion", "Illusion");
  }

  @Test
  public void testNotCreateLinkNotLoggedIn() throws Exception {
    navigate("/illusion");
    assertSelectorNotPresent(".illusion-index-create-event-link");
  }
  
  @Test
  public void testNotCreateLinkLoggedIn() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    navigate("/illusion");
    assertSelectorPresent(".illusion-index-create-event-link");
  }
  
}
