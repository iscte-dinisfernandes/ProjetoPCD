package iscot.events;

import iscot.enums.Areas;

/** Ordem para ligar/desligar as luzes de uma área. */
public class AreaLightingOrderEvent extends HighLevelOrderEvent {

	private static final long serialVersionUID = 1L;

	public AreaLightingOrderEvent(String deviceId, Areas area, boolean turnOn) {
		super(deviceId, area, turnOn);
	}
}
