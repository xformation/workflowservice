/**
 * 
 */
package com.synectiks.state.machine.config;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.StateMachineSystemConstants;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineBuilder.Builder;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.statemachine.config.configurers.StateConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

import com.synectiks.commons.entities.SSMState;
import com.synectiks.commons.entities.SSMachine;
import com.synectiks.commons.utils.IUtils;
import com.synectiks.state.machine.SSMAction;
import com.synectiks.state.machine.SSMGuard;
import com.synectiks.state.machine.listeners.SSMListener;
import com.synectiks.state.machine.repositories.SSMRepository;
import com.synectiks.state.machine.repositories.SSMStateRepository;
import com.synectiks.state.machine.utils.ICloudUtils;
import com.synectiks.state.machine.utils.StateMachineContextConverter;

/**
 * @author Rajesh
 */
@Configuration
public class SynectiksStateMachineConfig {

	private static Logger logger = LoggerFactory
			.getLogger(SynectiksStateMachineConfig.class);
	private static StateMachineContextConverter converter = new StateMachineContextConverter();

	@Autowired
	private SSMStateRepository stateRepository;
	@Autowired
	private SSMRepository ssmRepository;

	@Bean
	public BeanFactory beanFactory() {
		StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();
		beanFactory.addBean(StateMachineSystemConstants.TASK_EXECUTOR_BEAN_NAME,
				new SyncTaskExecutor());
		beanFactory.addBean("taskScheduler", new ConcurrentTaskScheduler());
		logger.info("BeanFactory created");
		return beanFactory;
	}

	private Iterable<SSMState> loadMachineStates(String machineId) {
		Iterable<SSMState> ssmStates = stateRepository
				.findBySsmId(ICloudUtils.getSsmID(machineId));
		return ssmStates;
	}

	public StateMachine<String, String> buildStateMachine(String machineId)
			throws Exception {
		Builder<String, String> builder = StateMachineBuilder.builder();
		setConfiguration(builder, machineId);
		Iterable<SSMState> ssmStates = loadMachineStates(machineId);
		setStates(builder, ssmStates);
		setTransitions(builder, ssmStates);
		logger.info("Machine builded");
		return builder.build();
	}

	private void setConfiguration(Builder<String, String> builder, String machineId)
			throws Exception {
		builder.configureConfiguration().withConfiguration().autoStartup(true)
				.listener(new SSMListener()).machineId(machineId)
				.beanFactory(beanFactory());
	}

	private void setStates(Builder<String, String> builder, Iterable<SSMState> ssmStates)
			throws Exception {
		StateConfigurer<String, String> withStates = builder.configureStates()
				.withStates();
		if (!IUtils.isNull(ssmStates) && ssmStates.iterator().hasNext()) {
			for (SSMState state : ssmStates) {
				SSMState parent = getParent(state.getParent());
				if (IUtils.isNull(parent)) {
					if (state.isEnd()) {
						logger.info("End: " + state);
						withStates.end(state.getName());
					} else if (state.isInitial()) {
						logger.info("initial: " + state);
						withStates
								.initial(state.getName(),
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
		}
	}

	private void setTransitions(Builder<String, String> builder,
			Iterable<SSMState> ssmStates) throws Exception {
		ExternalTransitionConfigurer<String, String> withTrans = builder
				.configureTransitions().withExternal();
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
					withTrans.action(SSMAction.build(state.getAction(), state.getName(),
							state.getEvent()));
				}
				if (!IUtils.isNull(state.getGuard())) {
					withTrans.guard(SSMGuard.build(state.getGuard(), state.getName(),
							state.getEvent()));
				}
				if (!IUtils.isNull(state.getGuardExpress())) {
					withTrans.guardExpression(state.getGuardExpress());
				}
			}
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
		return new DefaultStateMachinePersister<String, String, String>(
				stateMachinePersist());
	}

	@Bean
	public StateMachinePersist<String, String, String> stateMachinePersist() {

		return new StateMachinePersist<String, String, String>() {

			@Override
			public void write(StateMachineContext<String, String> context,
					String contextObj) throws Exception {
				SSMachine machine = ssmRepository.findBySsmId(contextObj);
				if (IUtils.isNull(machine)) {
					machine = new SSMachine();
					machine.setSsmId(contextObj);
				}
				machine.setSsmContext(converter.convertToDatabaseColumn(context));
				logger.info("SSMachine: ", machine);
				ssmRepository.save(machine);
			}

			@SuppressWarnings("unchecked")
			@Override
			public StateMachineContext<String, String> read(String contextObj)
					throws Exception {
				SSMachine machine = ssmRepository.findBySsmId(contextObj);
				if (!IUtils.isNull(machine) && !IUtils.isNullOrEmpty(machine.getSsmContext())) {
					return converter.convertToEntityAttribute(machine.getSsmContext());
				}
				return null;
			}
		};
	}
}
