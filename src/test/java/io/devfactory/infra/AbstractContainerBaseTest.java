package io.devfactory.infra;

import org.testcontainers.containers.PostgreSQLContainer;

public class AbstractContainerBaseTest {

  public static final PostgreSQLContainer POSTGRE_SQL_CONTAINER;

  static {
    POSTGRE_SQL_CONTAINER = new PostgreSQLContainer();
    POSTGRE_SQL_CONTAINER.start();
  }

}
