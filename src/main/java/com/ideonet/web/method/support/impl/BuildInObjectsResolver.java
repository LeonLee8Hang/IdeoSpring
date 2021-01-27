package com.ideonet.web.method.support.impl;

import com.ideonet.web.method.support.HandlerMethodArgumentResolver;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Parameter;


public class BuildInObjectsResolver implements HandlerMethodArgumentResolver {
    @Override
    public Boolean support(Parameter parameter) {
        Class<?> paramType = parameter.getType();
        if (ServletRequest.class.isAssignableFrom(paramType) ||
                HttpSession.class.isAssignableFrom(paramType)) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(HttpServletRequest request, Class<?> requiredType, Parameter parameter) {
        Class<?> paramType = parameter.getType();
        if (ServletRequest.class.isAssignableFrom(paramType)) {
            return request;
        }
        if (HttpSession.class.isAssignableFrom(paramType)) {
            return request.getSession();
        }
        return null;
    }
}
