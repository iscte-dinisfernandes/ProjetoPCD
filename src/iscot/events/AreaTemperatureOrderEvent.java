package iscot.events;

import iscot.enums.AirConditioningMode;
import iscot.enums.Areas;

/**
 * Ordem para o ar condicionado. Além de ligar/desligar, diz em que modo
 * (Frio, Aquecimento, Ventilação). Desligado significa desligar.
 */
public class AreaTemperatureOrderEvent extends HighLevelOrderEvent {

	private static final long serialVersionUID = 1L;

	private final AirConditioningMode mode;

	public AreaTemperatureOrderEvent(String deviceId, Areas area, AirConditioningMode mode) {
		super(deviceId, area, mode != AirConditioningMode.Desligado);
		this.mode = mode;
	}

	public AirConditioningMode getMode() {
		return mode;
	}

	@Override
	public String toString() {
		return "AreaTemperatureOrderEvent[área=" + getArea() + ", modo=" + mode + ", origem=" + getDeviceId()
				+ ", hora=" + getTimeString() + "]";
	}
}
