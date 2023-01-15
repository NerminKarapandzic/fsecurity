package fsecurity;

import java.util.List;

public interface Identity {
  Object getId();
  List<String> getRoles();

  default boolean hasRole(String requiredRole) {
    if (requiredRole == null || requiredRole.isEmpty()) {
      return true;
    }
    return getRoles().contains(requiredRole);
  }

  default boolean hasAnyRole(String... requiredRoles) {
    if (requiredRoles == null || requiredRoles.length == 0) {
      return true;
    }
    for (String requiredRole : requiredRoles) {
      if (hasRole(requiredRole)) {
        return true;
      }
    }
    return false;
  }

  default boolean hasAllRoles(String... requiredRoles) {
    if (requiredRoles == null || requiredRoles.length == 0) {
      return true;
    }
    for (String requiredRole : requiredRoles) {
      if (!hasRole(requiredRole)) {
        return false;
      }
    }
    return true;
  }
}
