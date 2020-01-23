Feature: the message can be retrieved

  Scenario: client makes call to GET /actuator
    When the client calls /actuator
    Then the client receives status code of 200

  Scenario: client makes call to GET /actuator/info
    Given the client calls /actuator/info
    Then the client receives status code of 200
    Then the client receives server version {"name":"Google Spanner API"}