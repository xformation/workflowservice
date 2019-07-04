/**
 * 
 */
package com.synectiks.state.machine;

import org.springframework.statemachine.guard.Guard;

/**
 * @author Rajesh
 */
public abstract class BaseGuard<S, E> extends SSMBase<S, E> implements Guard<S, E> {

	public BaseGuard() {
		super();
	}

	public BaseGuard(String name, S state, E event) {
		super(name, state, event);
	}
	
}
