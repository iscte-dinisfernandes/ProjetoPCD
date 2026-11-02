package iscot.events;

import iscot.enums.Areas;

/**
 * Leitura feita por um sensor. É publicada pelos sensores e consumida
 * pelos decisores. Todas as leituras são um número (double), por isso
 * o valor fica aqui e as subclasses só dizem que tipo de leitura é.
 */
public abstract class SensorEvent extends Event {

	private static final long serialVersionUID = 1L;

	private final double value;

	protected SensorEvent(String deviceId, Areas area, double value) {
		super(deviceId, area);
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	/** Unidade da leitura, para mostrar ao utilizador (ex.: "%", "°C"). */
	public abstract String getUnit();

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[área=" + getArea() + ", valor=" + value + " " + getUnit()
				+ ", origem=" + getDeviceId() + ", hora=" + getTimeString() + "]";
	}
}
