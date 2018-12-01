package cn.telami.uaa.config;

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
        .antMatchers("/login/phone", "/login", "/oauth/authorize",
            "/logout", "/**/profile", "/v1/api/uaa/test/**"
        )
        .and()
        .authorizeRequests()
        .antMatchers("/v1/api/uaa/test/normal").hasRole(User.ROLE_NORMAL)
        .antMatchers("/v1/api/uaa/test/admin").hasRole("ADMIN")
        .antMatchers("/login/phone").permitAll()
        .anyRequest().authenticated()
        .and().csrf().disable()
        .httpBasic().disable()
        .addFilterBefore(phoneAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
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
