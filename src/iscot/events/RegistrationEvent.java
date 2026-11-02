package iscot.events;

import iscot.enums.Areas;
import iscot.enums.Type;

/**
 * Primeiro evento que um cliente envia ao servidor quando arranca:
 * "sou um cliente do tipo X e estou na área Y".
 *
 * Ainda não tem deviceId (fica a null), porque é o servidor que o vai
 * atribuir como resposta a este registo.
 */
public class RegistrationEvent extends Event {

	private static final long serialVersionUID = 1L;

	private final Type type;

	public RegistrationEvent(Type type, Areas area) {
		super(null, area);
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "RegistrationEvent[tipo=" + type + ", área=" + getArea() + "]";
	}
}
