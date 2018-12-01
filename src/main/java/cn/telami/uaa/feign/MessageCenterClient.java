package cn.telami.uaa.feign;

import cn.telami.uaa.dto.Response;
import cn.telami.uaa.feign.fallback.MessageCenterFallbackClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "message-center", fallback = MessageCenterFallbackClient.class)
public interface MessageCenterClient {

  String SEND_MESSAGE_PATH = "/v1/api/message/short-message/{mobile}";

  @GetMapping(path = SEND_MESSAGE_PATH)
  Response sendShortMessage(@PathVariable("mobile") String mobile);
}
