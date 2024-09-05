package com.lynn.rabbitmq_demo.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: Lynn on 2024/9/5
 */
@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class SendController {
  public static final String QUEUE_NAME = "test.queue";
  public static final String EXCHANGE_NAME = "test.exchange";

  private final RabbitTemplate rabbitTemplate;

  @RequestMapping(value = "/send", method = {RequestMethod.GET, RequestMethod.POST})
  public void sendMessage(@RequestBody Map<String, String> map) {
    String message = MapUtils.getString(map, "message");
    System.out.println("message = " + message);
    rabbitTemplate.convertAndSend(QUEUE_NAME, message);
  }
}
