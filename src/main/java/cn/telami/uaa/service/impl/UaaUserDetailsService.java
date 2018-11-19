package cn.telami.uaa.service.impl;

import cn.telami.uaa.exception.PasswordNotSetException;
import cn.telami.uaa.mapper.UserMapper;
import cn.telami.uaa.model.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UaaUserDetailsService implements UserDetailsService {

  @Autowired
  private UserMapper userMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    String method = "loadUserByUsername";
    log.debug("Enter {}. username={}", method, username);
    User user = userMapper.selectOne(
        new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    if (Objects.isNull(user)) {
      log.debug("Exit {}. user {} does not exist", method, username);
      throw new UsernameNotFoundException(username);
    }
    String password = user.getPassword();
    if (StringUtils.isEmpty(password)) {
      throw new PasswordNotSetException("用户名或密码错误");
    }
    return user.buildUserDetails(password);
  }
}
