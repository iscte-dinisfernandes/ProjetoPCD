package iscot.events;

import iscot.enums.Areas;

/** Leitura do sensor: Luminosidade (0 = escuro, 100 = muita luz). */
public class LightSensorEvent extends SensorEvent {

	private static final long serialVersionUID = 1L;

	public LightSensorEvent(String deviceId, Areas area, double value) {
		super(deviceId, area, value);
	}

	@Override
	public String getUnit() {
		return "%";
	}
}
