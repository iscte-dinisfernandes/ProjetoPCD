package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.SensorControl;
import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.HumiditySensorEvent;

/**
 * Cliente Sensor: Humidade do Ar
 *
 * Argumentos aceites:
 *   java iscot.client.HumiditySensor [Area]
 *   java iscot.client.HumiditySensor <IP> <PORT> <Area>
 * Exemplo: localhost 8080 LivingRoom
 */
public class HumiditySensor {

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

		JFrame janela = new JFrame("Humidade do Ar em " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		final Areas finalArea = area;
		JPanel painel = new JPanel(new BorderLayout());
		SensorControl sensorControl = new SensorControl(
				"☁", "Humidade do Ar - " + area, "%",
				0, 100, 1, 50,
				valor -> {
					String devId = (connection != null && connection.getAssignedDeviceId() != null)
							? connection.getAssignedDeviceId() : "local-sensor";
					HumiditySensorEvent event = new HumiditySensorEvent(devId, finalArea, valor);
					System.out.println("[Sensor Humidade] Evento: " + event);

					if (connection != null) {
						try {
							connection.sendEvent(event);
						} catch (IOException e) {
							System.err.println("[Sensor Humidade] Erro ao enviar para o servidor: " + e.getMessage());
						}
					}
				});

		painel.add(sensorControl, BorderLayout.CENTER);
		janela.setContentPane(painel);
		janela.setLocation(200, 450);
		janela.pack();
		janela.setVisible(true);

		try {
			connection = new Connection(host, port, Type.SENSOR, area, null);
			connection.start();
			janela.setTitle("Humidade do Ar - " + area + " [" + connection.getAssignedDeviceId() + "]");

			connection.sendEvent(new HumiditySensorEvent(connection.getAssignedDeviceId(), area, 50));

			janela.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					connection.close();
				}
			});
		} catch (Exception e) {
			System.out.println("[HumiditySensor] Servidor não disponível em " + host + ":" + port + " (" + e.getMessage() + "). Modo offline.");
		}
	}
}
