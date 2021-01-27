package com.ideonet.beans;

import com.ideonet.beans.processor.impl.DefaultAdvisorAutoProxyCreator;
import com.ideonet.beans.support.BeanFactory;
import com.ideonet.beans.support.impl.DefaultBeanFactory;
import com.ideonet.beans.reader.ComponentBeanReader;


public class ApplicationContext implements BeanFactory {

    private DefaultBeanFactory beanFactory = new DefaultBeanFactory();

    public ApplicationContext() {
        loadBeanDefinitions(beanFactory);
        postProcessBeanFactory(beanFactory);
        finishBeanFactoryInitialization(beanFactory);
    }

    private void loadBeanDefinitions(DefaultBeanFactory beanFactory) {
        ComponentBeanReader beanReader = new ComponentBeanReader();
        beanReader.readBeanDefinition(beanFactory);
    }

    private void postProcessBeanFactory(DefaultBeanFactory beanFactory) {
        beanFactory.addBeanPostProcessor(new DefaultAdvisorAutoProxyCreator());
    }

    public void finishBeanFactoryInitialization(DefaultBeanFactory beanFactory) {
        beanFactory.preInstantiateSingletons();
    }

    @Override
    public Object getBean(String name) {
        return getBeanFactory().getBean(name);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return getBeanFactory().getBean(requiredType);
    }

    @Override
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    public DefaultBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
