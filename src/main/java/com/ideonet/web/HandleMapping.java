package com.ideonet.web;

import com.ideonet.beans.util.ApplicationContextUtils;
import com.ideonet.util.ReflectionUtils;
import com.ideonet.web.annotation.Controller;
import com.ideonet.web.annotation.RequestMapping;
import com.ideonet.web.method.model.HandlerMethod;
import com.ideonet.exception.IdeoException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class HandleMapping {
    private Map<String, HandlerMethod> mappings = new HashMap<>();

    public void init() {
        Set<Class<?>> controllerSet = ReflectionUtils.getAllClass(Controller.class);
        controllerSet.forEach((controller) -> {
            RequestMapping requestMappingAnnotation = controller.getAnnotation(RequestMapping.class);
            if (requestMappingAnnotation == null) {
                throw new IdeoException("controller '" + controller.getName() + "' must have a '@RequestMapping' annotation");
            }
            String parentPath = requestMappingAnnotation.value();
            Method[] methods = controller.getMethods();
            for (Method method : methods) {
                RequestMapping methodRequestMappingAnnotation = method.getAnnotation(RequestMapping.class);
                if (methodRequestMappingAnnotation == null) {
                    continue;
                }
                String path = methodRequestMappingAnnotation.value();
                try {
                    mappings.put(parentPath + path, new HandlerMethod(ApplicationContextUtils.getContext().getBean(controller), method));
                } catch (Exception e) {
                    throw new IdeoException("init controller failed,can not create instance for controller '" + controller.getName() + "'", e);
                }
            }
        });
    }

    public HandlerMethod getHandler(String url) {
        HandlerMethod handleMethod = mappings.get(url);
        if (handleMethod == null) {
            throw new IdeoException("path '" + url + "' can not find handle");
        }
        return handleMethod;
    }
}
