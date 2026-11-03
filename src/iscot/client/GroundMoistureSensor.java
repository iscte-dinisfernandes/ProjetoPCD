package iscot.client;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.SensorControl;
import iscot.enums.Areas;
import iscot.events.GroundMoistureSensorEvent;

/**
 * Cliente Sensor: Humidade do Solo (Rega/Jardim)
 *
 * Argumentos: IP PORT AreaDispositivo [TipoDispositivo]
 * Exemplo: localhost 8080 Garden
 */
public class GroundMoistureSensor {

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

		String deviceId = "ground-moisture-sensor-" + area;

		JFrame janela = new JFrame("Humidade do Solo em " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel painel = new JPanel(new BorderLayout());
		SensorControl sensorControl = new SensorControl(
				"🌱", "Humidade do Solo - " + area, "%",
				0, 100, 1, 40,
				valor -> {
					// Fase 3: Gerar evento do sensor e exibir na consola
					GroundMoistureSensorEvent event = new GroundMoistureSensorEvent(deviceId, area, valor);
					System.out.println("[Sensor Humidade do Solo] Evento gerado: " + event);
				});

		painel.add(sensorControl, BorderLayout.CENTER);
		janela.setContentPane(painel);
		janela.setLocation(800, 450);
		janela.pack();
		janela.setVisible(true);

		GroundMoistureSensorEvent eventoInicial = new GroundMoistureSensorEvent(deviceId, area, 40);
		System.out.println("[Sensor Humidade do Solo] Leitura inicial: " + eventoInicial);
	}
}
