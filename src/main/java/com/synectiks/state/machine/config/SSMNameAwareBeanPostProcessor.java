/**
 * 
 */
package com.synectiks.state.machine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.statemachine.support.AbstractStateMachine;
import org.springframework.stereotype.Component;

/**
 * @author Rajesh
 */
@Component
public class SSMNameAwareBeanPostProcessor implements BeanPostProcessor {

	private static final Logger logger = LoggerFactory
			.getLogger(SSMNameAwareBeanPostProcessor.class);

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		//logger.info(beanName + " -> Bean of(Before): " + bean.getClass().getName());
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		//logger.info(beanName + " -> Bean of(After): " + bean.getClass().getName());
		if (bean instanceof AbstractStateMachine<?, ?> && bean instanceof BeanNameAware) {
			logger.info(beanName + " -> Bean of(After): " + bean.getClass().getName());
			logger.info("SSM-ID: " + ((AbstractStateMachine<?, ?>) bean).getId());
			((BeanNameAware) bean).setBeanName(beanName);
		}
		return bean;
	}

}
