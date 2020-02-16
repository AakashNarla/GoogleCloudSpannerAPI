Feature: the message can be retrieved

  Scenario: Get All Spanner Instances Configs /configs
    When the client calls /configs:
    Then the client receives status code of 200
    Then the client receives server version [{"id":{"project":"helical-fin-89110","instanceConfig":"eur3","name":"projects/helical-fin-89110/instanceConfigs/eur3"},"displayName":"Europe (Belgium/Netherlands)"}]



  Scenario: Test Spanner Create/Status Instances APIs
    When create new spanner instance:
    Then the client receives status code of 200
    Then the client receives server version {"message":"Successfully Created Instance Id : spanner-demo","status":"success"}

    When get current spanner instance:
    Then the client receives status code of 200
    Then the client receives server version {"nodeCount": 1,"state": "READY","displayName": "spanner-demo","instanceConfigId": {"instanceConfig": "regional-asia-south1","project": "helical-fin-89110","name": "projects/helical-fin-89110/instanceConfigs/regional-asia-south1"}}

    When get All spanner instance:
    Then the client receives status code of 200
    Then the client receives server version [{"nodeCount": 1,"state": "READY","displayName": "spanner-demo","instanceConfigId": {"instanceConfig": "regional-asia-south1","project": "helical-fin-89110","name": "projects/helical-fin-89110/instanceConfigs/regional-asia-south1"}}]


  Scenario: Test Database CRUD APIs
    When create new database:
    Then the client receives status code of 200
    Then the client receives server version {"id": {"name": "projects/helical-fin-89110/instances/spanner-demo/databases/spanner","database": "spanner","instanceId": {"instance": "spanner-demo","project": "helical-fin-89110","name": "projects/helical-fin-89110/instances/spanner-demo"}},"state": "READY"}

    When get current database status:
    Then the client receives status code of 200
    Then the client receives server version {"id": {"name": "projects/helical-fin-89110/instances/spanner-demo/databases/spanner","database": "spanner","instanceId": {"instance": "spanner-demo","project": "helical-fin-89110","name": "projects/helical-fin-89110/instances/spanner-demo"}},"state": "READY"}


    When delete database:
    Then the client receives status code of 200
    Then the client receives server version {"message":"Successfully Deleted","status":"success"}

  Scenario: Test Spanner Update/Deleted Instances APIs
    When update spanner instance:
    Then the client receives status code of 200
    Then the client receives server version {"message":"Successfully Updated Instance : spanner-demo-1","status":"success"}

    When delete spanner instance:
    Then the client receives status code of 200
    Then the client receives server version {"message":"Successfully Deleted","status":"success"}



  Scenario: client makes call to GET /actuator/info
    Given the client calls /actuator/info
    Then the client receives status code of 200
    Then the client receives server version {"name":"Google Spanner API"}