package cn.telami.uaa.service.impl;

import static cn.telami.uaa.exception.UaaExceptionEnum.MESSAGE_SEND_FAIL;

import cn.telami.uaa.dto.Response;
import cn.telami.uaa.exception.CaptchaIncorrectException;
import cn.telami.uaa.exception.UaaException;
import cn.telami.uaa.feign.MessageCenterClient;
import cn.telami.uaa.service.CommonService;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {
  /**
   * expire time.
   */
  @Value("${message.expire}")
  private int expire;

  /**
   * prefix.
   */
  @Value("${message.prefix}")
  private String prefixSms;

  @Autowired
  private MessageCenterClient messageCenterClient;

  @Autowired
  RedisTemplate<String, String> redisTemplate;

  @Override
  public String sendPhoneCode(String phone) {
    String method = "sendPhoneCode";
    log.debug("Enter {},phone {}", method, phone);
    //调用sms服务，发送短信
    Response response = messageCenterClient.sendShortMessage(phone);
    log.debug("back message mesResult={}", response);
    if (response == null || !(response.getStatus() == 200)) {
      log.error("fail send message to phone {}.", phone);
      throw new UaaException(MESSAGE_SEND_FAIL);
    }
    //保存在redis中
    ValueOperations<String, String> stringOperations = redisTemplate.opsForValue();
    log.debug("message send to phone {} success, save key {} code {} into redis",
        phone, prefixSms + phone, response
    );
    String code = (String) response.getData();
    stringOperations.set(prefixSms + phone, code, expire, TimeUnit.SECONDS);
    log.debug("Exit {},phone {},get code {}",
        method, phone, stringOperations.get(prefixSms + phone)
    );
    return code;
  }

  @Override
  public void validatePhoneCode(String phone, String code) {
    String method = "validatePhoneCode";
    log.debug("Enter {},phone {},code {}", method, phone, code);
    ValueOperations<String, String> stringOperations = redisTemplate.opsForValue();
    String keepingCode = stringOperations.get(prefixSms + phone);

    if (StringUtils.isEmpty(keepingCode)) {
      log.debug("send to phone {} Verification code {} expired.key {}",
          phone, code, prefixSms + phone
      );
      throw new CaptchaIncorrectException("请输入正确的验证码");
    }
    log.debug("find phone {} keeping key {} code {}.", phone, prefixSms + phone, code);

    if (!keepingCode.equalsIgnoreCase(code)) {
      log.debug("Wrong Verification phone {} Code {} Input.", phone, code);
      throw new CaptchaIncorrectException("请输入正确的验证码");
    }

    redisTemplate.delete(prefixSms + phone);
    log.debug("remove phone {} keeping key {} code {}.", phone, prefixSms + phone, code);
    log.debug("Exit {}. Successfully validate phone {} code {}", method, phone, code);
  }
}
