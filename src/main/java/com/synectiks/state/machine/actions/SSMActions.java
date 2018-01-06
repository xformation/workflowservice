/**
 * 
 */
package com.synectiks.state.machine.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.annotation.OnEventNotAccepted;
import org.springframework.statemachine.annotation.OnExtendedStateChanged;
import org.springframework.statemachine.annotation.OnStateChanged;
import org.springframework.statemachine.annotation.OnStateEntry;
import org.springframework.statemachine.annotation.OnStateExit;
import org.springframework.statemachine.annotation.OnStateMachineError;
import org.springframework.statemachine.annotation.OnStateMachineStart;
import org.springframework.statemachine.annotation.OnStateMachineStop;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.OnTransitionEnd;
import org.springframework.statemachine.annotation.OnTransitionStart;
import org.springframework.statemachine.annotation.WithStateMachine;

/**
 * @author Rajesh
 */
@WithStateMachine(name = "ADMIN:1")
public class SSMActions {

	private static final Logger logger = LoggerFactory.getLogger(SSMActions.class);

	@OnTransition(target = {"Start", "End"})
	public void onTrasition() {
		logger.info("State Machine onTransition");
	}
	@OnEventNotAccepted
	public void onEventNotAccepted() {
		logger.info("State Machine onEventNotAccepted");
	}
	@OnExtendedStateChanged
	public void onExtendedStateChanged() {
		logger.info("State Machine onExtendedStateChanged");
	}
	@OnStateChanged
	public void onStateChanged() {
		logger.info("State Machine onStateChanged");
	}
	@OnStateEntry
	public void onStateEntry() {
		logger.info("State Machine onStateEntry");
	}
	@OnStateExit
	public void onStateExit() {
		logger.info("State Machine onStateExit");
	}
	@OnStateMachineError
	public void onStateMachineError() {
		logger.info("State Machine onStateMachineError");
	}
	@OnStateMachineStart
	public void onStateMachineStart() {
		logger.info("State Machine onStateMachineStart");
	}
	@OnStateMachineStop
	public void onStateMachineStop() {
		logger.info("State Machine onStateMachineStop");
	}
	@OnTransitionStart
	public void onTransitionStart() {
		logger.info("State Machine onTransitionStart");
	}
	@OnTransitionEnd
	public void onTransitionEnd() {
		logger.info("State Machine onTransitionEnd");
	}

}
