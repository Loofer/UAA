package cn.telami.uaa.service.impl;

import cn.telami.uaa.mapper.Oauth2ClientMapper;
import cn.telami.uaa.model.Oauth2Client;
import cn.telami.uaa.service.Oauth2ClientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Oauth2ClientServiceImpl extends ServiceImpl<Oauth2ClientMapper, Oauth2Client>
    implements Oauth2ClientService {

}
