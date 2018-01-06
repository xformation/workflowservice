/**
 * 
 */
package com.synectiks.state.machine.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.statemachine.config.configurers.StateConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

import com.synectiks.commons.entities.SSMState;
import com.synectiks.commons.utils.IUtils;
import com.synectiks.state.machine.SSMAction;
import com.synectiks.state.machine.SSMGuard;
import com.synectiks.state.machine.listeners.SSMListener;
import com.synectiks.state.machine.repositories.SSMStateRepository;

/**
 * @author Rajesh
 */
@Configuration
@EnableStateMachineFactory
public class SynectiksStateMachineConfig
		extends StateMachineConfigurerAdapter<String, String> {

	private static Logger logger = LoggerFactory
			.getLogger(SynectiksStateMachineConfig.class);

	@Autowired
	private SSMStateRepository stateRepository;

	@Override
	public void configure(StateMachineConfigurationConfigurer<String, String> config)
			throws Exception {
		logger.info("StateMachine config: 3, StateMachineConfigurationConfigurer");
		config.withConfiguration()
				.autoStartup(true)
				.listener(new SSMListener());
	}

	@Override
	public void configure(StateMachineStateConfigurer<String, String> states)
			throws Exception {
		logger.info("StateMachine config: 4, StateMachineStateConfigurer");
		// load all states
		Iterable<SSMState> ssmStates = stateRepository.findAll();
		StateConfigurer<String, String> withStates = states.withStates();
		if (!IUtils.isNull(ssmStates) && ssmStates.iterator().hasNext()) {
			for (SSMState state : ssmStates) {
				SSMState parent = getParent(state.getParent());
				if (IUtils.isNull(parent)) {
					if (state.isEnd()) {
						logger.info("End: " + state);
						withStates.end(state.getName());
					} else {
						logger.info("initial: " + state);
						withStates.initial(state.getName(),
								SSMAction.build(state.getAction(),
										state.getName(), state.getEvent()))
						.states(getStates(ssmStates));
					}
				} else {
					if (state.isEnd()) {
						logger.info("End: " + state);
						withStates.end(state.getName());
					}
				}
			}
		} else {
			logger.error("No defined states found in repository.");
			withStates.initial("Start");
			withStates.end("End");
		}
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<String, String> transitions)
			throws Exception {
		logger.info("StateMachine config: 5, StateMachineTransitionConfigurer");
		// load all states
		Iterable<SSMState> ssmStates = stateRepository.findAll();
		ExternalTransitionConfigurer<String, String> withTrans = transitions
				.withExternal();
		boolean bFirst = true;
		if (!IUtils.isNull(ssmStates) && ssmStates.iterator().hasNext()) {
			// Set state transitions
			for (SSMState state : ssmStates) {
				logger.info("State: " + state);
				if (!bFirst) {
					withTrans = withTrans.and().withExternal();
				} else {
					bFirst = false;
				}
				withTrans.source(state.getName());
				if (!IUtils.isNull(state.getTarget())) {
					withTrans.target(state.getTarget());
				}
				if (!IUtils.isNull(state.getEvent())) {
					withTrans.event(state.getEvent());
				}
				if (!IUtils.isNull(state.getAction())) {
					withTrans.action(SSMAction.build(state.getAction(),
							state.getName(), state.getEvent()));
				}
				if (!IUtils.isNull(state.getGuard())) {
					withTrans.guard(SSMGuard.build(state.getGuard(),
							state.getName(), state.getEvent()));
				}
				if (!IUtils.isNull(state.getGuardExpress())) {
					withTrans.guardExpression(state.getGuardExpress());
				}
			}
		} else {
			logger.error("No defined states found in repository.");
			withTrans.source("Start")
				.event("Start")
				.action(SSMAction.build("com.synectiks.state.machine.SSMAction", "Start", "Start"))
				.target("End");
		}
	}

	/**
	 * Method to load a state by name
	 * @param parent
	 * @return
	 */
	private SSMState getParent(String parent) {
		if (!IUtils.isNullOrEmpty(parent)) {
			return stateRepository.findByName(parent);
		}
		return null;
	}

	private Set<String> getStates(Iterable<SSMState> ssmStates) {
		Set<String> set = new HashSet<>();
		ssmStates.forEach(state -> {
			set.add(state.getName());
		});
		return set;
	}

	@Bean
	public StateMachinePersister<String, String, String> stateMachinePersister() {
		return new DefaultStateMachinePersister<String, String, String>(stateMachinePersist());
	}

	@Bean
	public StateMachinePersist<String, String, String> stateMachinePersist() {
		final HashMap<String, StateMachineContext<String, String>> contexts = new HashMap<>();
		return new StateMachinePersist<String, String, String>() {

			@Override
			public void write(StateMachineContext<String, String> context,
					String contextObj) throws Exception {
				contexts.put(contextObj, context);
			}

			@Override
			public StateMachineContext<String, String> read(String contextObj)
					throws Exception {
				return contexts.get(contextObj);
			}
		};
	}
}
