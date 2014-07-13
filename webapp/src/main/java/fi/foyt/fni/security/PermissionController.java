package fi.foyt.fni.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;

import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.Role;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;

@Dependent
@Stateless
public class PermissionController {
  
  @Inject
  private UserDAO userDAO;
  
  @PostConstruct
  public void init() {
    userRoleMap = new HashMap<>();
    userRoleMap.put(UserRole.ADMINISTRATOR, new Role[] { Role.USER, Role.ILLUSION_USER, Role.MATERIAL_USER, Role.FORUM_ADMIN, Role.GAME_LIBRARY_MANAGER, Role.SYSTEM_ADMINISTRATOR });
    userRoleMap.put(UserRole.LIBRARIAN, new Role[] { Role.USER, Role.ILLUSION_USER, Role.MATERIAL_USER, Role.FORUM_USER, Role.GAME_LIBRARY_MANAGER });
    userRoleMap.put(UserRole.USER, new Role[] { Role.USER, Role.ILLUSION_USER, Role.MATERIAL_USER, Role.FORUM_USER, Role.GAME_LIBRARY_USER });
    userRoleMap.put(UserRole.GUEST, new Role[] { Role.GUEST });
  }
  
  public List<Role> listUserRoles(User user) {
    return userRoleRoles(user.getRole());
  }

  public List<User> listUsersByPermission(Permission permission) {
    List<Role> roles = listRolesByPermission(permission);
    List<UserRole> userRoles = rolesUserRoles(roles);
    return userDAO.listByArchivedAndRoleIn(Boolean.FALSE, userRoles);
  }
  
  public List<Role> listRolesByPermission(Permission permission) {
    List<Role> result = new ArrayList<>();
    
    for (Role role : Role.values()) {
      if (ArrayUtils.contains(role.getPermissions(), permission)) {
        result.add(role); 
      }
    }
    
    return result;
  }
  
  public boolean hasUserPermission(User user, Permission permission) {
    List<Role> roles = listUserRoles(user);
    for (Role role : roles) {
      Permission[] rolePermissions = role.getPermissions();
      for (Permission rolePermission : rolePermissions) {
        if (rolePermission.equals(permission)) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  private Map<UserRole, Role[]> userRoleMap;
  
  private List<Role> userRoleRoles(UserRole role) {
    return Arrays.asList(userRoleMap.get(role));
  }
  
  private List<UserRole> rolesUserRoles(List<Role> roles) {
    List<UserRole> result = new ArrayList<>();
    
    for (UserRole userRole : userRoleMap.keySet()) {
      Role[] userRoleRoles = userRoleMap.get(userRole);
      for (Role role : roles) {
        if (ArrayUtils.contains(userRoleRoles, role)) {
          result.add(userRole);
        }
      }
    }
    
    return result;
  }
  
  
}
