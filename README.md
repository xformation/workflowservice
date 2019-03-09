# workflowservice
A customizable workflow service for any base application , based on spring state machine
### How to import project for editing ###

* Import as maven project in your IDE

### Build, install and run application ###

To get started build the build the latest sources with Maven 3 and Java 8 
(or higher). 

	$ cd workflowservice
	$ mvn clean install 

You can run this application as spring-boot app by following command:

	$ mvn spring-boot:run

Once done you can run the application by executing 

	$ java -jar target/workflowservice-x.x.x-SNAPSHOT-exec.jar

## Application api's documentation ##
### /ssm/states/listAll

List all the entities from repository

	Method: GET
	Params:
	Response:
		List<SSMState>

### /ssm/states/{id}

API to load entity by id

	Method: GET
	Params:
	Response:
		SSMState

### /ssm/states/delete/{id}

API to delete an entity by id

	Method: POST
	Params:
	Response:
		String Success message or json error message.

### /ssm/states/delete

API to delete an entity

	Method: POST
	Params:
		entity		request.body	Json object
	Response:
		String Success message or json error message.

### /ssm/states/create

API to create an entity

	Method: POST
	Params:
		entity		request.body	Json object
	Response:
		SSMState as json object.

### /ssm/states/createByJson

API to create multiple entity
	SSMState json format:
	{
		"ssmId": "CloudMigration",
		"name": "",
		"parent": "",
		"target": "",
		"event": "",
		"action": "CLASS",
		"guard": "CLASS",
		"guardExpress": "",
		"initial": "true/false",
		"end": "true/false"
	}

	Method: POST
	Params:
		entity		request.body	Json object
	Response:
		SSMState as json object.

### /ssm/states/update

API to update an entity

	Method: POST
	Params:
		entity		request.body	Json object
	Response:
		SSMState as json object.

### /ssm/states/listStates

API to get all states of statemachne by id

	Method: POST, GET
	Params:
		machineId		satate machine id
	Response:
		SSMState names list.

### /ssm/states/machineId

API to get the machine id from a string state machine name

	Method: POST, GET
	Params:
		machineId		satate machine name
	Response:
		state machine id

### /ssm/states/addExtVar

API to set extended variable into statemachine

	Method: POST
	Params:
		machineId		satate machine id
		key				variable key
		cls				value class name
		value			extended variable value
	Response:
		object value

### /ssm/states/sendEvent

API to set extended variable into statemachine

	Method: POST
	Params:
		machineId		satate machine id
		event			event name
	Response:
		true if event send is successful

### /ssm/states/currentState

API to get current state of statemachine

	Method: POST, GET
	Params:
		machineId		satate machine id
	Response:
		String current state name

### /ssm/subscription/listAll

List all the entities from repository

	Method: GET
	Params:
	Response:
		List<Subscription>

### /ssm/subscription/{id}

API to load entity by id

	Method: GET
	Params:
	Response:
		Subscription

### /ssm/subscription/delete/{id}

API to delete an entity by id

	Method: POST
	Params:
	Response:
		String Success message or json error message.

### /ssm/subscription/delete

API to delete an entity

	Method: POST
	Params:
		entity		request.body	Json object
	Response:
		String Success message or json error message.

### /ssm/subscription/create

API to create an entity

	Method: POST
	Params:
		entity		request.body	Json object
	Response:
		Subscription as json object.

### /ssm/subscription/update

API to update an entity

	Method: POST
	Params:
		entity		request.body	Json object
	Response:
		Subscription as json object.
