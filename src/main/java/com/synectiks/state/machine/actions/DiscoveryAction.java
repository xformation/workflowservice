/**
 * 
 */
package com.synectiks.state.machine.actions;

import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.web.client.RestTemplate;

import com.synectiks.commons.constants.IConsts;
import com.synectiks.commons.entities.oak.OakFileNode;
import com.synectiks.commons.utils.IUtils;
import com.synectiks.state.machine.BaseAction;
import com.synectiks.state.machine.utils.ICloudUtils;

/**
 * @author Rajesh
 */
public class DiscoveryAction extends BaseAction<String, String> {

	private RestTemplate rest;

	public DiscoveryAction() {
	}

	/**
	 * @param name
	 * @param state
	 * @param event
	 */
	public DiscoveryAction(String name, String state, String event) {
		super(name, state, event);
	}

	@Override
	public void execute(StateContext<String, String> context) {
		ExtendedState exState = context.getExtendedState();
		// logger.info("ST Ex vars: " + exState.getVariables());
		JSONObject oakNode = exState.get(IConsts.OAK_NODE, JSONObject.class);
		String url = exState.get(IConsts.OAK_URL, String.class);
		logger.info("Saving oak node " + oakNode);
		if (!IUtils.isNull(oakNode)) {
			String nodePath = ICloudUtils.getNodePath(exState, false);
			url += "/createNode";
			try {
				String res = IUtils.sendPostRestRequest(getTemplate(), url, null,
						String.class, IUtils.getParamMap(nodePath, oakNode, this.event),
						MediaType.APPLICATION_FORM_URLENCODED);
				logger.info("Result: " + res);
				Map<?, ?> nodes = exState.get(IConsts.OAK_FILE_NODE, Map.class);
				saveFileNodes(url, nodePath + "/" + this.event, nodes);
			} catch (Exception e) {
				context.getStateMachine().setStateMachineError(e);
				logger.error(e.getMessage(), e);
			}
		} else {
			context.getStateMachine()
					.setStateMachineError(new Exception("No node data to store"));
		}
	}

	private RestTemplate getTemplate() {
		if (IUtils.isNull(rest)) {
			rest = new RestTemplate();
		}
		return rest;
	}

	/**
	 * Method to save oak file nodes into jcr repository
	 * @param url
	 * @param parentPath
	 * @param nodes
	 */
	private void saveFileNodes(String url, String parentPath, Map<?, ?> nodes) {
		nodes.entrySet().forEach(entry -> {
			logger.info(entry.getKey() + " => [" + entry.getValue().getClass().getName()
					+ "]");
			OakFileNode node = null;
			if (!IUtils.isNull(entry.getValue()) && entry.getValue() instanceof String) {
				node = IUtils.getObjectFromValue(entry.getValue().toString(),
						OakFileNode.class);
			}
			String res = IUtils.sendPostRestRequest(getTemplate(), url, null,
					String.class,
					IUtils.getParamMap(parentPath, node, entry.getKey().toString()),
					MediaType.APPLICATION_FORM_URLENCODED);
			logger.info("Result: " + res);
		});
	}

}
