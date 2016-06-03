package fi.foyt.fni.users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;

import fi.foyt.fni.auth.AuthenticationController;
import fi.foyt.fni.persistence.dao.auth.UserIdentifierDAO;
import fi.foyt.fni.persistence.dao.users.AddressDAO;
import fi.foyt.fni.persistence.dao.users.UserContactFieldDAO;
import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.dao.users.UserEmailDAO;
import fi.foyt.fni.persistence.dao.users.UserFriendDAO;
import fi.foyt.fni.persistence.dao.users.UserGroupDAO;
import fi.foyt.fni.persistence.dao.users.UserGroupMemberDAO;
import fi.foyt.fni.persistence.dao.users.UserImageDAO;
import fi.foyt.fni.persistence.dao.users.UserSettingDAO;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.auth.UserIdentifier;
import fi.foyt.fni.persistence.model.common.Country;
import fi.foyt.fni.persistence.model.users.Address;
import fi.foyt.fni.persistence.model.users.AddressType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserContactField;
import fi.foyt.fni.persistence.model.users.UserContactFieldType;
import fi.foyt.fni.persistence.model.users.UserEmail;
import fi.foyt.fni.persistence.model.users.UserFriend;
import fi.foyt.fni.persistence.model.users.UserGroup;
import fi.foyt.fni.persistence.model.users.UserImage;
import fi.foyt.fni.persistence.model.users.UserProfileImageSource;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.persistence.model.users.UserSetting;
import fi.foyt.fni.persistence.model.users.UserSettingKey;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.search.SearchResult;
import fi.foyt.fni.utils.search.SearchResultScoreComparator;

public class UserGroupController implements Serializable {
  
  @Inject
  private UserGroupDAO userGroupDAO;

  public UserGroup findUserGroupById(Long userGroupId) {
    return userGroupDAO.findById(userGroupId);
  }
	

}
