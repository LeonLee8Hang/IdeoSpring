package com.ideonet.beans.processor.impl;

import com.ideonet.aop.proxy.ProxyInstance;
import com.ideonet.beans.processor.BeanPostProcessor;

public class DefaultAdvisorAutoProxyCreator implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Object proxyBean = new ProxyInstance().getProxy(bean.getClass());
        return proxyBean == null ? bean : proxyBean;
    }
}
