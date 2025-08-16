package com.thanlinardos.resource_server.misc.utils;

import com.thanlinardos.resource_server.model.mapped.base.BasicIdModel;
import jakarta.annotation.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class ModelUtils {

    private ModelUtils() {
    }

    public static Long getIdFromModel(@Nullable BasicIdModel model) {
        return Optional.ofNullable(model)
                .map(BasicIdModel::getId)
                .orElse(null);
    }

    public static <T extends BasicIdModel, R extends BasicIdModel> Long getIdFromNestedModel(@Nullable T model, Function<T, R> nestedModelSupplier) {
        return Optional.ofNullable(model)
                .map(t -> getIdFromModel(nestedModelSupplier.apply(t)))
                .orElse(null);
    }

    public static <T extends BasicIdModel, R extends BasicIdModel> Long getIdFromNestedModelOr(@Nullable T model, Function<T, R> nestedModelSupplier, Function<T, R> orNestedModelSupplier) {
        return Optional.ofNullable(model)
                .map(t -> getIdFromModel(nestedModelSupplier.apply(t)))
                .orElse(getIdFromNestedModel(model, orNestedModelSupplier));
    }
}
