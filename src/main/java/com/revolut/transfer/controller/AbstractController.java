package com.revolut.transfer.controller;

import com.revolut.transfer.service.ServiceContext;
import com.revolut.transfer.service.impl.ServiceContextImpl;
import io.javalin.Context;

class AbstractController {

    static final ServiceContext serviceContext = ServiceContextImpl.getInstance();

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

}
