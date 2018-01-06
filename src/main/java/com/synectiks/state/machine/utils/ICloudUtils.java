/**
 * 
 */
package com.synectiks.state.machine.utils;

import org.springframework.statemachine.ExtendedState;

import com.synectiks.commons.constants.IConsts;
import com.synectiks.commons.utils.IUtils;
import com.synectiks.state.machine.managers.SSMManager;

/**
 * @author Rajesh
 */
public interface ICloudUtils {

	String OAK_ROOT = "/Synectiks";

	/**
	 * Method add key, value into extended state
	 * @param ssmManager
	 * @param ssmId 
	 * @param key
	 * @param value
	 */
	static void addExtendedVariable(SSMManager ssmManager, String ssmId, String key,
			String value) {
		if (!IUtils.isNull(ssmManager) && !IUtils.isNull(key) && !IUtils.isNull(value)) {
			try {
				ssmManager.addExtendedStateVar(ssmId, key, value);
			} catch (Exception e) {
				IUtils.logger.error("ICloudUtils: " + e.getMessage());
			};
		}
	}

	/**
	 * Add subscription id in extended state variables
	 * @param ssm
	 */
	static void addSubscriptionId(SSMManager ssmManager, String ssmId) {
		addExtendedVariable(ssmManager, ssmId, IConsts.SUBSCRIPTION_ID,
				IUtils.extractSubscriptionId(ssmId));
	}

	/**
	 * Method to crate jcr node path
	 * @param exStates
	 * @param addEvent
	 * @return
	 */
	static String getNodePath(ExtendedState exStates, boolean addEvent) {
		StringBuilder sb = new StringBuilder();
		if (!IUtils.isNull(exStates)) {
			sb.append(OAK_ROOT);
			String val = exStates.get(IConsts.USERNAME, String.class);
			sb.append(IUtils.isNullOrEmpty(val) ? "" : "/" + val);
			val = exStates.get(IConsts.SUBSCRIPTION_ID, String.class);
			sb.append(IUtils.isNullOrEmpty(val) ? "" : "/" + val);
			if (addEvent) {
				val = exStates.get(IConsts.EVENT, String.class);
				sb.append(IUtils.isNullOrEmpty(val) ? "" : "/" + val);
			}
		}
		return sb.toString();
	}

}
