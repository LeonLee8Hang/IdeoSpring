package com.ideonet.web.method.support;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;


public interface HandlerMethodArgumentResolver {

    Boolean support(Parameter parameter);

    Object resolveArgument(HttpServletRequest request, Class<?> requiredType, Parameter parameter);
}
