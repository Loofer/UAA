package cn.telami.uaa.controller;

import cn.telami.uaa.dto.Response;
import cn.telami.uaa.dto.request.Oauth2ClientRequest;
import cn.telami.uaa.dto.response.Oauth2ClientResponse;
import cn.telami.uaa.model.Oauth2Client;
import cn.telami.uaa.service.Oauth2ClientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Oauth2ClientController {

  static final String PATH = "v1/api/uaa/admin/client";
  static final String CLIENT_INFO_PATH = PATH + "/{id}";
  static final String CLIENT_LIST_PATH = PATH + "/list";

  @Autowired
  private Oauth2ClientService clientService;

  /**
   * 添加client.
   */
  @PostMapping(path = PATH)
  public Response save(@RequestBody Oauth2ClientRequest request) {
    Oauth2Client oauth2Client = request.builderOauth2Client();
    oauth2Client.setClientId(RandomStringUtils.randomAlphanumeric(16));
    oauth2Client.setClientSecret("{noop}" + RandomStringUtils.randomAlphanumeric(32));
    clientService.save(oauth2Client);
    return Response.ok();
  }

  /**
   * 查询client.
   */
  @GetMapping(path = CLIENT_INFO_PATH)
  public Response get(@PathVariable Long id) {
    Oauth2Client client = clientService.getById(id);
    return Response.ok(Oauth2ClientResponse.builderOauth2Client(client));
  }

  /**
   * 查询clients.
   */
  @GetMapping(path = CLIENT_LIST_PATH)
  public Response list(Page page) {
    List<Oauth2Client> oauth2Clients = clientService.list(new QueryWrapper<>());
    List<Oauth2ClientResponse> list = oauth2Clients.stream()
        .map(Oauth2ClientResponse::builderOauth2Client)
        .collect(Collectors.toList());
    return Response.ok(list);
  }

  /**
   * 删除client.
   */
  @DeleteMapping(path = CLIENT_INFO_PATH)
  public Response delete(@PathVariable Long id) {
    clientService.removeById(id);
    return Response.ok();
  }
}
