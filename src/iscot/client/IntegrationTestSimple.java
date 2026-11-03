package iscot.client;

import iscot.enums.Areas;
import iscot.server.Server;

/**
 * Teste de integração das Fases 5 a 7:
 * Lança 1 Servidor + 1 Sensor + 1 Decisor + 1 Atuador (na mesma área: LivingRoom).
 *
 * Fluxo demonstrado através da rede (Sockets e canais de objetos):
 * 1. O servidor arranca no porto 8080.
 * 2. O Atuador (LightActuator) liga-se como ACTUATOR na LivingRoom e recebe ID único.
 * 3. O Decisor (Decider) liga-se como DECIDER na LivingRoom e recebe ID único.
 * 4. O Sensor (LightSensor) liga-se como SENSOR na LivingRoom e recebe ID único.
 * 5. Ao alterar o valor da luz no Sensor:
 *    Sensor -> Servidor -> Decisor -> Servidor -> Atuador
 *    O Atuador altera a lâmpada (amarela/cinzenta) e regista no histórico de ações!
 */
public class IntegrationTestSimple {

	public static void main(String[] args) {
		System.out.println("=== A INICIAR TESTE SIMPLES FASE 5 A 7 (1 Sensor + 1 Decisor + 1 Atuador) ===");

		// 1. Iniciar Servidor numa thread
		new Thread(() -> {
			Server server = new Server(8080);
			try {
				server.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "ServerThread").start();

		// Pequena pausa para garantir que o ServerSocket está pronto
		try {
			Thread.sleep(500);
		} catch (InterruptedException ignored) {}

		// 2. Iniciar Atuador (Iluminação na LivingRoom)
		new Thread(() -> {
			LightActuator.main(new String[]{"localhost", "8080", Areas.LivingRoom.name()});
		}, "ActuatorThread").start();

		// 3. Iniciar Decisor na LivingRoom
		new Thread(() -> {
			try {
				Thread.sleep(300);
				Decider.main(new String[]{"localhost", "8080", Areas.LivingRoom.name()});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "DeciderThread").start();

		// 4. Iniciar Sensor de Luminosidade na LivingRoom
		new Thread(() -> {
			try {
				Thread.sleep(600);
				LightSensor.main(new String[]{"localhost", "8080", Areas.LivingRoom.name()});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "SensorThread").start();

		System.out.println("Todos os componentes do teste simples foram iniciados na rede!");
	}
}
