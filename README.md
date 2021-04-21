# Wolt

Opening hours challenge. See [requirements](REQUIREMENTS.md) for full spec.

## Part 1: Implementation
### Prerequisites
You should have the following dependencies
* [Docker](https://www.docker.com/)
* [SBT](https://www.scala-sbt.org/)

To run the example requests below, you will need the following dependencies:
* [Httpie](https://httpie.io/)

### How to run
The solution can be run as a docker container
```
sbt clean compile stage && \
    docker build . --no-cache -t wolt:latest && \
    docker run -p 3000:3000 wolt:latest
```

### Exposed endpoints
#### HealthCheck
```
${baseUrl}/healthz
```
When the service is up, this endpoint will return `200 OK` to indicate it is healthy.

If run using the Docker image, this is also used by the Docker `HEALTHCHECK`.

##### Example Request
```
http GET http://localhost:3000/status
```

#### Pretty Print
```
${baseUrl}/hours/pretty_print
```

This endpoint takes input of `Json` in the format described (see [requirements](REQUIREMENTS.md#Input).)

##### Example Request
```
echo '{   "monday":[   ],   "tuesday":[      {         "type":"open",         "value":36000      },      {         "type":"close",         "value":64800      }   ],   "wednesday":[   ],   "thursday":[      {         "type":"open",         "value":36000      },      {         "type":"close",         "value":64800      }   ],   "friday":[      {         "type":"open",         "value":36000      }   ],   "saturday":[      {         "type":"close",         "value":3600      },      {         "type":"open",         "value":36000      }   ],   "sunday":[      {         "type":"close",         "value":3600      },      {         "type":"open",         "value":43200      },      {         "type":"close",         "value":75600      }   ]}' | \
  http POST http://localhost:3000/hours/pretty_print
```


## Part 2: Json Format
