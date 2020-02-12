Feature: the message can be retrieved

  Scenario: Get All Spanner Instances Configs /configs
    When the client calls /configs:
    Then the client receives status code of 200



  Scenario: Test Spanner Instances APIs
    When create new spanner instance:
    Then the client receives status code of 200

    When get current spanner instance:
    Then the client receives status code of 200

    When get All spanner instance:
    Then the client receives status code of 200

    When update spanner instance:
    Then the client receives status code of 200

    When delete spanner instance:
    Then the client receives status code of 200



  Scenario: client makes call to GET /actuator/info
    Given the client calls /actuator/info
    Then the client receives status code of 200
    Then the client receives server version {"name":"Google Spanner API"}