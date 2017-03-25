[<img src="https://img.shields.io/travis/jameshforster/OverVerse.svg"/>](https://travis-ci.org/jameshforster/OverVerse)

# OverVerse

This is a protected microservice to control the game logic behind the OverVerse server.

## Running

Run this using [sbt](http://www.scala-sbt.org/).

```
sbt run
```

And then go to http://localhost:9000 to see the running web application.

There are several demonstration files available in this template.

## Endpoints

## Test Endpoints

### /test-only/generators/planet

Body
```
{
    "systemCoordinateModel":{
        "sectorCoordinateModel":{
            "X":1,
            "Y":2
        },
        "x":3,
        "y":4
    },
    "z":3
}
```
