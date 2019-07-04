/**
 * 
 */
package com.synectiks.state.machine.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synectiks.commons.constants.IConsts;
import com.synectiks.commons.constants.IDBConsts;
import com.synectiks.commons.entities.SSMState;
import com.synectiks.commons.entities.SSMState.StatesWrapper;
import com.synectiks.commons.interfaces.IApiController;
import com.synectiks.commons.utils.IUtils;
import com.synectiks.state.machine.managers.SSMManager;
import com.synectiks.state.machine.repositories.SSMStateRepository;
import com.synectiks.state.machine.utils.ICloudUtils;

/**
 * @author Rajesh
 */
@RestController
@RequestMapping(path = IApiController.SSM_API
		+ IApiController.URL_SSM, method = RequestMethod.POST)
@CrossOrigin
public class StateMachineController implements IApiController {

	private static final Logger logger = LoggerFactory
			.getLogger(StateMachineController.class);

	@Autowired
	private SSMStateRepository repository;
	@Autowired
	private SSMManager ssmManager;

	@RequestMapping(path = "listBySsmId", method = RequestMethod.GET)
	public ResponseEntity<Object> listBySsmId(@RequestParam String ssmId) {
		List<SSMState> entities = null;
		try {
			entities = (List<SSMState>) repository.findBySsmId(ssmId);
		} catch (Throwable th) {
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(IUtils.getFailedResponse(th.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

	@Override
	@RequestMapping(path = IConsts.API_FIND_ALL, method = RequestMethod.GET)
	public ResponseEntity<Object> findAll(HttpServletRequest request) {
		List<SSMState> entities = null;
		try {
			entities = (List<SSMState>) repository.findAll();
		} catch (Throwable th) {
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(IUtils.getFailedResponse(th.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

	/**
	 * @param states json state obect in format:
	 * 	{
	 * 		"ssmId": "CloudMigration",
	 * 		"name": "",
	 * 		"parent": "",
	 * 		"target": "",
	 * 		"event": "",
	 * 		"action": "CLASS",
	 * 		"guard": "CLASS",
	 * 		"guardExpress": "",
	 * 		"initial": "true/false",
	 * 		"end": "true/false"
	 * 	}
	 */
	@Override
	@RequestMapping(IConsts.API_CREATE)
	public ResponseEntity<Object> create(@RequestBody ObjectNode states,
			HttpServletRequest request) {
		SSMState entity = null;
		try {
			String user = IUtils.getUserFromRequest(request);
			entity = IUtils.createEntity(states, user, SSMState.class);
			entity = repository.save(entity);
		} catch (Throwable th) {
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(IUtils.getFailedResponse(th.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(entity);
	}

	/**
	 * /createByJson api to create state object by json in format:
	 * @param states states json object
	 * <pre>
	 * {
	 * 	"states": [
	 * 		{
	 * 			"ssmId": "CloudMigration",
	 * 			"name": "",
	 * 			"parent": "",
	 * 			"target": "",
	 * 			"event": "",
	 * 			"action": "CLASS",
	 * 			"guard": "CLASS",
	 * 			"guardExpress": "",
	 * 			"initial": "true/false",
	 * 			"end": "true/false"
	 * 		},
	 * 		{
	 * 			"ssmId": "CloudMigration",
	 * 			"name": "",
	 * 			"parent": "",
	 * 			"target": "",
	 * 			"event": "",
	 * 			"action": "CLASS",
	 * 			"guard": "CLASS",
	 * 			"guardExpress": "",
	 * 			"initial": "true/false",
	 * 			"end": "true/false"
	 * 		}, ...
	 * 	]
	 * }
	 * </pre>
	 * @param request
	 * @return
	 */
	@RequestMapping(IConsts.API_CREATE + "ByJson")
	public ResponseEntity<Object> createByJson(@RequestBody ObjectNode states,
			HttpServletRequest request) {
		StatesWrapper entities = null;
		try {
			String user = IUtils.getUserFromRequest(request);
			entities = IUtils.createEntity(states, user, StatesWrapper.class);
			for (SSMState entity : entities.getStates()) {
				entity = repository.save(entity);
			}
			return ResponseEntity.status(HttpStatus.CREATED).body(entities.getStates());
		} catch (Throwable th) {
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(IUtils.getFailedResponse(th.getMessage()));
		}
	}

	@Override
	@RequestMapping(IConsts.API_FIND_ID)
	public ResponseEntity<Object> findById(@PathVariable("id") String id) {
		SSMState entity = null;
		try {
			entity = repository.findById(id).orElse(null);
		} catch (Throwable th) {
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(IUtils.getFailedResponse(th.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.OK).body(entity);
	}

	@Override
	@RequestMapping(IConsts.API_DELETE_ID)
	public ResponseEntity<Object> deleteById(@PathVariable("id") String id) {
		try {
			repository.delete(id);
		} catch (Throwable th) {
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(IUtils.getFailedResponse(th.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.OK).body("Service removed Successfully");
	}

	@Override
	@RequestMapping(IConsts.API_UPDATE)
	public ResponseEntity<Object> update(@RequestBody ObjectNode entity,
			HttpServletRequest request) {
		SSMState service = null;
		try {
			String user = IUtils.getUserFromRequest(request);
			service = IUtils.createEntity(entity, user, SSMState.class);
			service = repository.save(service);
		} catch (Throwable th) {
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(IUtils.getFailedResponse(th.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.OK).body(service);
	}

	@Override
	@RequestMapping(IConsts.API_DELETE)
	public ResponseEntity<Object> delete(@RequestBody ObjectNode entity) {
		if (!IUtils.isNull(entity.get(IDBConsts.Col_ID))) {
			return deleteById(entity.get(IDBConsts.Col_ID).asText());
		}
		return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
				.body(IUtils.getFailedResponse("Not a valid entity"));
	}

	/**
	 * API to get all states of statemachne by id
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(path = IConsts.API_MACHINE_STATES, method = RequestMethod.GET)
	public ResponseEntity<Object> listStates(
			@RequestParam(name = IConsts.PRM_MACHINE_ID) String id,
			HttpServletRequest request) {
		List<String> entities = null;
		try {
			entities = ssmManager.listAllStates(id);
		} catch (Throwable th) {
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(IUtils.getFailedResponse(th.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(entities);
	}

	/**
	 * API to get the machine id from a string state machine name
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(path = IConsts.API_MACHINE_ID, method = RequestMethod.GET)
	public ResponseEntity<Object> getMachineId(
			@RequestParam(name = IConsts.PRM_MACHINE_ID) String id,
			HttpServletRequest request) {
		String ssmId = null;
		try {
			ssmId = ICloudUtils.getMachineID(IUtils.getUserFromRequest(request), id);
		} catch (Throwable th) {
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(IUtils.getFailedResponse(th.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(ssmId);
	}

	/**
	 * API to set extended variable into statemachine
	 * @param id
	 * @param key
	 * @param cls
	 * @param val
	 * @return
	 */
	@RequestMapping(path = IConsts.API_ADD_EXT_VAR)
	public ResponseEntity<Object> addExtendedVariable(
			@RequestParam(name = IConsts.PRM_MACHINE_ID) String id,
			@RequestParam(name = IConsts.PRM_KEY) String key,
			@RequestParam(name = IConsts.PRM_CLASS, defaultValue = "") String cls,
			@RequestParam(name = IConsts.PRM_VALUE) String val) {
		boolean bRes = true;
		try {
			Object value = null;
			if (!IUtils.isNullOrEmpty(cls) && !cls.equals(String.class.getName())) {
				if (cls.equals(JSONObject.class.getName())) {
					value = new JSONObject(val);
				} else {
					Class<?> clazz = IUtils.getClass(cls);
					value = IUtils.getObjectFromValue(val, clazz);
					logger.info("value is type of: " + clazz.getName());
				}
			} else {
				value = val;
			}
			ssmManager.addExtendedStateVar(id, key, value);
		} catch (Throwable th) {
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(IUtils.getFailedResponse(th.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(bRes);
	}

	/**
	 * API to send event to satate machine
	 * @param id
	 * @param event
	 * @param request
	 * @return
	 */
	@RequestMapping(path = IConsts.API_MACHINE_EVENT)
	public ResponseEntity<Object> sendEvent(
			@RequestParam(name = IConsts.PRM_MACHINE_ID) String id,
			@RequestParam(name = IConsts.PRM_EVENT) String event,
			HttpServletRequest request) {
		boolean bRes = false;
		try {
			logger.info("Send event " + event + ", with vars: "
					+ ssmManager.getExtendedVariables(id));
			bRes = ssmManager.sendEvent(id, event);
		} catch (Throwable th) {
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(IUtils.getFailedResponse(th.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(bRes);
	}

	/**
	 * API to get current state of statemachine
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(path = IConsts.API_MACHINE_STATE)
	public ResponseEntity<Object> getCurrentState(
			@RequestParam(name = IConsts.PRM_MACHINE_ID) String id,
			HttpServletRequest request) {
		String curState = null;
		try {
			curState = ssmManager.getCurrentState(id);
		} catch (Throwable th) {
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(IUtils.getFailedResponse(th.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(curState);
	}

}
