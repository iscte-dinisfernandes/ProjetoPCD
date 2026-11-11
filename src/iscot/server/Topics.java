package iscot.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.Event;
import iscot.events.HighLevelOrderEvent;
import iscot.events.SensorEvent;

/**
 * Estrutura de tópicos e áreas (fase 2).
 *
 * Há dois tópicos:
 *  - SENSOR:  leituras dos sensores (quem as lê são os decisores);
 *  - DECIDER: ordens de alto nível dos decisores (quem as lê são os atuadores).
 *
 * Dentro de cada tópico há uma lista de eventos por área da casa,
 * pela ordem em que chegaram (o primeiro da lista é a cabeça da fila).
 */
public class Topics {

	private final Map<Areas, List<Event>> sensorTopic = new HashMap<>();
	private final Map<Areas, List<Event>> deciderTopic = new HashMap<>();

	public Topics() {
		// Criar uma lista vazia para cada área, nos dois tópicos.
		for (Areas area : Areas.values()) {
			sensorTopic.put(area, new ArrayList<>());
			deciderTopic.put(area, new ArrayList<>());
		}
	}

	/** Coloca o evento no fim da lista do seu tópico e da sua área. */
	public void publish(Event event) {
		if (event instanceof SensorEvent) {
			sensorTopic.get(event.getArea()).add(event);
		} else if (event instanceof HighLevelOrderEvent) {
			deciderTopic.get(event.getArea()).add(event);
		} else {
			System.out.println("Evento sem tópico, ignorado: " + event);
		}
	}

	/** Devolve os eventos de um tópico (SENSOR ou DECIDER) numa área. */
	public List<Event> getEvents(Type topic, Areas area) {
		if (topic == Type.SENSOR) {
			return sensorTopic.get(area);
		}
		return deciderTopic.get(area);
	}
}
