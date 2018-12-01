package cn.telami.uaa.feign.fallback;

import cn.telami.uaa.dto.Response;
import cn.telami.uaa.feign.MessageCenterClient;
import org.springframework.stereotype.Component;

@Component
public class MessageCenterFallbackClient implements MessageCenterClient {
  @Override
  public Response sendShortMessage(String mobile) {
    return Response.ok("123456");
  }
}
