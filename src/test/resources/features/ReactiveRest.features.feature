# Feature File for Cucumber Testing
# Created 7/18/18 8:42 AM by rezaalemy

Feature: I need to implement Reactive RESTFul services
  So that I can have streaming, Server side events, and non-blocking requests.

  @ReactiveRest
  Scenario: Should have a BookHandler class to handle the API requests
    Given There exists a class named "BookHandler" in "com.curisprofound.tddwebstack.controllers" package
    And   The class has a method "getAll" with parameters "ServerRequest"
    And   The class has a method "getAll" with parameters "ServerRequest" and return Type "Mono<ServerRespone>"
    Then  the "Component" annotation exists in the class annotations
