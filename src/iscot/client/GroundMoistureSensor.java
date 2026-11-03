package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.SensorControl;
import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.GroundMoistureSensorEvent;

/**
 * Cliente Sensor: Humidade do Solo (Rega/Jardim)
 *
 * Argumentos aceites:
 *   java iscot.client.GroundMoistureSensor [Area]
 *   java iscot.client.GroundMoistureSensor <IP> <PORT> <Area>
 * Exemplo: localhost 8080 Garden
 */
public class GroundMoistureSensor {

	private static Connection connection;

	public static void main(String[] args) {
		String host = "localhost";
		int port = 8080;
		Areas area = Areas.Garden;

		if (args.length >= 3) {
			host = args[0];
			try {
				port = Integer.parseInt(args[1]);
			} catch (NumberFormatException ignored) {}
			area = Areas.valueOf(args[2]);
		} else if (args.length == 1) {
			area = Areas.valueOf(args[0]);
		}

		JFrame janela = new JFrame("Humidade do Solo em " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		final Areas finalArea = area;
		JPanel painel = new JPanel(new BorderLayout());
		SensorControl sensorControl = new SensorControl(
				"🌱", "Humidade do Solo - " + area, "%",
				0, 100, 1, 40,
				valor -> {
					String devId = (connection != null && connection.getAssignedDeviceId() != null)
							? connection.getAssignedDeviceId() : "local-sensor";
					GroundMoistureSensorEvent event = new GroundMoistureSensorEvent(devId, finalArea, valor);
					System.out.println("[Sensor Humidade do Solo] Evento: " + event);

					if (connection != null) {
						try {
							connection.sendEvent(event);
						} catch (IOException e) {
							System.err.println("[Sensor Humidade do Solo] Erro ao enviar para o servidor: " + e.getMessage());
						}
					}
				});

		painel.add(sensorControl, BorderLayout.CENTER);
		janela.setContentPane(painel);
		janela.setLocation(800, 450);
		janela.pack();
		janela.setVisible(true);

		try {
			connection = new Connection(host, port, Type.SENSOR, area, null);
			connection.start();
			janela.setTitle("Humidade do Solo - " + area + " [" + connection.getAssignedDeviceId() + "]");

			connection.sendEvent(new GroundMoistureSensorEvent(connection.getAssignedDeviceId(), area, 40));

			janela.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					connection.close();
				}
			});
		} catch (Exception e) {
			System.out.println("[GroundMoistureSensor] Servidor não disponível em " + host + ":" + port + " (" + e.getMessage() + "). Modo offline.");
		}
	}
}
