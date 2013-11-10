package fi.foyt.fni.gamelibrary;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.gamelibrary.GameLibraryTagDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.PublicationTagDAO;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;

@Stateful
@Dependent
public class GameLibraryTagController {

	@Inject
	private GameLibraryTagDAO gameLibraryTagDAO;
	
	@Inject
	private PublicationTagDAO publicationTagDAO;
	
	/* Game Library Tags */

	public GameLibraryTag createTag(String text) {
		return gameLibraryTagDAO.create(text);
	}

	public GameLibraryTag findTagById(Long id) {
		return gameLibraryTagDAO.findById(id);
	}
	
	public GameLibraryTag findTagByText(String text) {
		return gameLibraryTagDAO.findByText(text);
	}
	
	public List<GameLibraryTag> listGameLibraryTags() {
		return gameLibraryTagDAO.listAll();
	}
	
	public List<GameLibraryTag> listActiveGameLibraryTags() {
		return publicationTagDAO.listGameLibraryTagsByPublicationPublished(Boolean.TRUE);
	}
	
	public List<GameLibraryTag> listPublicationGameLibraryTags(Publication publication) {
		List<GameLibraryTag> result = new ArrayList<GameLibraryTag>();
		
		List<PublicationTag> publicationTags = publicationTagDAO.listByPublication(publication);
		for (PublicationTag publicationTag : publicationTags) {
			result.add(publicationTag.getTag());
		}
		
		return result;
	}
	
	/* Publication Tags */

	public PublicationTag addPublicationTag(Publication publication, GameLibraryTag tag) {
		PublicationTag publicationTag = publicationTagDAO.findByPublicationAndTag(publication, tag);
		if (publicationTag == null) {
			publicationTag = publicationTagDAO.create(tag, publication);
		}
		
		return publicationTag;
	}

	public List<PublicationTag> listPublicationTags(Publication publication) {
		return publicationTagDAO.listByPublication(publication);
	}
	
	public void deletePublicationTag(PublicationTag publicationTag) {
		GameLibraryTag gameLibraryTag = publicationTag.getTag();
		
		publicationTagDAO.delete(publicationTag);
		
		Long publicationCount = publicationTagDAO.countPublicationsByTag(gameLibraryTag);
		if (publicationCount == 0) {
			gameLibraryTagDAO.delete(gameLibraryTag);
		}
	}
}
