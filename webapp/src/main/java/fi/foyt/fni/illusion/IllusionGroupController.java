package fi.foyt.fni.illusion;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.illusion.IllusionGroupDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;

@Dependent
@Stateless
public class IllusionGroupController {

  @Inject
  private IllusionGroupDAO illusionGroupDAO;

  public IllusionGroup createIllusionGroup(String urlName, String name, String xmppRoom) {
    return illusionGroupDAO.create(urlName, name, xmppRoom);
  }

  public IllusionGroup findIllusionSpaceById(Long id) {
    return illusionGroupDAO.findById(id);
  }

  public IllusionGroup findIllusionGroupByUrlName(String urlName) {
    return illusionGroupDAO.findByUrlName(urlName);
  }

}
