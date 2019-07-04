/**
 * 
 */
package com.synectiks.state.machine.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.access.StateMachineAccess;
import org.springframework.statemachine.access.StateMachineFunction;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;

import com.synectiks.commons.utils.IUtils;
import com.synectiks.state.machine.config.SynectiksStateMachineConfig;
import com.synectiks.state.machine.listeners.SSMInterceptor;

/**
 * @author Rajesh
 */
@Component
public class SSMManager {

	private static Logger logger = LoggerFactory.getLogger(SSMManager.class);

	@Autowired
	private SynectiksStateMachineConfig ssmConfig;
	@Autowired
	private StateMachinePersister<String, String, String> smPersister;

	private final Map<String, StateMachine<String, String>> machines = new HashMap<>();

	public List<String> listAllStates(String ssmId) throws Exception {
		StateMachine<String, String> machine = getStateMachine(ssmId);
		List<String> lst = new ArrayList<>();
		machine.getStates().forEach(item -> {
			lst.add(item.getId());
		});
		return lst;
	}

	public String getCurrentState(String ssmId) throws Exception {
		StateMachine<String, String> machine = getStateMachine(ssmId);
		String curState = null;
		if (!IUtils.isNull(machine)) {
			if (!IUtils.isNull(machine.getState())) {
				curState = machine.getState().getId();
			} else {
				throw new Exception("Machine in INVALID state");
			}
		} else {
			throw new Exception("No machine exists");
		}
		return curState;
	}

	public StateMachine<String, String> getStateMachine(String machineId)
			throws Exception {
		StateMachine<String, String> machine = machines.get(machineId);
		try {
			if (IUtils.isNull(machine)) {
				machine = ssmConfig.buildStateMachine(machineId);
				if (!IUtils.isNull(machine)) {
					if (machine instanceof BeanNameAware) {
						((BeanNameAware) machine).setBeanName(machineId);
					}
					// Add interceptor
					machine.getStateMachineAccessor().doWithRegion(
							new StateMachineFunction<StateMachineAccess<String, String>>() {

								@Override
								public void apply(
										StateMachineAccess<String, String> function) {
									function.addStateMachineInterceptor(
											new SSMInterceptor());
								}
							});
					logger.info(machineId + " started.");
					machine.start();
					persistMachine(machine);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		}
		if (!IUtils.isNull(machine)) {
			// finally restore machine state from persister
			machine = smPersister.restore(machine, machineId);
			logger.info("Machine current state: " + machine.getState());
		} else {
			throw new Exception("No State Machine for machineId: " + machineId);
		}
		return machine;
	}

	private void persistMachine(StateMachine<String, String> machine) throws Exception {
		// set machine into cache
		machines.put(machine.getId(), machine);
		// persist machine state
		smPersister.persist(machine, machine.getId());
	}

	public boolean sendEvent(String ssmId, String event) throws Exception {
		StateMachine<String, String> machine = getStateMachine(ssmId);
		logger.info("Machine current state: " + machine.getState());
		boolean res = machine.sendEvent(event);
		if (!res && machine.hasStateMachineError()) {
			logger.error("Send " + event + " failed see previous log for details");
		}
		persistMachine(machine);
		return res;
	}

	public void addExtendedStateVar(String ssmId, String key, Object value)
			throws Exception {
		StateMachine<String, String> machine = getStateMachine(ssmId);
		machine.getExtendedState().getVariables().put(key, value);
		logger.info("added: " + key + " => " + value);
		persistMachine(machine);
	}

	public void addExtendedStateVar(String ssmId, Map<String, Object> vars)
			throws Exception {
		StateMachine<String, String> machine = getStateMachine(ssmId);
		machine.getExtendedState().getVariables().putAll(vars);
		persistMachine(machine);
	}

	public Map<Object, Object> getExtendedVariables(String ssmId) throws Exception {
		return getStateMachine(ssmId).getExtendedState().getVariables();
	}

	public ExtendedState getExtendedState(String ssmId) throws Exception {
		return getStateMachine(ssmId).getExtendedState();
	}

}
