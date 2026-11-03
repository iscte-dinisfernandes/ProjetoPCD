package iscot.events;

import iscot.enums.Areas;

/** Ordem para ligar/desligar a rega (normalmente no jardim). */
public class AreaIrrigationOrderEvent extends HighLevelOrderEvent {

	private static final long serialVersionUID = 1L;

	public AreaIrrigationOrderEvent(String deviceId, Areas area, boolean turnOn) {
		super(deviceId, area, turnOn);
	}
}
