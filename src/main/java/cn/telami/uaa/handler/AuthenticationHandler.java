package cn.telami.uaa.handler;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationHandler implements AuthenticationSuccessHandler,
    LogoutSuccessHandler, AccessDeniedHandler,
    AuthenticationEntryPoint, AuthenticationFailureHandler {

  @Autowired
  ObjectMapper objectMapper;

  /**
   * Removes temporary authentication-related data which may have been stored in the
   * session during the authentication process.
   */
  private final void clearAuthenticationAttributes(HttpServletRequest request) {
    HttpSession session = request.getSession(false);

    if (session == null) {
      return;
    }

    session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
  }


  /**
   * login success handler.
   */
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException {
    String method = "onAuthenticationSuccess";
    log.debug("Enter {}", method);

    response.setStatus(HttpServletResponse.SC_OK);
    Message message = Message.successOf("Login successfully.");
    writeResponse(response, message, objectMapper);
    clearAuthenticationAttributes(request);
    log.debug("Exit {}", method);
  }

  /**
   * Logout success handler.
   */
  @Override
  public void onLogoutSuccess(HttpServletRequest request,
                              HttpServletResponse response,
                              Authentication authentication) throws IOException {
    String method = "onLogoutSuccess";
    log.debug("Enter {}", method);
    response.setStatus(HttpServletResponse.SC_OK);
    Message message = Message.successOf("Logout successfully.");
    writeResponse(response, message, objectMapper);
    log.debug("Exit {}", method);
  }


  /**
   * Access denied handler.
   */
  @Override
  public void handle(HttpServletRequest request,
                     HttpServletResponse response,
                     AccessDeniedException accessDeniedException) throws IOException {
    String method = "handle";
    log.debug("Enter {}", method);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    Message message = Message.failOf(accessDeniedException.getMessage());
    writeResponse(response, message, objectMapper);
    log.debug("Exit {}", method);
  }


  /**
   * AuthenticationEntryPoint.
   */
  @Override
  public void commence(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException authException) throws IOException {
    String method = "commence";
    log.debug("Enter {}", method);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    Message message = Message.failOf(authException.getMessage());
    writeResponse(response, message, objectMapper);
    log.debug("Exit {}", method);
  }


  /**
   * 返回信息.
   */
  static void writeResponse(HttpServletResponse response,
                            Object responseBody,
                            ObjectMapper objectMapper) throws IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json; charset=utf-8");
    String jsonMessage = objectMapper.writeValueAsString(responseBody);
    response.getWriter().append(jsonMessage);
    log.debug("writeResponse,responseBody={}", jsonMessage);
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
                                      HttpServletResponse response,
                                      AuthenticationException exception) throws IOException {
    String method = "onAuthenticationFailure";
    log.debug("Enter {}", method);
    response.setStatus(HttpServletResponse.SC_OK);
    Message message = Message.failOf(exception.getMessage());
    writeResponse(response, message, objectMapper);
    log.debug("Exit {}", method);
  }
}