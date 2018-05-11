/**
 * 
 */
package com.synectiks.state.machine.utils;

import com.synectiks.commons.constants.IConsts;
import com.synectiks.commons.utils.IUtils;
import com.synectiks.state.machine.managers.SSMManager;

/**
 * @author Rajesh
 */
public interface ICloudUtils {

	/**
	 * Method returns ssmId from machine id
	 * @param machineId
	 * @return
	 */
	static String getSsmID(String machineId) {
		if (!IUtils.isNullOrEmpty(machineId)) {
			if (machineId.contains(":")) {
				return machineId.substring(0, machineId.indexOf(":"));
			}
		}
		return null;
	}

	/**
	 * Method to create a machine id with user and ssmId
	 * @param user
	 * @param id
	 * @return
	 */
	static String getMachineID(String user, String id) {
		if (!IUtils.isNullOrEmpty(id)) {
			if (id.contains(":")) {
				return id;
			}
		}
		return user + ":" + id;
	}

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

}
