/**
 * 
 */
package com.synectiks.state.machine.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * @author Rajesh Upadhyay
 */
@SuppressWarnings("rawtypes")
public class StateMachineContextConverter {

	private static class SSMCtxSerializer<S, E> extends Serializer<StateMachineContext<S, E>> {

		@Override
		public void write(Kryo kryo, Output output, StateMachineContext<S, E> context) {
			kryo.writeClassAndObject(output, context.getEvent());
			kryo.writeClassAndObject(output, context.getState());
			kryo.writeClassAndObject(output, context.getEventHeaders());
			kryo.writeClassAndObject(output, context.getExtendedState() != null ? context.getExtendedState().getVariables() : null);
			kryo.writeClassAndObject(output, context.getChilds());
			kryo.writeClassAndObject(output, context.getHistoryStates());
			kryo.writeClassAndObject(output, context.getId());
		}

		@SuppressWarnings("unchecked")
		@Override
		public StateMachineContext<S, E> read(Kryo kryo, Input input, Class<StateMachineContext<S, E>> clazz) {
			E event = (E) kryo.readClassAndObject(input);
			S state = (S) kryo.readClassAndObject(input);
			Map<String, Object> eventHeaders = (Map<String, Object>) kryo.readClassAndObject(input);
			Map<Object, Object> variables = (Map<Object, Object>) kryo.readClassAndObject(input);
			List<StateMachineContext<S, E>> childs = (List<StateMachineContext<S, E>>) kryo.readClassAndObject(input);
			Map<S, S> historyStates = (Map<S, S>) kryo.readClassAndObject(input);
			String id = (String) kryo.readClassAndObject(input);

			return new DefaultStateMachineContext<S, E>(childs, state, event, eventHeaders,
					new DefaultExtendedState(variables), historyStates, id);
		}
	}

	private static final ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal<Kryo>() {

		@Override
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
			kryo.addDefaultSerializer(StateMachineContext.class, new SSMCtxSerializer());
			return kryo;
		}
	};

	public String convertToDatabaseColumn(StateMachineContext attribute) {
		 String out = new String(Base64.getEncoder().encode(serialize(attribute)));
		 return out;
	}

	public StateMachineContext convertToEntityAttribute(String in) {
		byte[] dbData = in.getBytes();
		return deserialize(Base64.getDecoder().decode(dbData));
	}

	private byte[] serialize(StateMachineContext context) {
		if (context == null) {
			return null;
		}
		Kryo kryo = kryoThreadLocal.get();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Output output = new Output(out);
		kryo.writeObject(output, context);
		output.close();
		return out.toByteArray();
	}

	private StateMachineContext deserialize(byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		Kryo kryo = kryoThreadLocal.get();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		Input input = new Input(in);
		return kryo.readObject(input, StateMachineContext.class);
	}
}
