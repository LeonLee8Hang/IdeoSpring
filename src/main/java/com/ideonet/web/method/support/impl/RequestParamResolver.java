package com.ideonet.web.method.support.impl;

import com.ideonet.web.annotation.RequestParam;
import com.ideonet.web.constants.ValueConstants;
import com.ideonet.web.method.model.NamedValueInfo;
import com.ideonet.exception.IdeoException;
import com.ideonet.web.method.convert.RequestParameterConverter;
import com.ideonet.web.method.support.HandlerMethodArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;

public class RequestParamResolver implements HandlerMethodArgumentResolver {
    @Override
    public Boolean support(Parameter parameter) {
        return parameter.getAnnotation(RequestParam.class) != null;
    }

    @Override
    public Object resolveArgument(HttpServletRequest request, Class<?> requiredType, Parameter parameter) {
        NamedValueInfo namedValueInfo = buildNamedValueInfo(parameter);
        String value = request.getParameter(namedValueInfo.getName());
        if (value == null) {
            if (namedValueInfo.isRequired() && namedValueInfo.getDefaultValue() == null) {
                throw new IdeoException("RequestParam for '" + namedValueInfo.getName() + "' value can not be null");
            }
            value = namedValueInfo.getDefaultValue();
        }
        RequestParameterConverter converter = new RequestParameterConverter();
        return converter.convert(requiredType, value);
    }

    private NamedValueInfo buildNamedValueInfo(Parameter parameter) {
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        String defaultValue = requestParam.defaultValue().equals(ValueConstants.DEFAULT_NONE) ? null : requestParam.defaultValue();
        NamedValueInfo valueInfo = new NamedValueInfo(requestParam.value(), requestParam.required(), defaultValue);
        return valueInfo;
    }
}
