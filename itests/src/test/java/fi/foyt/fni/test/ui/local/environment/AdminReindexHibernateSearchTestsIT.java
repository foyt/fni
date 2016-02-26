package fi.foyt.fni.test.ui.local.environment;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.environment.AdminReindexHibernateSearchTestsBase;

public class AdminReindexHibernateSearchTestsIT extends AdminReindexHibernateSearchTestsBase {
  
  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}
