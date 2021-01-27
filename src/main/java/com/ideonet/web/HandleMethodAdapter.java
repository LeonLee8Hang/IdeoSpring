package com.ideonet.web;

import com.ideonet.web.annotation.ResponseBody;
import com.ideonet.web.method.model.HandlerMethod;
import com.ideonet.web.method.support.HandlerMethodArgumentResolver;
import com.ideonet.web.method.support.impl.BuildInObjectsResolver;
import com.ideonet.web.method.support.impl.RequestModelResolver;
import com.ideonet.web.method.support.impl.RequestParamResolver;
import com.ideonet.exception.IdeoException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;


public class HandleMethodAdapter {

    public static final String REDIRECT_URL_PREFIX = "redirect:";

    public void handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        if (method.getReturnType() != String.class) {
            throw new IdeoException("unsupported method '" + method.getName() + "',request method can only return 'java.lang.String'");
        }
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
        resolvers.add(new RequestParamResolver());
        resolvers.add(new RequestModelResolver());
        resolvers.add(new BuildInObjectsResolver());
        Parameter[] handleMethodParameters = method.getParameters();
        Object[] args = new Object[handleMethodParameters.length];
        for (int i = 0; i < handleMethodParameters.length; i++) {
            Parameter parameter = handleMethodParameters[i];
            boolean matchedResolver = false;
            for (HandlerMethodArgumentResolver resolver : resolvers) {
                if (resolver.support(parameter)) {
                    matchedResolver = true;
                    Object arg = resolver.resolveArgument(request, parameter.getType(), parameter);
                    args[i] = arg;
                }
            }
            if (!matchedResolver) {
                throw new IdeoException("can not find resolver for request method '" + method.getName() + "',please check your method parameters");
            }
        }
        String returnValue;
        try {
            returnValue = (String) method.invoke(handlerMethod.getBean(), args);
            if (method.getAnnotation(ResponseBody.class) == null) {
                if (returnValue.startsWith(REDIRECT_URL_PREFIX)) {
                    response.sendRedirect(returnValue.substring(REDIRECT_URL_PREFIX.length()));
                    return;
                }
                RequestDispatcher requestDispatcher = request.getRequestDispatcher(returnValue);
                requestDispatcher.forward(request, response);
                return;
            }
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().print(returnValue);
        } catch (Exception e) {
            throw new IdeoException("invoke method '" + method.getName() + "' failed", e);
        }
    }
}
