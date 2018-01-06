/**
 * 
 */
package com.synectiks.state.machine.repositories;

import org.springframework.stereotype.Repository;

import com.synectiks.commons.entities.dynamodb.Subscription;
import com.synectiks.schemas.repositories.DynamoDbRepository;

/**
 * @author Rajesh
 */
@Repository
public class SubscriptionRepository extends DynamoDbRepository<Subscription, String> {

	public SubscriptionRepository() {
		super(Subscription.class);
	}

}
