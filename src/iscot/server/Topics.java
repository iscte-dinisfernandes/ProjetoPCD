package iscot.server;

import java.util.ArrayList;
import java.util.EnumMap;
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
 * Um tópico é identificado por (tipo de quem publica, área):
 *  - tópico SENSOR  + área: leituras dos sensores dessa área (lidas pelos decisores);
 *  - tópico DECIDER + área: ordens de alto nível dessa área (lidas pelos atuadores).
 *
 * Para cada par guardamos uma lista de eventos, pela ordem em que chegaram.
 * Nesta fase ainda não há threads, por isso a lista é uma lista normal.
 * Na fase 8 cada lista passa a ser uma fila bloqueante feita por nós.
 */
public class Topics {

	private final Map<Type, Map<Areas, List<Event>>> topics = new EnumMap<>(Type.class);

	public Topics() {
		for (Type type : new Type[] { Type.SENSOR, Type.DECIDER }) {
			Map<Areas, List<Event>> byArea = new EnumMap<>(Areas.class);
			for (Areas area : Areas.values()) {
				byArea.put(area, new ArrayList<>());
			}
			topics.put(type, byArea);
		}
	}

	/** Diz em que tópico um evento deve ser publicado, conforme a sua classe. */
	public static Type topicOf(Event event) {
		if (event instanceof SensorEvent) {
			return Type.SENSOR;
		}
		if (event instanceof HighLevelOrderEvent) {
			return Type.DECIDER;
		}
		throw new IllegalArgumentException("Este evento não pertence a nenhum tópico: " + event);
	}

	/** Coloca o evento no fim da lista do seu tópico e área. */
	public void publish(Event event) {
		topics.get(topicOf(event)).get(event.getArea()).add(event);
	}

	/** Devolve os eventos de um tópico numa área (a cabeça da fila é o índice 0). */
	public List<Event> getEvents(Type topic, Areas area) {
		return topics.get(topic).get(area);
	}
}
