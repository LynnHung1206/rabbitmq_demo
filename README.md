## rabbit demo

### work queue
讓多個消費者綁定到一個 queue，共同消費隊列消息。
default 為輪詢，如果怕有消息堆積，請至 `application.properties` 設定 prefetch=1 意為消費完消息才會再拿下一條

### exchange
- Fanout: 會將消息分給每個人（廣播）
- direct: 根據規則(binding key)路由到指定 queue
- topic: 類似 direct,但 routing key 可以為多個單字的列表, 以 `.` 分隔, binding key 可使用通配符 `#`:代指0或多個單字, `*`:代指1個單字
    + xxx.# (以xxx.開頭的所有都會被綁定)
    + #.xxx (.xxx結尾的都會被綁定)
    + #.# 所有消息
    + `#` 可為多個單字，`*` 只一個單字
    + child.* 的話， child.boy.good 不行，child.#則可以
- CustomExchange: 自定義的 exchange，當預設的 exchange 無法滿足需求可自定義，需繼承 `CustomExchange`實作
- HeadersExchange: 根據 headers 來路由，不依賴 routingKey，可指定一組 headers
  + ```java
    HeadersExchange headersExchange = new HeadersExchange("myHeadersExchange");
    Queue queue = new Queue("myQueue");
    // 綁定佇列時，指定需要匹配的 headers
    Map<String, Object> headers = new HashMap<>();
      headers.put("format", "xxx");
      headers.put("x-match", "all");
    Binding binding = BindingBuilder.bind(queue).to(headersExchange).whereAll(headers).match();
    ``` 

### message convert
不要使用 default 的 SimpleMessageConverter，有可能會有序列化上的問題，如果需要在 server 端查看也會無法閱讀，
可以使用 jackson 轉換為 String，監聽器接收後再行處理

### 可靠性
- producer : 會額外消耗網路及系統資源，可以不使用，如需使用，可使用 callback 即，return通常是開發者層面的問題
  + producer reconnect:在 `application.properties` 做設定，要注意此為阻塞式設計，會阻塞當前執行序，慎用並合理配置 這只是連接失敗的重試，並非消息失敗的重試
  + producer acknowledgement(ack)
    + 失敗會返回 nack，可針對此作 `有限次數` 的重試
    +  `application.properties` 設定
    + 消息失敗可以設定重試
    + 每個 rabbitTemplate 只能設定一個 ReturnCallback，在啟動時設定
    + 慎用 速度會變慢
  - mq
    + 數據持久化
      + 存 memory
    + Lazy Queue
      + 存 disk （memory只留最近的消息 預設2048）
      + 消費者消費消息才會讀取 disk 並加載到 memory
      + 支持數百萬條消息儲存
      + 3.12 版後 全部的隊列都是這個
      + 速度會稍慢
      + 開啟持久化和生產者確認，會在持久化完成後返回ack
- consumer
  - 確認機制 -> spring 已實作
    - ack : 成功處理，rabbitmq 自隊列刪除
    - nack: 處理失敗，需再次投遞
    - reject: 處理失敗並且拒絕該消息，rabbitmq 自隊列刪除（有問題的消息）-> 消息轉換異常會出現（MessageConversionException）  
  - 可選：
    - none
    - manual 手動處理
    - auto
  - 失敗處理
    - 失敗重試機制
      - RejectAndDontRequeueRecoverer : 重試耗盡後，直接 reject & 丟棄 
      - ImmediateRequeueMessageRecoverer : 重試耗盡後，返回 NACK，消息重新入隊
      - RepublishMessageRecoverer : 重試耗盡後，將失敗消息投到指定的交換機