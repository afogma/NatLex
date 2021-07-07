# NatLex backend test

This project implements CRUD operations to work with Sections which contains Geological Classes , in database(
PostgreSQL). It is also possible to import files from .xls datasheet or export into it. Includes track of import/export
progress by id.

Database has two tables. First one contains names of the sections marked as id, and list of geological classes codes,
which current section includes. Second table contains names of geological classes marked as id, and codes matches those
classes.

All information about import/export , which is status of current process, byte array (contains data to load in/out from
database), id of the process), stored in memory for test purposes. In real case data should be cached in temp files.

To simplify basic authentication/authorization: username, password and role was written directly in web configuration
class. Default username: "admin" , password "admin".

Project built with jdk11. It includes lombok, flyway, spring security and openapi libraries. To inspect all crud methods
follow url: http://server-ip:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/

Curl command for testing: curl -u admin:admin http://server-ip:8080/api/

### `GET /api/sections`

response example :
```json
[
  {
    "name": "Section 5",
    "geologicalClasses": [
      {
        "name": "Geo Class 51",
        "code": "GC51"
      },
      {
        "name": "Geo Class 52",
        "code": "GC52"
      }
    ]
  },
  {
    "name": "Section 6",
    "geologicalClasses": [
      {
        "name": "Geo Class 62",
        "code": "GC62"
      },
      {
        "name": "Geo Class 64",
        "code": "GC64"
      }
    ]
  }
]
```
Response : `200 OK`


### `GET /api/sections/by-code?code=GC62`

Response example :
```
[
  "Section 6"
]
```

Response : `200 OK`


### `POST /api/section/add`

Body example :
```
{
    "name": "Section 8",
    "geologicalClasses": [
      {
        "name": "Geo Class 83",
        "code": "GC83"
      },
      {
        "name": "Geo Class 85",
        "code": "GC85"
      }
    ]
}
```

Response : `200 OK`


### `PUT /api/section/Section 8`

Body example :
```
{
    "name": "Section 8",
    "geologicalClasses": [
      {
        "name": "Geo Class 83",
        "code": "GC83"
      },
      {
        "name": "Geo Class 87",
        "code": "GC87"
      }
    ]
}
```

Response : `200 OK`


### `DELETE /api/section/Section 8`

Response : `200 OK`


### `POST /api/import`

Body example :
```
Multipart form: "file", Attachment.File=sections.xls
```
Response : `"5d92220f-0ecd-4dce-aa8e-e0a2379fef7d"`


### `GET /api/import/5d92220f-0ecd-4dce-aa8e-e0a2379fef7d`

Response : `"DONE"`


### `GET /api/export`

Response : `"d1f91a36-67e4-418b-9833-ef5bbe90ea99"`


### `GET /api/export/d1f91a36-67e4-418b-9833-ef5bbe90ea99`

Response : `IN PROGRESS`


### `GET /api/export/d1f91a36-67e4-418b-9833-ef5bbe90ea99/file`

Response : Downloadable file: `job_d1f91a36-67e4-418b-9833-ef5bbe90ea99.xls`

