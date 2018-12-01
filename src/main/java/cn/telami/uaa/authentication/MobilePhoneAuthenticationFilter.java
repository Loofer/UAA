package cn.telami.uaa.authentication;

import cn.telami.uaa.exception.BadRequestParamsException;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class MobilePhoneAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  private static final String PHONE_KEY = "username";
  private static final String VALIDATE_CODE_KEY = "password";

  public MobilePhoneAuthenticationFilter() {
    super(new AntPathRequestMatcher("/login/phone", "GET"));
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    if (!request.getMethod().equals("GET")) {
      throw new AuthenticationServiceException(
          "Authentication method not supported: " + request.getMethod());
    }

    String phone = request.getParameter(PHONE_KEY);
    String captcha = request.getParameter(VALIDATE_CODE_KEY);

    if (phone == null) {
      throw new BadRequestParamsException("Failed to get the phone");
    }

    if (captcha == null) {
      throw new BadRequestParamsException("Failed to get the validate code");
    }

    MobilePhoneCode mobilePhoneCode = MobilePhoneCode.builder()
        .mobile(phone.trim())
        .captcha(captcha)
        .sessionId(request.getSession(true).getId())
        .build();

    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
        UUID.randomUUID().toString(), mobilePhoneCode);

    // Allow subclasses to set the "details" property
    authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

    return this.getAuthenticationManager().authenticate(authRequest);
  }
}
