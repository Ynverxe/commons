package io.github.ynverxe.commons.crud.error;

public interface AsyncHandler<K, T> extends AsyncErrorLogger, MissingEntityStrategy<K, T> {
}