package com.lynn.rabbitmq_demo.properties;

import lombok.Data;

/**
 * @Author: Lynn on 2024/9/6
 */
public class RabbitProperties {

  public static final String SIMPLE_QUEUE_NAME = "simple.queue";

  public static final String WORK_QUEUE_NAME = "work.queue";

  public static final String FANOUT_QUEUE_1_NAME = "fanout.queue.1";

  public static final String FANOUT_QUEUE_2_NAME = "fanout.queue.2";

  public static final String FANOUT_EXCHANGE_NAME = "fanout.exchange";
}
