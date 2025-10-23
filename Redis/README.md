# Redis

## Connect:
```yaml
spring:
  cache:
    type: redis
  data:
    redis:
      host: localhost
      port: 6379
      password: 2410
      timeout: 6000ms
```

## Run docker-compose
```
docker-compose up -d
```