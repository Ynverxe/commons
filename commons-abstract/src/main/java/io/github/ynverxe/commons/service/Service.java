package io.github.ynverxe.commons.service;

/**
 * Represents a component which provides a functionality.
 */
public interface Service {

  void start() throws Exception;

  void reload() throws Exception;

  void stop() throws Exception;

}