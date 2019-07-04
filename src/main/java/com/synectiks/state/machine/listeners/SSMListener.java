/**
 * 
 */
package com.synectiks.state.machine.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

/**
 * @author Rajesh
 */
@Component
public class SSMListener extends StateMachineListenerAdapter<String, String> {

	private static Logger logger = LoggerFactory.getLogger(SSMListener.class);

	@Override
	public void stateChanged(State<String, String> from, State<String, String> to) {
		super.stateChanged(from, to);
		logger.debug("State changed \nfrom: " + from + "\nto:" + to);
	}

	@Override
	public void stateEntered(State<String, String> state) {
		super.stateEntered(state);
		logger.debug("State: " + state + " started.");
	}

	@Override
	public void stateExited(State<String, String> state) {
		super.stateExited(state);
		logger.debug("State: " + state + " exited.");
	}

	@Override
	public void eventNotAccepted(Message<String> event) {
		super.eventNotAccepted(event);
		logger.warn("Event: " + event + " not accepted.");
	}

	@Override
	public void transition(Transition<String, String> transition) {
		super.transition(transition);
		logger.debug("Transition: " + transition.getKind() +
				", src: " + (transition.getSource() != null ? transition.getSource().getId() : "null") +
				", target: " + (transition.getTarget() != null ? transition.getTarget().getId() : "null") +
				", actions: " + (transition.getActions() != null ? transition.getActions() : "null") + " going on.");
	}

	@Override
	public void transitionStarted(Transition<String, String> transition) {
		super.transitionStarted(transition);
		logger.debug("Transition start: " + transition.getKind() +
				", src: " + (transition.getSource() != null ? transition.getSource().getId() : "null") +
				", target: " + (transition.getTarget() != null ? transition.getTarget().getId() : "null") +
				", actions: " + (transition.getActions() != null ? transition.getActions() : "null") + " going on.");
	}

	@Override
	public void transitionEnded(Transition<String, String> transition) {
		super.transitionEnded(transition);
		logger.debug("Transition end: " + transition.getKind() +
				", src: " + (transition.getSource() != null ? transition.getSource().getId() : "null") +
				", target: " + (transition.getTarget() != null ? transition.getTarget().getId() : "null") +
				", actions: " + (transition.getActions() != null ? transition.getActions() : "null") + " going on.");
	}

	@Override
	public void stateMachineStarted(StateMachine<String, String> stateMachine) {
		super.stateMachineStarted(stateMachine);
		logger.debug("StateMachine: " + stateMachine.getId() + " started.");
	}

	@Override
	public void stateMachineStopped(StateMachine<String, String> stateMachine) {
		super.stateMachineStopped(stateMachine);
		logger.debug("StateMachine: " + stateMachine.getId() + " ended.");
	}

	@Override
	public void stateMachineError(StateMachine<String, String> stateMachine,
			Exception exception) {
		super.stateMachineError(stateMachine, exception);
		logger.error("StateMachine: " + stateMachine.getId() + " error.", exception);
	}

	@Override
	public void extendedStateChanged(Object key, Object value) {
		super.extendedStateChanged(key, value);
		logger.debug("ExtendedState changed key: " + key + ", value: " + value);
	}

}
