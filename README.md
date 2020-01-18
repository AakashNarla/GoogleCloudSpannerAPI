# GoogleCloudSpannerAPI

### Spanner API Documentation API
Google Spanner API Documentation file : [Spanner API](https://raw.githubusercontent.com/AakashNarla/GoogleCloudSpannerAPI/master/google-spanner-api.yaml) 

Open Documentation in [Editor Swagger](http://editor.swagger.io/)

### Guides To Executing the program
* To Clean and Build:
``` .\gradlew clean build ```

* To Run the application:
``` .\gradlew bootRun ```
* Once application is up, please open [http://localhost:8080](http://localhost:8080/) on any browser, it will redirect to Swagger UI.
Updated API's will be add here for testing.



### Schema updates

| Schema operation            | Estimated duration                                                                                                                                                         |
|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| CREATE TABLE                | Minutes                                                                                                                                                                    |
| CREATE INDEX                | Minutes to hours, if the base table is created before the index.  Minutes, if the statement is executed at the same time as the CREATE TABLE statement for the base table. |
| DROP TABLE                  | Minutes                                                                                                                                                                    |
| DROP INDEX                  | Minutes                                                                                                                                                                    |
| ALTER TABLE ...ADD COLUMN   | Minutes                                                                                                                                                                    |
| ALTER TABLE ...ALTER COLUMN | Minutes to hours, if background validation is required. Minutes, if background validation is not required.                                                                 |
| ALTER TABLE ...DROP COLUMN  | Minutes                                                                                                                                                                  |

Source : [Google Source Docs](https://cloud.google.com/spanner/docs/schema-updates)
