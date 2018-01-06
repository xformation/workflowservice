package com.synectiks.state.machine;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

import com.synectiks.commons.utils.IUtils;

/**
 * Sample guard class
 * @author Rajesh
 */
public class SSMGuard extends BaseGuard<String, String> {

	public SSMGuard() {
		super();
	}

	public SSMGuard(String name, String state, String event) {
		super(name, state, event);
	}

	@Override
	public boolean evaluate(StateContext<String, String> context) {
		logger.info(name + " Ctx: " + context);
		logger.info(name + " Empty Guard(" + state + ", " + event + ") defined");
		return true;
	}

	public static Guard<String, String> build(String guard, String state, String event) {
		if (!IUtils.isNullOrEmpty(guard) && guard.contains(".")) {
			try {
				Class<?> actClass = Class.forName(guard);
				Object inst = actClass.newInstance();
				if (inst instanceof BaseGuard) {
					@SuppressWarnings("unchecked")
					BaseGuard<String, String> base = ((BaseGuard<String, String>) inst);
					base.setName(guard);
					base.setState(state);
					base.setEvent(event);
					return base;
				}
			} catch (Exception e) {
				logger.warn("Failed to load guard class: " + e.getMessage());
			}
		}
		return new SSMGuard(guard, state, event);
	}
}
