# Wolt

Opening hours challenge. See [requirements](REQUIREMENTS.md) for full spec.

## Part 1: Implementation
### Prerequisites
You should have the following dependencies
* Docker
* SBT

### How to run
The solution can be run as a docker container
```
sbt clean compile stage && \
    docker build . --no-cache -t wolt:latest \
    docker run -p 3000:3000 wolt:latest
```

### Exposed endpoints
#### HealthCheck
```
${baseUrl}/healthz
```
When the service is up, this endpoint will return `200 OK` to indicate it is healthy.

If run using the Docker image, this is also used by the Docker `HEALTHCHECK`.

```
http GET http://localhost:3000/status
```


## Part 2: Json Format
