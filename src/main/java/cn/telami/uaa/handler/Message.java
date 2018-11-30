package cn.telami.uaa.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(staticName = "of")
public class Message {

  public static final int SUCCESS = 200;
  public static final int FAILURE = 400;

  Integer status;
  String message;

  static Message failOf(String msg) {
    return Message.of(FAILURE, msg);
  }

  static Message successOf(String msg) {
    return Message.of(SUCCESS, msg);
  }
}