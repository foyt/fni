package fi.foyt.fni.test.ui.base.forgepublic;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet(id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql"),
})
public class ForgePublicIndexTestsBase extends AbstractUITest {

  @Test
  public void testTitle() {
    testTitle("/forge/public", "Forge Public");
  }
  
}
