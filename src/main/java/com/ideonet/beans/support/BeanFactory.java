package com.ideonet.beans.support;


public interface BeanFactory {
    Object getBean(String name);

    <T> T getBean(Class<T> requiredType);

    boolean containsBean(String name);
}
