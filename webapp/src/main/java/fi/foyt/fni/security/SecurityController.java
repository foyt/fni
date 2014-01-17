package fi.foyt.fni.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.users.Permission;

@Dependent
@Stateless
public class SecurityController {
  
  @Inject
  @Any
  private Instance<PermissionCheckImplementation<?>> permissionChecks;

  public List<PermissionCheckImplementation<Object>> getSecurityChecksByPermission(final Permission permission) {
    List<PermissionCheckImplementation<Object>> result = new ArrayList<>();
    
    @SuppressWarnings("serial")
    PermissionCheck permissionCheckAnnotation = new PermissionCheckQualifier() { 
      @Override
      public Permission value() {
        return permission;
      }
    };
     
    Iterator<PermissionCheckImplementation<?>> permissionCheckIterator = permissionChecks.select(permissionCheckAnnotation).iterator();
    while (permissionCheckIterator.hasNext()) {
      @SuppressWarnings("unchecked")
      PermissionCheckImplementation<Object> permissionCheck = (PermissionCheckImplementation<Object>) permissionCheckIterator.next();
      result.add(permissionCheck);
    }
    
    return result;
  }

  public boolean checkPermission(Permission permission, Object contextParameter) {
    return checkPermission(permission, contextParameter, new HashMap<String, Object>());
  }
  
  public boolean checkPermission(Permission permission, Object contextParameter, Map<String, Object> parameters) {
    List<PermissionCheckImplementation<Object>> permissionChecks = getSecurityChecksByPermission(permission);
    for (PermissionCheckImplementation<Object> permissionCheck : permissionChecks) {
      if (!permissionCheck.checkPermission(contextParameter, parameters)) {
        return false;
      }
    }
    
    return true;
  }
  
  @SuppressWarnings ("all")
  private abstract class PermissionCheckQualifier extends AnnotationLiteral<PermissionCheck> implements PermissionCheck {
  };
  
}
