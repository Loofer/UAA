package cn.telami.uaa.authentication;

import cn.telami.uaa.exception.AuthorizationCodeEmptyException;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class DingTalkAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  public DingTalkAuthenticationFilter() {
    super(new AntPathRequestMatcher("/dingtalk/callback", "GET"));
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    if (!request.getMethod().equals("GET")) {
      throw new AuthenticationServiceException(
          "Authentication method not supported: " + request.getMethod());
    }
    //授权码.
    String code = request.getParameter("code");
    if (StringUtils.isEmpty(code)) {
      throw new AuthorizationCodeEmptyException("DingTalk authentication code can not be empty!");
    }
    DingTalkCode dingTalkCode = DingTalkCode.builder()
        .authorizationCode(code)
        .sessionId(request.getSession(true).getId())
        .build();
    // Allow subclasses to set the "details" property
    UsernamePasswordAuthenticationToken authRequest =
        new UsernamePasswordAuthenticationToken(IdWorker.get32UUID(), dingTalkCode);
    setDetails(request, authRequest);

    return this.getAuthenticationManager().authenticate(authRequest);
  }

  /**
   * Provided so that subclasses may configure what is put into the authentication
   * request's details property.
   *
   * @param request     that an authentication request is being created for
   * @param authRequest the authentication request object that should have its details
   *                    set
   */
  private void setDetails(HttpServletRequest request,
                          UsernamePasswordAuthenticationToken authRequest) {
    authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
  }
}