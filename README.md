Spring Cloud Demo
=================

* Spring Cloud Eureka
* Spring Cloud Feign

### How to run

1. Start Eureka first

  ```
  $ cd eureka
  $ ./gradlew bootRun
  ```

  Visit http://localhost:9091/eureka-server/

1. Start 'Gmt' and 'Local' services

  ```
  $ cd gmt
  $ ./gradlew bootRun
  ```

  Visit http://localhost:9092/gmt/

  ```
  $ cd local
  $ ./gradlew bootRun
  ```

  Visit http://localhost:9093/local/

1. Invoke Pep Logo Service via 'Local' Service

  Visit http://localhost:9093/local/logo

1. Invoke UIP login Service via 'Local' Serivce

  Visit http://localhost:9093/local/index
