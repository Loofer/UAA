package cn.telami.uaa.service.impl;

import cn.telami.uaa.mapper.Oauth2LoginMapper;
import cn.telami.uaa.model.Oauth2Login;
import cn.telami.uaa.service.Oauth2LoginService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class Oauth2LoginServiceImpl extends ServiceImpl<Oauth2LoginMapper, Oauth2Login>
    implements Oauth2LoginService {

}
