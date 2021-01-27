package com.ideonet.aop.proxy;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

public class ProxyInstance {

    private DefaultBeanProxy beanProxy = new DefaultBeanProxy();

    public Object getProxy(Class<?> clazz) {
        Enhancer en = new Enhancer();
        en.setSuperclass(clazz);
        en.setCallbacks(new Callback[]{beanProxy});
        return en.create();
    }
}
