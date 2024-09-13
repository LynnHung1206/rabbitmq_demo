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