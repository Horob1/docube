# Message Queue

## Connect:
```
spring.kafka.bootstrap-servers=localhost:7092
spring.kafka.properties.security.protocol=SASL_PLAINTEXT
spring.kafka.properties.sasl.mechanism=PLAIN
spring.kafka.properties.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="horob1" password="horob1-secret-password";
#password and username in kafka_server_jaas.conf

```

## Run docker-compose
```
docker-compose up -d
```