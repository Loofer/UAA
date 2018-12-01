package cn.telami.uaa.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class ValidatorUtils {

  /**
   * 正则表达式：验证手机号.
   */
  private static final String REGEX_MOBILE = "^[1][0-9]{10}$";

  /**
   * 正则表达式：验证邮箱.
   */
  private static final String REGEX_EMAIL =
      "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

  /**
   * 校验手机号.
   *
   * @param mobile 手机号
   * @return 校验通过返回true，否则返回false
   */
  public static boolean isMobile(String mobile) {
    if (StringUtils.isEmpty(mobile)) {
      return false;
    }
    return Pattern.matches(REGEX_MOBILE, mobile);
  }

  /**
   * 校验邮箱.
   *
   * @param email 邮箱
   * @return 校验通过返回true，否则返回false
   */
  public static boolean isEmail(String email) {
    return Pattern.matches(REGEX_EMAIL, email);
  }
}
