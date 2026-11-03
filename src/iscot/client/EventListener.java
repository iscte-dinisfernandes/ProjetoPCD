package iscot.client;

import iscot.events.Event;

/**
 * Interface simples para receção de eventos nos clientes consumidores.
 * Evita o uso de java.util.function.Consumer para manter o código
 * 100% aderente ao Java básico ensinado na UC.
 */
public interface EventListener {
	void onEventReceived(Event event);
}
