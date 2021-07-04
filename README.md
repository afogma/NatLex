# NatLex backend test

This project implements CRUD operations to work with Sections which contains Geological Classes , in database(PostgreSQL).
It is also possible to import files from .xls datasheet or export into it. Includes track of import/export progress by id.

Database has two tables. First one contains names of the sections marked as id, and list of geological classes codes, 
which current section includes. Second table contains names of geological classes marked as id, and codes matches those classes.

All information about import/export , which is status of current process, byte array (contains data to load in/out from database),
id of the process), stored in memory for test purposes.

To simplify basic authentication/authorization: username, password and role was written directly in web configuration class.

Project built with jdk11.
To inspect all crud methods follow url: server-ip:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/