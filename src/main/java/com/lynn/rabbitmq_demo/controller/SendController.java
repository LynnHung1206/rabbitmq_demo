package com.lynn.rabbitmq_demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.lynn.rabbitmq_demo.properties.RabbitProperties.FANOUT_EXCHANGE_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitProperties.SIMPLE_QUEUE_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitProperties.WORK_QUEUE_NAME;

/**
 * @Author: Lynn on 2024/9/5
 */
@Slf4j
@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
public class SendController {

  private final RabbitTemplate rabbitTemplate;

  @RequestMapping(value = "/simple", method = {RequestMethod.GET, RequestMethod.POST})
  public void sendSimpleMessage(@RequestBody Map<String, String> map) {
    String message = MapUtils.getString(map, "message");
    System.out.println("message = " + message);
    rabbitTemplate.convertAndSend(SIMPLE_QUEUE_NAME, message);
  }

  @RequestMapping(value = "/work", method = {RequestMethod.GET, RequestMethod.POST})
  public void sendWorkMessage(@RequestBody Map<String, String> map) throws InterruptedException {
    String message = MapUtils.getString(map, "message");
    System.out.println("message = " + message);
    for (int i = 0; i < 30; i++) {
      rabbitTemplate.convertAndSend(WORK_QUEUE_NAME, message + "--" + i);
      Thread.sleep(20);
    }
  }

  @RequestMapping(value = "/fanout", method = {RequestMethod.GET, RequestMethod.POST})
  public void sendFanoutMessage(@RequestBody Map<String, String> map) throws InterruptedException {
    String message = MapUtils.getString(map, "message");
    for (int i = 0; i < 30; i++) {
      rabbitTemplate.convertAndSend(FANOUT_EXCHANGE_NAME, "",message + "--" + i);
      Thread.sleep(20);
    }
  }
}
