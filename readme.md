# CACHING APPLICATION

A chaching application which provides basic caching services .

## Installation

Run the following command in the root directory to build the application
```bash
mvn clean install
```
Run the following command to run the aaplication
```bash
java -jar target/cacheapplication-0.0.1-SNAPSHOT.jar
```
Use the following URL to access the exposed API

[http://<HOST_IP>:<HOST_PORT>/query/mysql](http://<HOST_IP>:<HOST_PORT>/query/mysql)

Request Body should consist the following

* Query – Any MYSQL query (Insert, select, update, delete)
* Parameters – An array of parameters
* Type – type of INSERT, SELECT, DELETE, UPDATE

Use the following Request body to invoke the API


```python
{
	"query": "select * from user where id < ?",
   	"parameters": [4],
	"type": "SELECT"
}

```

## Design Documentation

* To handle the caching a concurrent hashmap is used. Concurrent hashmap is used to ensure thread safety and performance
* Two concurrent hashmaps are being used to handle the cache. One hashmap store the formatted query as the key and the queried values as   the value. The other hashmap stores the table name as the key and the list of queries related to that table
* Caching concepts of read through and write through are used within the application
* In read through whenever a SELECT query comes in application would query the cache first. If the cache does not contain the data or if   the data is invalidated, it will query the database and then update the hashmap with the queried data for future use
* In write through when an INSERT, UPDATE, DELETE query comes in, hashmap value will not be updated but will mark the content invalidate   using a flag
* Caching application is designed as a spring boot application where an API is being exposed to execute the queries
* If the database connectivity fails  at some point, if user queries for any data that is already in the cache then data from cache will be returned to the application. If the queried data is not cached then an error will be thrown

## Draw Backs 

* This design is addressing only MYSQL database connections
* Complex queries are not being addressed by the current design
* Haven't handled the cache clearance policy
* If the update query doesn’t update any fields still the hashmap will invalidate 
  the data within it
