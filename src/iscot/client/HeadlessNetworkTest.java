package iscot.client;

import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.AreaLightingOrderEvent;
import iscot.events.LightSensorEvent;
import iscot.server.Server;

/**
 * Teste automatizado sem interface gráfica (modo headless)
 * para validar o fluxo de rede de ponta a ponta das Fases 5 a 7:
 * Sensor -> Server -> Decider -> Server -> Actuator.
 */
public class HeadlessNetworkTest {

	private static Connection deciderConn;

	public static void main(String[] args) throws Exception {
		int port = 8085;
		System.out.println("--- 1. A iniciar Servidor no porto " + port + " ---");
		Server server = new Server(port);
		Thread serverThread = new Thread(() -> {
			try {
				server.start();
			} catch (Exception ignored) {}
		});
		serverThread.start();
		Thread.sleep(500);

		// Flag para verificar se o atuador recebeu a ordem
		final boolean[] actuatorReceivedOrder = {false};

		System.out.println("--- 2. A conectar Atuador como ACTUATOR na LivingRoom ---");
		Connection actuatorConn = new Connection("localhost", port, Type.ACTUATOR, Areas.LivingRoom, event -> {
			System.out.println("[Atuador Recebeu] " + event);
			if (event instanceof AreaLightingOrderEvent) {
				AreaLightingOrderEvent order = (AreaLightingOrderEvent) event;
				System.out.println("[Atuador] Confirmou ordem de luz: ligar=" + order.isTurnOn());
				actuatorReceivedOrder[0] = true;
			}
		});
		actuatorConn.start();

		System.out.println("--- 3. A conectar Decisor como DECIDER na LivingRoom ---");
		deciderConn = new Connection("localhost", port, Type.DECIDER, Areas.LivingRoom, event -> {
			System.out.println("[Decisor Recebeu] " + event);
			if (event instanceof LightSensorEvent) {
				LightSensorEvent lse = (LightSensorEvent) event;
				boolean ligar = lse.getValue() < 50;
				try {
					AreaLightingOrderEvent order = new AreaLightingOrderEvent(deciderConn.getAssignedDeviceId(), Areas.LivingRoom, ligar);
					deciderConn.sendEvent(order);
					System.out.println("[Decisor] Regra aplicada e ordem enviada: " + order);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		deciderConn.start();

		System.out.println("--- 4. A conectar Sensor como SENSOR na LivingRoom ---");
		Connection sensorConn = new Connection("localhost", port, Type.SENSOR, Areas.LivingRoom, null);
		sensorConn.start();

		Thread.sleep(500);

		System.out.println("--- 5. Sensor a emitir leitura de baixa luz (valor = 20.0%) ---");
		sensorConn.sendEvent(new LightSensorEvent(sensorConn.getAssignedDeviceId(), Areas.LivingRoom, 20.0));

		// Aguardar propagação pela rede
		Thread.sleep(1500);

		if (actuatorReceivedOrder[0]) {
			System.out.println("\n>>> SUCESSO TOTAL! O fluxo de rede completou-se com sucesso! <<<");
		} else {
			System.err.println("\n>>> FALHA: O atuador não recebeu o evento a tempo. <<<");
		}

		// Limpeza
		sensorConn.close();
		deciderConn.close();
		actuatorConn.close();
		server.stop();
		System.exit(actuatorReceivedOrder[0] ? 0 : 1);
	}
}
