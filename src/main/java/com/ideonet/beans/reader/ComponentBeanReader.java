package com.ideonet.beans.reader;

import com.ideonet.beans.model.BeanDefinition;
import com.ideonet.beans.support.impl.DefaultBeanFactory;
import com.ideonet.util.ReflectionUtils;
import com.ideonet.util.StringUtils;
import com.ideonet.web.annotation.Controller;
import com.ideonet.beans.annotation.Component;

import java.util.Set;


public class ComponentBeanReader {

    public void readBeanDefinition(DefaultBeanFactory beanFactory) {
        Set<Class<?>> componentSet = ReflectionUtils.getAllClass(Component.class);
        Set<Class<?>> controllerSet = ReflectionUtils.getAllClass(Controller.class);
        componentSet.addAll(controllerSet);
        componentSet.forEach((componentClass) -> {
            BeanDefinition beanDefinition = new BeanDefinition();
            String beanName = componentClass.getAnnotation(Component.class) != null ? componentClass.getAnnotation(Component.class).value() : componentClass.getAnnotation(Controller.class).value();
            if ("".equals(beanName)) {
                beanName = StringUtils.lowerFirst(componentClass.getSimpleName());
            }
            beanDefinition.setBeanClass(componentClass);
            beanFactory.registerBeanDefinition(beanName, beanDefinition);
        });
    }
}
