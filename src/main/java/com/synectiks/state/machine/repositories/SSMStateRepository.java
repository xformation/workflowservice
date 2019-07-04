/**
 * 
 */
package com.synectiks.state.machine.repositories;

import org.springframework.stereotype.Repository;

import com.synectiks.commons.constants.IDBConsts;
import com.synectiks.commons.entities.SSMState;
import com.synectiks.commons.utils.IUtils;
import com.synectiks.schemas.repositories.DynamoDbRepository;

/**
 * @author Rajesh
 */
@Repository
public class SSMStateRepository extends DynamoDbRepository<SSMState, String> {

	public SSMStateRepository() {
		super(SSMState.class);
	}

	public SSMState findByName(String name) {
		Iterable<SSMState> states = super.findByKeyValue(IDBConsts.Col_NAME, name);
		if (!IUtils.isNull(states) && !IUtils.isNull(states.iterator())
				&& states.iterator().hasNext()) {
			return states.iterator().next();
		}
		return null;
	}

	public Iterable<SSMState> findBySsmId(String id) {
		Iterable<SSMState> states = super.findByKeyValue(IDBConsts.Col_SSMID, id);
		return states;
	}

	public Iterable<SSMState> findByParent(String parent) {
		Iterable<SSMState> states = super.findByKeyValue(IDBConsts.Col_PARENT, parent);
		return states;
	}

}
