package iscot.events;

import iscot.enums.Areas;

/** Leitura do sensor: Humidade do ar. */
public class HumiditySensorEvent extends SensorEvent {

	private static final long serialVersionUID = 1L;

	public HumiditySensorEvent(String deviceId, Areas area, double value) {
		super(deviceId, area, value);
	}

	@Override
	public String getUnit() {
		return "%";
	}
}
