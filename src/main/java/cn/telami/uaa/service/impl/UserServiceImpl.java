package cn.telami.uaa.service.impl;

import cn.telami.uaa.dto.response.UserResponseDto;
import cn.telami.uaa.mapper.Oauth2LoginMapper;
import cn.telami.uaa.mapper.UserMapper;
import cn.telami.uaa.model.Oauth2Login;
import cn.telami.uaa.model.User;
import cn.telami.uaa.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private Oauth2LoginMapper oauth2LoginMapper;

  @Override
  public UserResponseDto getProfile() {
    final String method = "getProfile";
    final String username = SecurityContextHolder.getContext().getAuthentication().getName();
    log.debug("Enter {}, username={}", method, username);
    User fromDb = userMapper.selectOne(
        new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    if (Objects.isNull(fromDb)) {
      throw new UsernameNotFoundException("User does not exist");
    }
    List<Oauth2Login> oauth2Logins = oauth2LoginMapper.selectList(
        new LambdaQueryWrapper<Oauth2Login>().eq(Oauth2Login::getUserId, fromDb.getId()));
    UserResponseDto responseDto = UserResponseDto.builderProfile(fromDb);
    responseDto.setOauth2Logins(oauth2Logins);
    log.debug("Exits {}", method);
    return responseDto;
  }
}

