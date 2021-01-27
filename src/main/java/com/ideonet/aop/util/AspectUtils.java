package com.ideonet.aop.util;

import com.ideonet.aop.annotation.After;
import com.ideonet.aop.annotation.Aspect;
import com.ideonet.aop.annotation.Before;
import com.ideonet.util.ReflectionUtils;
import com.ideonet.exception.IdeoException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AspectUtils {

    private static Map<Class<?>, Object> advisorInstanceMap = new HashMap<>();

    private static Map<String, Method> advisorBeforeMap = new HashMap<>();

    private static Map<String, Method> advisorAfterMap = new HashMap<>();

    static {
        Set<Class<?>> aspectSet = ReflectionUtils.getAllClass(Aspect.class);
        for (Class<?> asp : aspectSet) {
            try {
                advisorInstanceMap.put(asp, asp.newInstance());
            } catch (Exception e) {
                throw new IdeoException("can not create instance for '" + asp.getName() + "'", e);
            }
            for (Method method : asp.getDeclaredMethods()) {
                if (method.getAnnotation(Before.class) != null) {
                    advisorBeforeMap.put(method.getAnnotation(Before.class).value(), method);
                }
                if (method.getAnnotation(After.class) != null) {
                    advisorAfterMap.put(method.getAnnotation(After.class).value(), method);
                }
            }
        }
    }

    public static Method getBeforeAdvisorMethod(String methodName) {
        for (Map.Entry<String, Method> entry : advisorBeforeMap.entrySet()) {
            if (methodName.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Method getAfterAdvisorMethod(String methodName) {
        for (Map.Entry<String, Method> entry : advisorAfterMap.entrySet()) {
            if (methodName.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Object getAdvisorInstance(Class<?> clazz) {
        return advisorInstanceMap.get(clazz);
    }
}
