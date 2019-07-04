package com.synectiks.state;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import com.synectiks.commons.utils.IUtils;

/**
 * @author Rajesh
 */
@SpringBootApplication
@ComponentScan("com.synectiks")
public class SynectiksSSMApplication {

	private static final Logger logger = LoggerFactory
			.getLogger(SynectiksSSMApplication.class);

	private static ConfigurableApplicationContext ctx;

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		ctx = SpringApplication
				.run(SynectiksSSMApplication.class, args);
		for (String bean : ctx.getBeanDefinitionNames()) {
			logger.info("Beans: " + bean);
		}
	}

	/**
	 * Utility method to get bean from spring context.
	 * @param cls
	 * @return
	 */
	public static <T> T getBean(Class<T> cls) {
		return ctx.getBean(cls);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void setIndexAndMapping() {
		populateEnvPropertiesInSystem();
	}

	private void populateEnvPropertiesInSystem() {
		AbstractEnvironment absEnv = (AbstractEnvironment) env;
		absEnv.getPropertySources().forEach(pSrc -> {
			//logger.info("Source----->: " + pSrc.getName()
			//		+ "\n\tcls: " + pSrc.getSource().getClass().getName());
			if (pSrc instanceof MapPropertySource) {
				Map<String, Object> propMap = ((MapPropertySource) pSrc).getSource();
				for (String key : propMap.keySet()) {
					Object val = propMap.get(key);
					//logger.info("Val class: "
					//		+ (val != null ? val.getClass().getName() : "null"));
					if (!IUtils.isNull(val) && (val instanceof String ||
							val instanceof Number || val instanceof OriginTrackedValue)) {
						logger.info("Setting: " + key);
						System.setProperty(key, String.valueOf(val));
					}
				}
			}
		});
	}
}
