/**
 * 
 */
package com.synectiks.state.machine;

import org.springframework.statemachine.action.Action;

/**
 * @author Rajesh
 */
public abstract class BaseAction<S, E> extends SSMBase<S, E> implements Action<S, E> {

	public BaseAction() {
		super();
	}

	public BaseAction(String name, S state, E event) {
		super(name, state, event);
	}
}
