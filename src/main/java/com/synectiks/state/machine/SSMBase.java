/**
 * 
 */
package com.synectiks.state.machine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rajesh
 */
public abstract class SSMBase<S, E> {

	protected static final Logger logger = LoggerFactory.getLogger(SSMBase.class);

	protected String name;
	protected S state;
	protected E event;

	public SSMBase() {
	}

	public SSMBase(String name, S state, E event) {
		this.name = name;
		this.state = state;
		this.event = event;
	}

	public String getName() {
		return name;
	}

	public S getState() {
		return state;
	}

	public E getEvent() {
		return event;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setState(S state) {
		this.state = state;
	}

	public void setEvent(E event) {
		this.event = event;
	}

	@Override
	public String toString() {
		return "{\"" + (name != null ? "name\": \"" + name + "\", " : "")
				+ (state != null ? "state\": \"" + state + "\", " : "")
				+ (event != null ? "event\": \"" + event + "\" " : "") + "}";
	}
}
