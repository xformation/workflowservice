/**
 * 
 */
package com.synectiks.state.machine.repositories;

import org.springframework.stereotype.Repository;

import com.synectiks.commons.constants.IDBConsts;
import com.synectiks.commons.entities.SSMachine;
import com.synectiks.commons.utils.IUtils;
import com.synectiks.schemas.repositories.DynamoDbRepository;

/**
 * @author Rajesh Upadhyay
 */
@Repository
public class SSMRepository extends DynamoDbRepository<SSMachine, String> {

	public SSMRepository() {
		super(SSMachine.class);
	}

	public SSMachine findBySsmId(String id) {
		Iterable<SSMachine> machines = super.findByKeyValue(IDBConsts.Col_SSMID, id);

		if (!IUtils.isNull(machines) && !IUtils.isNull(machines.iterator())
				&& machines.iterator().hasNext()) {
			return machines.iterator().next();
		}
		return null;
	}

}
