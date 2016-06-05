package fi.foyt.fni.rest.illusion;

public enum OAuthScopes {
  
  ILLUSION_LIST_GENRES,
  ILLUSION_LIST_TYPES,
  
  ILLUSION_LIST_EVENTS,
  ILLUSION_FIND_EVENT,
  ILLUSION_CREATE_EVENT,
  ILLUSION_UPDATE_EVENT,
  ILLUSION_DELETE_EVENT,
  
  ILLUSION_CREATE_EVENT_PARTICIPANT,
  ILLUSION_FIND_EVENT_PARTICIPANT,
  ILLUSION_UPDATE_EVENT_PARTICIPANT, 
  ILLUSION_DELETE_EVENT_PARTICIPANT,
  
  ILLUSION_GROUP_LIST, 
  ILLUSION_CREATE_GROUP,
  
  FORGE_COOPS_ACCESS_FILE,
  FORGE_COOPS_MODIFY_FILE, 

  USERS_USER_CREATE,
  USERS_USER_LIST, 
  USERS_USER_FIND,
  USERS_USER_FIND_ME, 
  
  SYSTEM_JPA_CACHE_FLUSH, 
  SYSTEM_REINDEX_SEARCH,  
  
  SYSTEM_SETTINGS_FIND, 
  
  ILLUSION_CREATE_FORUM_POST, 
  ILLUSION_FIND_FORUM_POST, 
  ILLUSION_LIST_FORUM_POSTS, 
  ILLUSION_UPDATE_FORUM_POST, 
  ILLUSION_DELETE_FORUM_POST, 
  
  ILLUSION_CREATE_MATERIAL_PARTICIPANT_SETTING, 
  ILLUSION_LIST_MATERIAL_PARTICIPANT_SETTING, 
  ILLUSION_FIND_MATERIAL_PARTICIPANT_SETTING, 
  ILLUSION_UPDATE_MATERIAL_PARTICIPANT_SETTING, 

  ILLUSION_FIND_CHARACTER_SHEET_DATA, 
  
  ILLUSION_FIND_REGISTRATION_FORM_DATA, 
  
  ILLUSION_DELETE_EVENT_PAGE, 
  
  FORUM_CREATE_WATCHER, 
  FORUM_LIST_WATCHERS,
  FORUM_DELETE_WATCHER, 
  
  MATERIAL_FIND_MATERIAL,
  MATERIAL_UPDATE_MATERIAL,
  
  MATERIAL_CREATE_SHARE,
  MATERIAL_LIST_SHARES,
  MATERIAL_FIND_SHARE,
  MATERIAL_UPDATE_SHARE,
  MATERIAL_DELETE_SHARE,
 
  MATERIAL_FIND_DOCUMENT, 
  
  MATERIAL_FIND_IMAGE,
  
  MATERIAL_LIST_BOOK_TEMPLATES,
  
  MATERIAL_LIST_TAGS

}
