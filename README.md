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
When the service is up, this endpoint will return `204 No Content` to indicate it is healthy.

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
> Is the current JSON structure the best way to store that kind of data or can you come up with a better version?
### Seconds since Epoch for time
It seems to me that there is no benefit to serialising the times as a Unix timestamp in seconds.
Though it is trivial to define a `Deserialiser` for this format, it only serves to obfuscate the data in the `Json`.

I would suggest a simple representation of `hh:mm` would be clearer both in the `Json` and would likely get a `Deserialiser` in this case "for free"
e.g.
```json
{
   "friday":[
      {
         "type":"open",
         "value":"18:00"
      }
   ],
   "saturday":[
      {
         "type":"close",
         "value":"01:00"
      },
      {
         "type":"open",
         "value":"09:00"
      },
      {
         "type":"close",
         "value":"11:00"
      },
      {
         "type":"open",
         "value":"16:00"
      },
      {
         "type":"close",
         "value":"23:00"
      }
   ]
}
```
There is also a question of whether the TimeZone of the opening hours should be included in the `Json`.
However, I assume that this is explicitly intended to be decoupled and would be based on the restaurant's location - This makes sense - we could also always serialise to UTC.
Though it should not be left up to the App/Browser to derive the user's timezone in this case.

### Order dependant arrays
In the current structure, we rely on the assumption that the arrays are in order, but there is nothing in the json which enforces or denotes the order explicitly.

I would suggest to modify the structure here in a way which explicitly denotes the order of the elements.
e.g.
```json
{
  "friday":{
    "1":{
      "type":"open",
      "value":"18:00"
    }
  },
  "saturday":{
    "1":{
      "type":"close",
      "value":"01:00"
    },
    "2":{
      "type":"open",
      "value":"09:00"
    },
    "3":{
      "type":"close",
      "value":"11:00"
    },
    "4":{
      "type":"open",
      "value":"16:00"
    },
    "5":{
      "type":"close",
      "value":"23:00"
    }
  }
}
```

### Naming
Both `type` and `value` have no semantic meaning without the context required. Why not give these meaningful names?
e.g.
```json
{
  "friday":{
    "1":{
      "action":"open",
      "time":"18:00"
    }
  },
  "saturday":{
    "1":{
      "action":"close",
      "time":"01:00"
    },
    "2":{
      "action":"open",
      "time":"09:00"
    },
    "3":{
      "action":"close",
      "time":"11:00"
    },
    "4":{
      "action":"open",
      "time":"16:00"
    },
    "5":{
      "action":"close",
      "time":"23:00"
    }
  }
}
```
