package cn.telami.uaa.service;

public interface CommonService {

  /**
   * 发送短信验证码.
   *
   * @param phone 手机号
   */
  String sendPhoneCode(String phone);

  /**
   * validate phone code.
   *
   * @param phone phone
   * @param code  code
   */
  void validatePhoneCode(String phone, String code);
}
