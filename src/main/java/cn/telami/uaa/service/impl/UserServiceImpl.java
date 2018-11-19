package cn.telami.uaa.service.impl;

import static cn.telami.uaa.exception.UaaExceptionEnum.USER_DOES_NOT_EXIST;

import cn.telami.uaa.dto.response.UserResponseDto;
import cn.telami.uaa.exception.UaaException;
import cn.telami.uaa.mapper.UserMapper;
import cn.telami.uaa.model.User;
import cn.telami.uaa.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

  @Autowired
  private UserMapper userMapper;

  @Override
  public UserResponseDto getProfile() {
    final String method = "getProfile";
    final String username = SecurityContextHolder.getContext().getAuthentication().getName();
    log.debug("Enter {}, username={}", method, username);

    UserResponseDto user = UserResponseDto.builderProfile(
        userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username)));

    if (null == user) {
      throw new UaaException(USER_DOES_NOT_EXIST);
    }
    log.debug("Exits {}", method);
    return user;
  }
}

