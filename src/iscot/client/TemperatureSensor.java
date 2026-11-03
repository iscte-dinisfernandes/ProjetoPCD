package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.SensorControl;
import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.TemperatureSensorEvent;

/**
 * Cliente Sensor: Temperatura
 *
 * Argumentos aceites:
 *   java iscot.client.TemperatureSensor [Area]
 *   java iscot.client.TemperatureSensor <IP> <PORT> <Area>
 * Exemplo: localhost 8080 BedRoom
 */
public class TemperatureSensor {

	private static Connection connection;

	public static void main(String[] args) {
		String host = "localhost";
		int port = 8080;
		Areas area = Areas.BedRoom;

		if (args.length >= 3) {
			host = args[0];
			try {
				port = Integer.parseInt(args[1]);
			} catch (NumberFormatException ignored) {}
			area = Areas.valueOf(args[2]);
		} else if (args.length == 1) {
			area = Areas.valueOf(args[0]);
		}

		JFrame janela = new JFrame("Temperatura em " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		final Areas finalArea = area;
		JPanel painel = new JPanel(new BorderLayout());
		SensorControl sensorControl = new SensorControl(
				"🌡", "Temperatura - " + area, "°C",
				-10, 50, 0.5, 22.0,
				valor -> {
					String devId = (connection != null && connection.getAssignedDeviceId() != null)
							? connection.getAssignedDeviceId() : "local-sensor";
					TemperatureSensorEvent event = new TemperatureSensorEvent(devId, finalArea, valor);
					System.out.println("[Sensor Temperatura] Evento: " + event);

					if (connection != null) {
						try {
							connection.sendEvent(event);
						} catch (IOException e) {
							System.err.println("[Sensor Temperatura] Erro ao enviar para o servidor: " + e.getMessage());
						}
					}
				});

		painel.add(sensorControl, BorderLayout.CENTER);
		janela.setContentPane(painel);
		janela.setLocation(600, 450);
		janela.pack();
		janela.setVisible(true);

		try {
			connection = new Connection(host, port, Type.SENSOR, area, null);
			connection.start();
			janela.setTitle("Temperatura - " + area + " [" + connection.getAssignedDeviceId() + "]");

			connection.sendEvent(new TemperatureSensorEvent(connection.getAssignedDeviceId(), area, 22.0));

			janela.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					connection.close();
				}
			});
		} catch (Exception e) {
			System.out.println("[TemperatureSensor] Servidor não disponível em " + host + ":" + port + " (" + e.getMessage() + "). Modo offline.");
		}
	}
}
