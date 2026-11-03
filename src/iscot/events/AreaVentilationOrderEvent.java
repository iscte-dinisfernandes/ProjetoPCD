package iscot.events;

import iscot.enums.Areas;

/** Ordem para ligar/desligar a ventilação de uma área. */
public class AreaVentilationOrderEvent extends HighLevelOrderEvent {

	private static final long serialVersionUID = 1L;

	public AreaVentilationOrderEvent(String deviceId, Areas area, boolean turnOn) {
		super(deviceId, area, turnOn);
	}
}
