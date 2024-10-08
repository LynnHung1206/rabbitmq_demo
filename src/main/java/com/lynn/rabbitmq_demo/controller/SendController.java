package com.lynn.rabbitmq_demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lynn.rabbitmq_demo.dto.CustomCorrelationData;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

import static com.lynn.rabbitmq_demo.properties.RabbitExchangeProperties.DIRECT_EXCHANGE_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitExchangeProperties.FANOUT_EXCHANGE_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitExchangeProperties.TOPIC_EXCHANGE_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.SIMPLE_QUEUE_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.WORK_QUEUE_NAME;

/**
 * @Author: Lynn on 2024/9/5
 */
@Slf4j
@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
public class SendController {

  private final RabbitTemplate rabbitTemplate;

  private static ObjectMapper objectMapper = new ObjectMapper();

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
  public void sendFanoutMessage(@RequestBody Map<String, String> map) {
    String message = MapUtils.getString(map, "message");
    rabbitTemplate.convertAndSend(FANOUT_EXCHANGE_NAME, "", message);
  }

  @RequestMapping(value = "/direct", method = {RequestMethod.GET, RequestMethod.POST})
  public void sendDirectMessage(@RequestBody Map<String, String> map) {
    String message = MapUtils.getString(map, "message");
    String key = MapUtils.getString(map, "key");
    rabbitTemplate.convertAndSend(DIRECT_EXCHANGE_NAME, key, message);
  }

  @RequestMapping(value = "/topic", method = {RequestMethod.GET, RequestMethod.POST})
  @SneakyThrows
  public void sendTopicMessage(@RequestBody Map<String, String> map) {
    String message = MapUtils.getString(map, "message");
    String key = MapUtils.getString(map, "key");
    String mapStr = objectMapper.writeValueAsString(map);

    rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, key, mapStr, new CustomCorrelationData(UUID.randomUUID().toString(),TOPIC_EXCHANGE_NAME,key,mapStr));
  }


}
