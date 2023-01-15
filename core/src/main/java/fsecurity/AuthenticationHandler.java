package fsecurity;

import javax.servlet.http.HttpServletRequest;

public abstract class AuthenticationHandler {
  private SecurityExceptionHandler authenticationExceptionHandler = (request, response, e) -> {
    System.out.println("Authentication exception handler default");
    response.sendError(401);
  };
  private SecurityExceptionHandler authorizationExceptionHandler = (request, response, e) -> {
    System.out.println("Authorization exception handler default");
    response.sendError(403);
  };

  public abstract Identity authenticate(HttpServletRequest request);

  public AuthenticationHandler authenticationExceptionHandler(SecurityExceptionHandler handler) {
    authenticationExceptionHandler = handler;
    return this;
  }

  public AuthenticationHandler authorizationExceptionHandler(SecurityExceptionHandler handler) {
    authorizationExceptionHandler = handler;
    return this;
  }

  public SecurityExceptionHandler getAuthenticationExceptionHandler() {
    return authenticationExceptionHandler;
  }

  public SecurityExceptionHandler getAuthorizationExceptionHandler() {
    return authorizationExceptionHandler;
  }
}
