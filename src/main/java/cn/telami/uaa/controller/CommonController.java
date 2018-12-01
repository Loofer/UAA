package cn.telami.uaa.controller;

import cn.telami.uaa.dto.Response;
import cn.telami.uaa.exception.UaaException;
import cn.telami.uaa.service.CommonService;
import cn.telami.uaa.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CommonController {

  private static final String PATH = "/v1/api/uaa";

  private static final String SEND_MESSAGE_PATH = PATH + "/short-message/{mobile}";

  @Autowired
  private CommonService commonService;

  /**
   * 发送短信.
   */
  @GetMapping(path = SEND_MESSAGE_PATH)
  public Response sendShortMessage(@PathVariable String mobile) {
    String method = "sendPhoneMsg";
    log.debug("enter method {},phone number {}", method, mobile);
    if (!ValidatorUtils.isMobile(mobile)) {
      log.debug("phone number {} format incorrect.", mobile);
      throw UaaException.badRequest("phone number format incorrect");
    }
    String code = commonService.sendPhoneCode(mobile);
    return Response.ok(code);
  }
}
