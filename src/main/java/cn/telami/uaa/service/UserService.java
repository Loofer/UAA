package cn.telami.uaa.service;

import cn.telami.uaa.dto.response.UserResponseDto;
import cn.telami.uaa.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
  UserResponseDto getProfile();
}
