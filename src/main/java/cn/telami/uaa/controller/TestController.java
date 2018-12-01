package cn.telami.uaa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  static final String PATH = "/v1/api/uaa/test/";
  static final String NORMAL_PATH = PATH + "normal";
  static final String ADMIN_PATH = PATH + "admin";

  @GetMapping(path = NORMAL_PATH)
  public String normal() {
    return "I'm normal string";
  }

  @GetMapping(path = ADMIN_PATH)
  public String admin() {
    return "I'm admin string";
  }
}
