package com.synectiks.state.machine;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import com.synectiks.commons.utils.IUtils;

/**
 * Sample action class
 * @author Rajesh
 */
public class SSMAction extends BaseAction<String, String> {

	public SSMAction() {
		super();
	}

	public SSMAction(String name, String state, String event) {
		super(name, state, event);
	}

	@Override
	public void execute(StateContext<String, String> context) {
		logger.info(name + " Context: " + context);
		logger.info(name + " Empty action(" + state + ", " + event + ") defined");
	}

	public static Action<String, String> build(
			String action, String state, String event) {
		if (!IUtils.isNullOrEmpty(action) && action.contains(".")) {
			try {
				Class<?> actClass = Class.forName(action);
				Object inst = actClass.newInstance();
				if (inst instanceof BaseAction) {
					@SuppressWarnings("unchecked")
					BaseAction<String, String> base = ((BaseAction<String, String>) inst);
					base.setName(action);
					base.setState(state);
					base.setEvent(event);
					return base;
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.warn("Failed to load action class: " + e.getMessage());
			}
		}
		return new SSMAction(action, state, event);
	}
	
}
