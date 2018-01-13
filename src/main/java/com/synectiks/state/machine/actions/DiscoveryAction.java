/**
 * 
 */
package com.synectiks.state.machine.actions;

import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;

import com.synectiks.state.machine.BaseAction;

/**
 * @author Rajesh
 */
public class DiscoveryAction extends BaseAction<String, String> {

	public DiscoveryAction() {
	}

	/**
	 * @param name
	 * @param state
	 * @param event
	 */
	public DiscoveryAction(String name, String state, String event) {
		super(name, state, event);
	}

	@Override
	public void execute(StateContext<String, String> context) {
		ExtendedState exState = context.getExtendedState();
		logger.info("ST Ex vars: " + exState.getVariables());
	}

}
