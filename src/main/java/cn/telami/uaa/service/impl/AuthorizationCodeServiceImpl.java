package cn.telami.uaa.service.impl;

import cn.telami.uaa.mapper.AuthorizationCodeMapper;
import cn.telami.uaa.model.AuthorizationCode;
import cn.telami.uaa.service.AuthorizationCodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationCodeServiceImpl
    extends ServiceImpl<AuthorizationCodeMapper, AuthorizationCode>
    implements AuthorizationCodeService {
}
