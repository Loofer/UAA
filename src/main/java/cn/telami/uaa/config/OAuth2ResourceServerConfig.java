package cn.telami.uaa.config;

import cn.telami.uaa.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;

@Configuration
@EnableResourceServer
@Slf4j
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

  @Value("${resource.id}")
  private String resourceId;

  @Autowired
  private OAuth2WebSecurityExpressionHandler expressionHandler;

  @Override
  @Order(2)
  public void configure(HttpSecurity http) throws Exception {
    String method = "configure";
    log.debug("Enter{}", method);
    log.debug("Configure HttpSecurity for Resource Server");
    http
        .authorizeRequests()
        .antMatchers("/v1/api/uaa/user/me").hasRole(User.ROLE_NORMAL)
        .and().csrf().disable();
    log.debug("Exit {}", method);
  }

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    String method = "configure";
    log.debug("Enter {}", method);
    log.debug("Configure ResourceServerSecurityConfigurer resourceId={}", resourceId);
    resources.resourceId(resourceId);
    resources.expressionHandler(expressionHandler);
    log.debug("Exit {}", method);
  }
}
