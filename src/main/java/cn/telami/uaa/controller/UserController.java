package cn.telami.uaa.controller;

import static cn.telami.uaa.exception.UaaExceptionEnum.USER_HAVE_ALREADY_REGISTERED;

import cn.telami.uaa.dto.Response;
import cn.telami.uaa.exception.UaaException;
import cn.telami.uaa.exception.UaaExceptionEnum;
import cn.telami.uaa.model.User;
import cn.telami.uaa.service.UserService;
import cn.telami.uaa.utils.ValidatorUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {

  private static final String PATH = "/v1/api/uaa/user/";

  private static final String REGISTER_PATH = PATH + "register";

  private static final String PROFILE_PATH = PATH + "profile";

  private static final String ME_PATH = PATH + "me";

  private static final String CHECK_BIND_PHONE_PATH = PATH + "/check/bind";


  @Autowired
  private UserService userService;

  @Autowired
  protected AuthenticationManager authenticationManager;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * register and then login successful.
   */
  @PostMapping(path = REGISTER_PATH)
  public Response createNewUser(@RequestParam String username,
                                @RequestParam String password,
                                HttpServletRequest request) {
    User user = userService.getOne(
        new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    if (!Objects.isNull(user)) {
      throw new UaaException(USER_HAVE_ALREADY_REGISTERED);
    }
    //save user
    userService.save(User.builder()
        .username(username)
        .password(passwordEncoder.encode(password))
        .authorities(User.ROLE_NORMAL)
        .enabled(true)
        .build()
    );
    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(username, password);
    // generate session if one doesn't exist
    request.getSession();

    token.setDetails(new WebAuthenticationDetails(request));
    Authentication authenticatedUser = authenticationManager.authenticate(token);

    SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

    return Response.ok();
  }

  /**
   * get user profile.
   */
  @GetMapping(path = PROFILE_PATH)
  public Response getProfile() {

    return Response.ok(userService.getProfile());
  }

  /**
   * get user profile with token.
   */
  @GetMapping(path = ME_PATH)
  public Response me() {

    return Response.ok(userService.getProfile());
  }

  /**
   * 检查用户是否需要绑定手机号.
   *
   * @param activeUser 当前用户.
   */
  @GetMapping(path = CHECK_BIND_PHONE_PATH)
  @PreAuthorize("isAuthenticated()")
  public Response checkBindPhone(@AuthenticationPrincipal UserDetails activeUser) {
    String method = "checkBindPhone";
    log.debug("Enter {},activeUser {}", method, activeUser);

    boolean bind = !ValidatorUtils.isMobile(activeUser.getUsername());

    if (bind) {
      log.debug("Exit {}. current user {} need bind phone {}", method, activeUser.getUsername());
      throw new UaaException(UaaExceptionEnum.NEED_BIND);
    } else {
      log.debug("Exit {}. current user {} don't bind phone {}", method, activeUser.getUsername());
      return Response.ok("current user don't need to bind phone");
    }
  }
}
