package iscot.events;

import iscot.enums.Areas;

/** Leitura do sensor: Temperatura do ar. */
public class TemperatureSensorEvent extends SensorEvent {

	private static final long serialVersionUID = 1L;

	public TemperatureSensorEvent(String deviceId, Areas area, double value) {
		super(deviceId, area, value);
	}

	@Override
	public String getUnit() {
		return "°C";
	}
}
