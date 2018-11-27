package com.revolut.transfer.controller;

import io.javalin.Context;

import java.sql.Timestamp;
import java.util.Objects;

class AbstractController {

    static long pathParamToLong(Context context, String paramName) {

        final String paramValue = context.pathParam(paramName);

        try {
            return Long.parseLong(paramValue);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException(
                    String.format("Invalid value '%s' for path param ':%s'",
                            paramValue,
                            paramName));
        }

    }

    static Timestamp queryParamToTimestamp(Context context, String paramName) {

        final String paramValue = context.queryParam(paramName);

        if (Objects.isNull(paramValue)) {
            return null;
        }

        try {
            final long longValue = Long.parseLong(paramValue);
            return new Timestamp(longValue);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException(
                    String.format("Invalid value '%s' for query param ':%s'",
                            paramValue,
                            paramName));
        }

    }

}
