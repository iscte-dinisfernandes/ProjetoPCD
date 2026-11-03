package iscot.client;

import java.io.IOException;

import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.AreaLightingOrderEvent;
import iscot.events.Event;
import iscot.events.LightSensorEvent;

/**
 * Cliente Decisor: Exemplo simples para iluminação (Fase 6 e 7).
 *
 * Responsabilidade:
 * - Liga-se ao servidor como DECIDER para uma determinada área;
 * - Consome leituras de SensorEvent (ex: LightSensorEvent);
 * - Aplica regra: luminosidade < 50% -> ligar luzes; >= 50% -> desligar luzes;
 * - Publica a ordem HighLevelOrderEvent (AreaLightingOrderEvent) no servidor,
 *   que o servidor encaminhará para os atuadores dessa área.
 */
public class Decider {

	private final String host;
	private final int port;
	private final Areas area;
	private Connection connection;

	public Decider(String host, int port, Areas area) {
		this.host = host;
		this.port = port;
		this.area = area;
	}

	public void start() throws IOException, ClassNotFoundException {
		this.connection = new Connection(host, port, Type.DECIDER, area, this::processIncomingEvent);
		connection.start();
		System.out.println("[Decisor @" + area + "] Ligado ao servidor e pronto a processar regras.");
	}

	/**
	 * Processa o evento recebido do servidor.
	 */
	private void processIncomingEvent(Event event) {
		System.out.println("[Decisor @" + area + "] Recebeu leitura: " + event);

		if (event instanceof LightSensorEvent) {
			LightSensorEvent lse = (LightSensorEvent) event;
			boolean shouldTurnOn = lse.getValue() < 50.0;
			System.out.println("[Decisor @" + area + "] Regra aplicada: luz=" + lse.getValue() + "% -> Ligar=" + shouldTurnOn);

			AreaLightingOrderEvent order = new AreaLightingOrderEvent(connection.getAssignedDeviceId(), area, shouldTurnOn);
			try {
				connection.sendEvent(order);
				System.out.println("[Decisor @" + area + "] Ordem enviada para o servidor: " + order);
			} catch (IOException e) {
				System.err.println("[Decisor @" + area + "] Erro ao enviar ordem: " + e.getMessage());
			}
		}
	}

	public void stop() {
		if (connection != null) {
			connection.close();
		}
	}

	public static void main(String[] args) {
		String host = "localhost";
		int port = 8080;
		Areas area = Areas.LivingRoom;

		if (args.length >= 3) {
			host = args[0];
			port = Integer.parseInt(args[1]);
			area = Areas.valueOf(args[2]);
		} else if (args.length >= 1) {
			area = Areas.valueOf(args[0]);
		}

		try {
			Decider decider = new Decider(host, port, area);
			decider.start();
		} catch (Exception e) {
			System.err.println("Erro no Decisor: " + e.getMessage());
		}
	}
}
