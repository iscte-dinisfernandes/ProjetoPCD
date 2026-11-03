package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.SensorControl;
import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.LightSensorEvent;

/**
 * Cliente Sensor: Luminosidade
 *
 * Argumentos aceites:
 *   java iscot.client.LightSensor [Area]
 *   java iscot.client.LightSensor <IP> <PORT> <Area>
 * Exemplo: localhost 8080 LivingRoom
 */
public class LightSensor {

	private static Connection connection;

	public static void main(String[] args) {
		String host = "localhost";
		int port = 8080;
		Areas area = Areas.LivingRoom;

		if (args.length >= 3) {
			host = args[0];
			try {
				port = Integer.parseInt(args[1]);
			} catch (NumberFormatException ignored) {}
			area = Areas.valueOf(args[2]);
		} else if (args.length == 1) {
			area = Areas.valueOf(args[0]);
		}

		JFrame janela = new JFrame("Luminosidade em " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		final Areas finalArea = area;
		JPanel painel = new JPanel(new BorderLayout());
		SensorControl sensorControl = new SensorControl(
				"☀", "Luminosidade - " + area, "%",
				0, 100, 1, 70,
				valor -> {
					String devId = (connection != null && connection.getAssignedDeviceId() != null)
							? connection.getAssignedDeviceId() : "local-sensor";
					LightSensorEvent event = new LightSensorEvent(devId, finalArea, valor);
					System.out.println("[Sensor Luz] Evento: " + event);

					if (connection != null) {
						try {
							connection.sendEvent(event);
						} catch (IOException e) {
							System.err.println("[Sensor Luz] Erro ao enviar para o servidor: " + e.getMessage());
						}
					}
				});

		painel.add(sensorControl, BorderLayout.CENTER);
		janela.setContentPane(painel);
		janela.setLocation(400, 450);
		janela.pack();
		janela.setVisible(true);

		// Conectar ao servidor
		try {
			connection = new Connection(host, port, Type.SENSOR, area, null);
			connection.start();
			janela.setTitle("Luminosidade - " + area + " [" + connection.getAssignedDeviceId() + "]");

			// Enviar leitura inicial ao servidor
			connection.sendEvent(new LightSensorEvent(connection.getAssignedDeviceId(), area, 70));

			janela.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					connection.close();
				}
			});
		} catch (Exception e) {
			System.out.println("[LightSensor] Servidor não disponível em " + host + ":" + port + " (" + e.getMessage() + "). Modo offline.");
		}
	}
}
