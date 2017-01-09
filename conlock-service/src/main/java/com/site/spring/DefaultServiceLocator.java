package com.site.spring;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.site.annotation.Load;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DefaultServiceLocator {
    private static Map<String, ClassPathXmlApplicationContext> contexts = new ConcurrentHashMap();

    public DefaultServiceLocator() {
    }

    protected static String getSpringConfigFileName(Class<?> clz) {
        String currentLocatorName = clz.getSimpleName();
        StringBuffer configFileName = new StringBuffer("");
        int i = 0;

        for(int j = currentLocatorName.length(); i < j; ++i) {
            char tempChar = currentLocatorName.charAt(i);
            if(Character.isLowerCase(tempChar)) {
                configFileName.append(tempChar);
            } else if(Character.isUpperCase(currentLocatorName.charAt(i))) {
                configFileName.append("_");
                configFileName.append(Character.toLowerCase(tempChar));
            }
        }

        configFileName.append(".xml");
        return configFileName.substring(1, configFileName.length()).toString();
    }

    public static ApplicationContext getApplicationContext(Class<?> clz) {
        String xmlName = getSpringConfigFileName(clz);
        return getApplicationContext(xmlName);
    }

    public static ApplicationContext getApplicationContext(String xmlName) {
        ClassPathXmlApplicationContext context = (ClassPathXmlApplicationContext)contexts.get(xmlName);
        if(context == null && !StringUtils.isBlank(xmlName)) {
            ArrayList beanList = new ArrayList();
            context = new ClassPathXmlApplicationContext();
            context.setConfigLocation(xmlName);
            context.addBeanFactoryPostProcessor(createProcessor(beanList));
            contexts.put(xmlName, context);
            context.refresh();
            Iterator i$ = beanList.iterator();

            while(i$.hasNext()) {
                Object bean = i$.next();
                resolveInjects(bean);
            }
        }

        return context;
    }

    private static BeanFactoryPostProcessor createProcessor(final List<Object> beanList) {
        return new BeanFactoryPostProcessor() {
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                beanFactory.addBeanPostProcessor(new BeanPostProcessor() {
                    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                        beanList.add(bean);
                        return bean;
                    }

                    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                        return bean;
                    }
                });
            }
        };
    }

    private static void resolveInjects(Object bean) throws BeansException {
        Class clazz = bean.getClass();
        Field[] fileds = clazz.getDeclaredFields();
        Field[] arr$ = fileds;
        int len$ = fileds.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Field field = arr$[i$];
            Load injectAnno = (Load)field.getAnnotation(Load.class);
            if(injectAnno != null) {
                Class locator = injectAnno.locator();
                ApplicationContext context = getApplicationContext(locator);
                if(context != null) {
                    Object target = null;
                    String name = injectAnno.name();
                    if("".equals(name.trim())) {
                        target = context.getBean(field.getType());
                    } else {
                        target = context.getBean(name, field.getType());
                    }

                    if(target != null) {
                        try {
                            boolean e = field.isAccessible();
                            if(!e) {
                                field.setAccessible(true);
                            }

                            field.set(bean, target);
                            if(!e) {
                                field.setAccessible(false);
                            }
                        } catch (IllegalAccessException var13) {
                            throw new RuntimeException("resolve inject error", var13);
                        }
                    }
                }
            }
        }

    }
}
