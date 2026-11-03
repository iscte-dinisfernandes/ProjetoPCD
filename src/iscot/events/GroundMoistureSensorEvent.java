package iscot.events;

import iscot.enums.Areas;

/** Leitura do sensor: Humidade do solo (jardim). */
public class GroundMoistureSensorEvent extends SensorEvent {

	private static final long serialVersionUID = 1L;

	public GroundMoistureSensorEvent(String deviceId, Areas area, double value) {
		super(deviceId, area, value);
	}

	@Override
	public String getUnit() {
		return "%";
	}
}
