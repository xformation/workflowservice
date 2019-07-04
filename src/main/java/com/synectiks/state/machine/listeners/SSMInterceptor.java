/**
 * 
 */
package com.synectiks.state.machine.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;

/**
 * @author Rajesh
 *
 */
public class SSMInterceptor extends StateMachineInterceptorAdapter<String, String> {

	private static final Logger logger = LoggerFactory.getLogger(SSMInterceptor.class);

	@Override
	public Message<String> preEvent(Message<String> message,
			StateMachine<String, String> stateMachine) {
		logger.debug("preEvent: " + message.getPayload());
		return super.preEvent(message, stateMachine);
	}

	@Override
	public void preStateChange(State<String, String> state, Message<String> message,
			Transition<String, String> transition,
			StateMachine<String, String> stateMachine) {
		logger.debug("preStateChange: " + message.getPayload());
		super.preStateChange(state, message, transition, stateMachine);
	}

	@Override
	public void postStateChange(State<String, String> state, Message<String> message,
			Transition<String, String> transition,
			StateMachine<String, String> stateMachine) {
		logger.debug("postStateChange: " + message.getPayload());
		super.postStateChange(state, message, transition, stateMachine);
	}

	@Override
	public StateContext<String, String> preTransition(
			StateContext<String, String> stateContext) {
		logger.debug("preTransition");
		return super.preTransition(stateContext);
	}

	@Override
	public StateContext<String, String> postTransition(
			StateContext<String, String> stateContext) {
		logger.debug("postTransition");
		return super.postTransition(stateContext);
	}

	@Override
	public Exception stateMachineError(StateMachine<String, String> stateMachine,
			Exception exception) {
		logger.debug("stateMachineError: " + exception.getMessage());
		return super.stateMachineError(stateMachine, exception);
	}

}
