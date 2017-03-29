[<img src="https://travis-ci.org/jameshforster/OverVerse.svg?branch=master"/>](https://travis-ci.org/jameshforster/OverVerse)

# OverVerse

This is a protected microservice to control the game logic behind the OverVerse server.

## Running

Run this using [sbt](http://www.scala-sbt.org/).

```
sbt run
```

And then go to http://localhost:9000 to see the running web application.

## End points

<table>
    <tr>
        <th>Path</th>
        <th>Supported Methods</th>
        <th>Description</th>
    </tr>
</table>

## Test Endpoints /test-only

<table>
    <tr>
        <th>Path</th>
        <th>Supported Methods</th>
        <th>Description</th>
    </tr>
    <tr>
        <td>/generators/planet</td>
        <td>POST</td>
        <td>End point that generates a random planet with the submitted coordinates and returns it.</td>
    </tr>
    <tr>
        <td>/generators/system</td>
        <td>POST</td>
        <td>End point that generates a random system with the submitted coordinates and returns it.</td>
    </tr>
    <tr>
        <td>/generators/universe?size</td>
        <td>GET</td>
        <td>End point that generates a random universe with a grid of sectors determined by the size.</td>
    </tr>
</table>

## POST /test-only/generators/planet

**Body**
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

**Response**
```
{
    "coordinate": {
        "systemCoordinateModel": {
            "sectorCoordinateModel": {
                "X": 1,
                "Y": 2
            },
            "x": 3,
            "y": 4
        },
        "z": 3
    },
    "size": 9,
    "environment": {
        "name": "Gas Giant"
    },
    "attributes": [
        {
            "key": "Solar",
            "value": 2
        },
        {
            "key": "Atmosphere",
            "value": 5
        },
        {
            "key": "Metal",
            "value": 2
        },
        {
            "key": "Fuel",
            "value": 5
        },
        {
            "key": "Nuclear",
            "value": 2
        },
        {
            "key": "Volatility",
            "value": 2
        },
        {
            "key": "Temperature",
            "value": 2
        },
        {
            "key": "Wind",
            "value": 5
        },
        {
            "key": "Water",
            "value": 0
        },
        {
            "key": "Fertility",
            "value": 0
        }
    ],
    "name": "Unnamed World"
}
```

**Response Types**

<table>
    <tr>
        <th>Code</th>
        <th>Body</th>
        <th>Reason</th>
    </tr>
    <tr>
        <td>200</td>
        <td>{PlanetModel}</td>
        <td>Successful response.</td>
    </tr>
    <tr>
        <td>400</td>
        <td>Could not bind request body to json due to: {Exception}</td>
        <td>Empty or invalid json body submitted in request.</td>
    </tr>
    <tr>
        <td>500</td>
        <td>Unexpected error occurred: {Exception}</td>
        <td>Unknown error occurred.</td>
    </tr>
</table>

## POST /test-only/generators/system

**Body**
```
{
    "sectorCoordinateModel":{
        "X":1,
        "Y":2
    },
    "x":3,
    "y":4
}
```

**Response**
```
{
  "coordinates": {
    "sectorCoordinateModel": {
      "X": 1,
      "Y": 2
    },
    "x": 3,
    "y": 4
  },
  "star": {
    "size": 5,
    "age": 3,
    "category": {
      "name": "Blue Giant"
    }
  },
  "planets": [
    {
      "coordinate": {
        "systemCoordinateModel": {
          "sectorCoordinateModel": {
            "X": 1,
            "Y": 2
          },
          "x": 3,
          "y": 4
        },
        "z": 0
      },
      "size": 9,
      "environment": {
        "name": "Desert"
      },
      "name": "Unnamed World"
    }
  ],
  "entities": []
}
```

**Response Types**

<table>
    <tr>
        <th>Code</th>
        <th>Body</th>
        <th>Reason</th>
    </tr>
    <tr>
        <td>200</td>
        <td>{SystemModel}</td>
        <td>Successful response.</td>
    </tr>
    <tr>
        <td>400</td>
        <td>Could not bind request body to json due to: {Exception}</td>
        <td>Empty or invalid json body submitted in request.</td>
    </tr>
    <tr>
        <td>500</td>
        <td>Unexpected error occurred: {Exception}</td>
        <td>Unknown error occurred.</td>
    </tr>
</table>

## GET /test-only/generators/universe?size

**Example**
```
    /test-only/generators/universe?size=0
```

**Response**
```
{
  "sectors": [
    {
      "coordinates": {
        "X": 0,
        "Y": 0
      },
      "systems": [
        {
          "coordinates": {
            "sectorCoordinateModel": {
              "X": 0,
              "Y": 0
            },
            "x": 0,
            "y": 5
          },
          "star": {
            "size": 6,
            "age": 1,
            "category": {
              "name": "Blue Star"
            }
          },
          "planets": [
            {
              "coordinate": {
                "systemCoordinateModel": {
                  "sectorCoordinateModel": {
                    "X": 0,
                    "Y": 0
                  },
                  "x": 0,
                  "y": 5
                },
                "z": 0
              },
              "size": 4,
              "environment": {
                "name": "Gas Giant"
              },
              "name": "Unnamed World"
            }
          ],
          "entities": []
        }
      ]
    }
  ]
}
```

**Response Types**

<table>
    <tr>
        <th>Code</th>
        <th>Body</th>
        <th>Reason</th>
    </tr>
    <tr>
        <td>200</td>
        <td>{UniverseModel}</td>
        <td>Successful response.</td>
    </tr>
    <tr>
        <td>400</td>
        <td>BadRequest</td>
        <td>Empty or invalid query string parameter.</td>
    </tr>
    <tr>
        <td>500</td>
        <td>Unexpected error occurred: {Exception}</td>
        <td>Unknown error occurred.</td>
    </tr>
</table>