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

## Part 2: Json Format
