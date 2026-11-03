package iscot.events;

import iscot.enums.Areas;
import iscot.enums.Type;

/**
 * Resposta do servidor ao RegistrationEvent de um cliente.
 * Contém o identificador único atribuído ao dispositivo,
 * ou o status de sucesso/recusa.
 */
public class RegistrationResponse extends Event {

	private static final long serialVersionUID = 1L;

	private final boolean success;
	private final String message;

	public RegistrationResponse(String deviceId, Areas area, boolean success, String message) {
		super(deviceId, area);
		this.success = success;
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "RegistrationResponse[id=" + getDeviceId() + ", área=" + getArea()
				+ ", sucesso=" + success + ", msg=" + message + "]";
	}
}
