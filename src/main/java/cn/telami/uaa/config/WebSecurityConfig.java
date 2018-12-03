package cn.telami.uaa.config;

import cn.telami.uaa.authentication.AliPayAuthenticationFilter;
import cn.telami.uaa.authentication.AliPayAuthenticationProvider;
import cn.telami.uaa.authentication.DingTalkAuthenticationFilter;
import cn.telami.uaa.authentication.DingTalkAuthenticationProvider;
import cn.telami.uaa.authentication.MobilePhoneAuthenticationFilter;
import cn.telami.uaa.authentication.MobilePhoneAuthenticationProvider;
import cn.telami.uaa.handler.AuthenticationHandler;
import cn.telami.uaa.model.User;
import cn.telami.uaa.service.impl.UaaUserDetailsService;

import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
@Order(1)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private AuthenticationHandler authenticationHandler;

  @Autowired
  private UaaUserDetailsService userDetailsService;

  @Autowired
  private MobilePhoneAuthenticationProvider mobilePhoneAuthenticationProvider;

  @Autowired
  private DingTalkAuthenticationProvider dingTalkAuthenticationProvider;

  @Autowired
  private AliPayAuthenticationProvider aliPayAuthenticationProvider;

  @Bean
  MobilePhoneAuthenticationFilter phoneAuthenticationFilter() {
    MobilePhoneAuthenticationFilter phoneAuthenticationFilter =
        new MobilePhoneAuthenticationFilter();
    ProviderManager providerManager =
        new ProviderManager(Collections.singletonList(mobilePhoneAuthenticationProvider));
    phoneAuthenticationFilter.setAuthenticationManager(providerManager);
    phoneAuthenticationFilter.setAuthenticationSuccessHandler(authenticationHandler);
    phoneAuthenticationFilter.setAuthenticationFailureHandler(authenticationHandler);
    return phoneAuthenticationFilter;
  }

  @Bean
  DingTalkAuthenticationFilter dingTalkAuthenticationFilter() {
    DingTalkAuthenticationFilter dingTalkAuthenticationFilter = new DingTalkAuthenticationFilter();
    ProviderManager providerManager =
        new ProviderManager(Collections.singletonList(dingTalkAuthenticationProvider));
    dingTalkAuthenticationFilter.setAuthenticationManager(providerManager);
    dingTalkAuthenticationFilter.setAuthenticationSuccessHandler(authenticationHandler);
    dingTalkAuthenticationFilter.setAuthenticationFailureHandler(authenticationHandler);
    return dingTalkAuthenticationFilter;
  }

  @Bean
  AliPayAuthenticationFilter aliPayAuthenticationFilter() {
    AliPayAuthenticationFilter aliPayAuthenticationFilter = new AliPayAuthenticationFilter();
    ProviderManager providerManager =
        new ProviderManager(Collections.singletonList(aliPayAuthenticationProvider));
    aliPayAuthenticationFilter.setAuthenticationManager(providerManager);
    aliPayAuthenticationFilter.setAuthenticationSuccessHandler(authenticationHandler);
    aliPayAuthenticationFilter.setAuthenticationFailureHandler(authenticationHandler);
    return aliPayAuthenticationFilter;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    String method = "configure";
    log.debug("Enter {}", method);
    log.debug("Configure the HttpSecurity for Authentication Server");
    http
        .formLogin()
        .successHandler(authenticationHandler)
        .failureHandler(authenticationHandler)
        .loginPage("/login").permitAll()
        .and()
        .logout().logoutUrl("/logout").logoutSuccessHandler(authenticationHandler).permitAll()
        .and()
        .exceptionHandling()
        .accessDeniedHandler(authenticationHandler)
        .authenticationEntryPoint(authenticationHandler)
        .and()
        .requestMatchers()
        .antMatchers("/login/phone", "/login", "/oauth/authorize", "/dingtalk/callback",
            "/logout", "/**/profile", "/v1/api/uaa/user/check/bind","/alipay/callback"
        )
        .and()
        .authorizeRequests()
        .antMatchers("/**/client/**").hasRole("ADMIN")
        .antMatchers("/login/phone", "/dingtalk/callback","/alipay/callback").permitAll()
        .anyRequest().authenticated()
        .and().csrf().disable()
        .httpBasic().disable()
        .addFilterBefore(phoneAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(dingTalkAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(aliPayAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .cors();
    log.debug("Exit {}", method);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    String method = "configure";
    log.debug("Enter {}", method);
    log.debug("Configure AuthenticationManagerBuilder for the Authentication Server");
    //encrypt password
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    log.debug("Exit {}", method);
  }

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
