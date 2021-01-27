package com.ideonet.beans.support.impl;

import com.ideonet.beans.model.BeanDefinition;
import com.ideonet.beans.processor.BeanPostProcessor;
import com.ideonet.beans.util.BeanUtils;
import com.ideonet.util.StringUtils;
import com.ideonet.beans.annotation.Resource;
import com.ideonet.beans.support.BeanFactory;
import com.ideonet.beans.support.SingletonBeanRegistry;
import com.ideonet.exception.IdeoException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanFactory extends SingletonBeanRegistry implements BeanFactory {

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(beanName, beanDefinition);
    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.add(beanPostProcessor);
    }

    public void preInstantiateSingletons() {
        this.beanDefinitionMap.forEach((beanName, beanDef) -> {
            getBean(beanName);
        });
    }

    @Override
    public Object getBean(String name) {
        return doGetBean(name);
    }

    @SuppressWarnings("unchecked")
    private <T> T doGetBean(String beanName) {
        Object bean;
        Object sharedInstance = getSingleton(beanName, true);
        if (sharedInstance != null) {
            bean = sharedInstance;
        } else {
            BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
            if (beanDefinition == null) {
                throw new IdeoException("can not find the definition of bean '" + beanName + "'");
            }
            bean = getSingleton(beanName, () -> {
                try {
                    return doCreateBean(beanName, beanDefinition);
                } catch (Exception ex) {
                    removeSingleton(beanName);
                    throw ex;
                }
            });
        }
        return (T) bean;
    }

    private Object doCreateBean(String beanName, BeanDefinition beanDefinition) {
        Object bean = createBeanInstance(beanName, beanDefinition);
        boolean earlySingletonExposure = isSingletonCurrentlyInCreation(beanName);
        if (earlySingletonExposure) {
            addSingletonFactory(beanName, () -> bean);
        }
        Object exposedObject = bean;
        populateBean(beanName, beanDefinition, bean);
//        exposedObject = initializeBean(exposedObject, beanName);
        if (earlySingletonExposure) {
            Object earlySingletonReference = getSingleton(beanName, false);
            if (earlySingletonReference != null) {
//                if (exposedObject == bean) {
                exposedObject = earlySingletonReference;
//                }
            }
        }
        return exposedObject;
    }

    private Object createBeanInstance(String beanName, BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?> constructorToUse;
        if (beanClass.isInterface()) {
            throw new IdeoException("Specified class '" + beanName + "' is an interface");
        }
        try {
            constructorToUse = beanClass.getDeclaredConstructor((Class<?>[]) null);
            return BeanUtils.instantiateClass(constructorToUse);
        } catch (Exception e) {
            throw new IdeoException("'" + beanName + "',No default constructor found", e);
        }
    }

    private void populateBean(String beanName, BeanDefinition beanDefinition, Object beanInstance) {
        Field[] beanFields = beanDefinition.getBeanClass().getDeclaredFields();
        try {
            for (Field field : beanFields) {
                if (field.getAnnotation(Resource.class) == null) {
                    continue;
                }
                if (!containsBean(field.getName())) {
                    throw new IdeoException("'@Resource' for field '" + field.getClass().getName() + "' can not find");
                }
                field.setAccessible(true);
                field.set(beanInstance, getBean(field.getName()));
            }
        } catch (Exception e) {
            throw new IdeoException("populateBean '" + beanName + "' error", e);
        }
    }

//    private Object initializeBean(Object bean, String beanName) {
//        for (BeanPostProcessor beanProcessor : this.beanPostProcessors) {
//            bean = beanProcessor.postProcessAfterInitialization(bean, beanName);
//        }
//        return bean;
//    }

    private boolean containsBeanDefinition(String name) {
        return beanDefinitionMap.containsKey(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) getBean(StringUtils.lowerFirst(requiredType.getSimpleName()));
    }

    @Override
    public boolean containsBean(String name) {
        return this.containsSingleton(name) || this.containsBeanDefinition(name);
    }
}
