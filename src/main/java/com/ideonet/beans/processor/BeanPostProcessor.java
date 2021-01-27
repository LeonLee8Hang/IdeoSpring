package com.ideonet.beans.processor;

public interface BeanPostProcessor {
    Object postProcessAfterInitialization(Object bean, String beanName);
}
