package iscot.client;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.SensorControl;
import iscot.enums.Areas;
import iscot.events.TemperatureSensorEvent;

/**
 * Cliente Sensor: Temperatura
 *
 * Argumentos: IP PORT AreaDispositivo [TipoDispositivo]
 * Exemplo: localhost 8080 BedRoom
 */
public class TemperatureSensor {

	public static void main(String[] args) throws NumberFormatException, InterruptedException {
		if (args.length < 1) {
			System.err.println("Argumentos insuficientes! Uso: <Area> ou <IP> <PORT> <Area>");
			return;
		}

		String areaStr = (args.length == 1) ? args[0] : (args.length >= 3 ? args[2] : args[0]);
		Areas area;
		try {
			area = Areas.valueOf(areaStr);
		} catch (IllegalArgumentException e) {
			System.err.println("Área inválida: " + areaStr + ". Áreas válidas: Kitchen, WC, LivingRoom, BedRoom, Garden");
			return;
		}

		String deviceId = "temperature-sensor-" + area;

		JFrame janela = new JFrame("Temperatura em " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel painel = new JPanel(new BorderLayout());
		SensorControl sensorControl = new SensorControl(
				"🌡", "Temperatura - " + area, "°C",
				-10, 50, 0.5, 22.0,
				valor -> {
					// Fase 3: Gerar evento do sensor e exibir na consola
					TemperatureSensorEvent event = new TemperatureSensorEvent(deviceId, area, valor);
					System.out.println("[Sensor Temperatura] Evento gerado: " + event);
				});

		painel.add(sensorControl, BorderLayout.CENTER);
		janela.setContentPane(painel);
		janela.setLocation(600, 450);
		janela.pack();
		janela.setVisible(true);

		TemperatureSensorEvent eventoInicial = new TemperatureSensorEvent(deviceId, area, 22.0);
		System.out.println("[Sensor Temperatura] Leitura inicial: " + eventoInicial);
	}
}
