package cn.telami.uaa.controller;

import static cn.telami.uaa.exception.UaaExceptionEnum.USER_HAVE_ALREADY_REGISTERED;

import cn.telami.uaa.dto.Response;
import cn.telami.uaa.exception.UaaException;
import cn.telami.uaa.model.User;
import cn.telami.uaa.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private static final String PATH = "/v1/api/uaa/user/";

  private static final String REGISTER_PATH = PATH + "register";

  private static final String PROFILE_PATH = PATH + "profile";

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
}
