package iscot.server;

import iscot.enums.AirConditioningMode;
import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.AreaLightingOrderEvent;
import iscot.events.AreaTemperatureOrderEvent;
import iscot.events.LightSensorEvent;
import iscot.events.RegistrationEvent;
import iscot.events.TemperatureSensorEvent;

/**
 * Pequeno programa para mostrar as fases 2 e 3 a funcionar, sem rede:
 * cria alguns eventos, publica-os nos tópicos e imprime o resultado.
 */
public class TopicsDemo {

	public static void main(String[] args) {
		System.out.println(new RegistrationEvent(Type.SENSOR, Areas.LivingRoom));

		Topics topics = new Topics();
		topics.publish(new LightSensorEvent("sensor-1", Areas.LivingRoom, 12));
		topics.publish(new TemperatureSensorEvent("sensor-2", Areas.LivingRoom, 31));
		topics.publish(new AreaLightingOrderEvent("decisor-1", Areas.LivingRoom, true));
		topics.publish(new AreaTemperatureOrderEvent("decisor-2", Areas.LivingRoom, AirConditioningMode.Frio));

		System.out.println("Tópico SENSOR na sala:  " + topics.getEvents(Type.SENSOR, Areas.LivingRoom));
		System.out.println("Tópico DECIDER na sala: " + topics.getEvents(Type.DECIDER, Areas.LivingRoom));
		System.out.println("Tópico SENSOR na cozinha (vazio): " + topics.getEvents(Type.SENSOR, Areas.Kitchen));
	}
}
