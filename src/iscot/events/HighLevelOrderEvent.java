package iscot.events;

import iscot.enums.Areas;

/**
 * Ordem de alto nível. É publicada pelos decisores e consumida pelos
 * atuadores. A ordem mais simples é "ligar" ou "desligar", por isso
 * esse campo fica aqui; as subclasses dizem a que dispositivo se refere.
 */
public abstract class HighLevelOrderEvent extends Event {

	private static final long serialVersionUID = 1L;

	private final boolean turnOn;

	protected HighLevelOrderEvent(String deviceId, Areas area, boolean turnOn) {
		super(deviceId, area);
		this.turnOn = turnOn;
	}

	public boolean isTurnOn() {
		return turnOn;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[área=" + getArea() + ", " + (turnOn ? "ligar" : "desligar")
				+ ", origem=" + getDeviceId() + ", hora=" + getTimeString() + "]";
	}
}
