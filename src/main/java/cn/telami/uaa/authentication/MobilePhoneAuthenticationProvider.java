package cn.telami.uaa.authentication;

import cn.telami.uaa.model.User;
import cn.telami.uaa.service.CommonService;
import cn.telami.uaa.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@Slf4j
public class MobilePhoneAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  @Autowired
  private CommonService commonService;

  @Autowired
  public UserService userService;

  @Override
  protected void additionalAuthenticationChecks(
      UserDetails userDetails,
      UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
  }

  @Override
  protected UserDetails retrieveUser(
      String username,
      UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    String method = "retrieveUser";
    log.debug("Enter {}. param={}", method, authentication);
    MobilePhoneCode mobilePhoneCode = (MobilePhoneCode) authentication.getCredentials();
    String mobile = mobilePhoneCode.getMobile();
    //验证短信校验码
    commonService.validatePhoneCode(mobile, mobilePhoneCode.getCaptcha());
    LambdaQueryWrapper<User> queryWrapper =
        new LambdaQueryWrapper<User>().eq(User::getMobile, mobile);
    User exist = userService.getOne(queryWrapper);
    User loadUser;
    if (ObjectUtils.isEmpty(exist)) {
      //新手机用户
      loadUser = User.builder()
          .username(mobile)
          .mobile(mobile)
          .enabled(true)
          .authorities(User.ROLE_NORMAL)
          .build();
      userService.save(loadUser);
    } else {
      loadUser = exist;
    }
    return loadUser.buildUserDetails();
  }
}
