package cn.telami.uaa.config;

import cn.telami.oatuh2.RedisTokenStore;
import cn.telami.uaa.constant.SecurityConstants;
import cn.telami.uaa.service.impl.UaaUserDetailsService;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
@Slf4j
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

  @Autowired
  private DataSource dataSource;

  @Autowired
  private AuthorizationCodeServices authorizationCodeServices;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private RedisConnectionFactory redisConnectionFactory;

  @Autowired
  private UaaUserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    String method = "configure";
    log.debug("Enter {}. Config Authorization Server ClientDetailsServiceConfigurer", method);
    JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
    clientDetailsService.setSelectClientDetailsSql(SecurityConstants.DEFAULT_SELECT_STATEMENT);
    clientDetailsService.setFindClientDetailsSql(SecurityConstants.DEFAULT_FIND_STATEMENT);
    clientDetailsService.setPasswordEncoder(passwordEncoder);
    clients.withClientDetails(clientDetailsService);
    log.debug("Exit {}.", method);
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) {
    security
        .checkTokenAccess("permitAll()");
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
    String method = "configure";
    log.debug("Enter {}. Config Authorization AuthorizationServerEndpointsConfigurer", method);
    endpoints
        .tokenStore(redisTokenStore())
        .authenticationManager(authenticationManager)
        .userDetailsService(userDetailsService)
        .authorizationCodeServices(authorizationCodeServices)
        //使用refresh_token的方式获得新的access token后重新生成新的refresh token
        .reuseRefreshTokens(false)
        .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    log.debug("Exit {}.", method);
  }

  /**
   * redis存储.
   */
  @Bean
  public TokenStore redisTokenStore() {
    return new RedisTokenStore(redisConnectionFactory);
  }
}
