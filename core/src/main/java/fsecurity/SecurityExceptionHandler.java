package fsecurity;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface SecurityExceptionHandler {
  void handle(HttpServletRequest req, HttpServletResponse res, Exception e) throws IOException;
}
