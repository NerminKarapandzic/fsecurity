package fsecurity;

import fsecurity.exception.AuthenticationException;
import fsecurity.exception.AuthorizationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthenticationAspect {

  private static final Logger logger = LoggerFactory.getLogger(AuthenticationAspect.class);

  private final ApplicationContext context;
  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private AuthenticationHandler authenticationHandler;

  public AuthenticationAspect(ApplicationContext applicationContext,
      HttpServletRequest request, HttpServletResponse response) {
    this.context = applicationContext;
    this.request = request;
    this.response = response;
    System.out.println("AuthenticationAspect created");
  }

  @Around("@annotation(Authenticated)")
  public void executeMiddlewares(ProceedingJoinPoint joinPoint) throws Throwable {
    try {
      //User is authenticated, check if there are permissions required
      MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
      var authenticationAnnotation = methodSignature.getMethod().getAnnotation(Authenticated.class);
      authenticationHandler = context.getBean(authenticationAnnotation.handler());

      var identity = authenticationHandler.authenticate(request);

      if (identity == null) {
        throw new AuthenticationException("Not authenticated");
      }

      var requiredRole = authenticationAnnotation.hasRole();
      var requiredAny = authenticationAnnotation.hasAnyRole();
      var requiredAll = authenticationAnnotation.hasAllRoles();

      boolean authorizationDecision = identity.hasRole(requiredRole)
          && identity.hasAnyRole(requiredAny)
          && identity.hasAllRoles(requiredAll);

      if (!authorizationDecision) {
        throw new AuthorizationException("Not authorized");
      }

      //User is authenticated and authorized,inject the identity into controller method if specified
      Object[] args = joinPoint.getArgs();

      for (int i = 0; i < args.length; i++) {
        if (args[i] instanceof Identity) {
          args[i] = identity;
          break;
        }
      }
      joinPoint.proceed(args);

    } catch (AuthenticationException e) {
      //Use the registered authentication exception handler to handle the exception
      authenticationHandler.getAuthenticationExceptionHandler().handle(request, response, e);
    } catch (AuthorizationException e) {
      // Use the registered authorization exception handler to handle the exception
      authenticationHandler.getAuthorizationExceptionHandler().handle(request, response, e);
    } catch (NoUniqueBeanDefinitionException e){
      logger.error("Multiple AuthenticationHandler beans found, you must specify which one to use on the @Authenticated annotation");
    } catch (NoSuchBeanDefinitionException e){
      logger.error("No AuthenticationHandler bean found");
    }
  }
}
