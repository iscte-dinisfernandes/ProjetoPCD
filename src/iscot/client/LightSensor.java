package iscot.client;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.SensorControl;
import iscot.enums.Areas;
import iscot.events.LightSensorEvent;

/**
 * Cliente Sensor: Luminosidade
 *
 * Argumentos: IP PORT AreaDispositivo [TipoDispositivo]
 * Exemplo: localhost 8080 LivingRoom
 */
public class LightSensor {

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

		String deviceId = "light-sensor-" + area;

		JFrame janela = new JFrame("Luminosidade em " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel painel = new JPanel(new BorderLayout());
		SensorControl sensorControl = new SensorControl(
				"☀", "Luminosidade - " + area, "%",
				0, 100, 1, 70,
				valor -> {
					// Fase 3: Gerar evento do sensor e exibir na consola
					LightSensorEvent event = new LightSensorEvent(deviceId, area, valor);
					System.out.println("[Sensor Luz] Evento gerado: " + event);
				});

		painel.add(sensorControl, BorderLayout.CENTER);
		janela.setContentPane(painel);
		janela.setLocation(400, 450);
		janela.pack();
		janela.setVisible(true);

		LightSensorEvent eventoInicial = new LightSensorEvent(deviceId, area, 70);
		System.out.println("[Sensor Luz] Leitura inicial: " + eventoInicial);
	}
}
