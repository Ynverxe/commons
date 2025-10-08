package io.github.ynverxe.commons.crud.crud;

import io.github.ynverxe.commons.crud.CrudRepository;
import io.github.ynverxe.commons.crud.exception.EntityNotFoundException;
import io.github.ynverxe.commons.crud.exception.FailedCrudOperationException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

import static java.nio.file.Files.*;

public class BinaryFileCrudRepository implements CrudRepository<String, byte[]> {

  protected final Path root;

  public BinaryFileCrudRepository(@NotNull Path root) {
    this.root = Objects.requireNonNull(root, "root");
  }

  @Override
  public void upsert(@NotNull String key, byte @NotNull [] object) throws FailedCrudOperationException {
    try {
      write(buildPath(key), object, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException exception) {
      throw new FailedCrudOperationException(exception);
    }
  }

  @Override
  public void remove(@NotNull String key) throws FailedCrudOperationException {
    try {
      delete(buildPath(key));
    } catch (IOException exception) {
      if (exception instanceof NoSuchFileException) {
        throw new EntityNotFoundException(exception);
      }

      throw new FailedCrudOperationException(exception);
    }
  }

  @Override
  public byte @NotNull [] read(@NotNull String key) throws FailedCrudOperationException {
    try {
      return readAllBytes(buildPath(key));
    } catch (IOException exception) {
      if (exception instanceof NoSuchFileException) {
        throw new EntityNotFoundException(exception);
      }

      throw new FailedCrudOperationException(exception);
    }
  }

  private Path buildPath(@NotNull String key) throws IOException {
    if (!Files.exists(root)) {
      Files.createDirectories(root);
    }

    return this.root.resolve(Paths.get(key));
  }
}