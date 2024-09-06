## rabbit demo

### work queue
讓多個消費者綁定到一個 queue，共同消費隊列消息。
默認為輪詢，如果怕有消息堆積，請至 `application.properties` 設定 prefetch=1 意為消費完消息才會再拿下一條

### exchange
- Fanout: 會將消息分給每個人（廣播）
- direct: 根據規則(binding key)路由到指定 queue
- topic: 類似 direct,但 routing key 可以為多個單字的列表, 以 `.` 分隔, binding key 可使用通配符 `#`:代指0或多個單字, `*`:代指1個單字

