## rabbit demo

### work queue
讓多個消費者綁定到一個 queue，共同消費隊列消息。
默認為輪詢，如果怕有消息堆積，請至 `application.properties` 設定 prefetch=1 意為消費完消息才會再拿下一條

