package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.ACIcon;
import iscot.client.gui.ActionHistoryPanel;
import iscot.enums.AirConditioningMode;
import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.AreaTemperatureOrderEvent;

/**
 * Cliente Atuador: Ar Condicionado
 *
 * Argumentos aceites:
 *   java iscot.client.ACActuator [Area]
 *   java iscot.client.ACActuator <IP> <PORT> <Area>
 * Exemplo: localhost 8080 BedRoom
 */
public class ACActuator {

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

		JFrame janela = new JFrame("Ar Condicionado - " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));

		ACIcon acIcon = new ACIcon();
		ActionHistoryPanel historyPanel = new ActionHistoryPanel();

		painelPrincipal.add(acIcon, BorderLayout.CENTER);
		painelPrincipal.add(historyPanel, BorderLayout.SOUTH);

		AirConditioningMode[] modos = {
			AirConditioningMode.Aquecimento,
			AirConditioningMode.Frio,
			AirConditioningMode.Ventilação,
			AirConditioningMode.Desligado
		};
		final int[] modoIndex = {0};
		final Areas finalArea = area;

		JButton btnSimular = new JButton("Alternar Modo Manualmente");
		btnSimular.addActionListener(e -> {
			AirConditioningMode proximoModo = modos[modoIndex[0] % modos.length];
			modoIndex[0]++;
			AreaTemperatureOrderEvent eventoSimulado = new AreaTemperatureOrderEvent("manual", finalArea, proximoModo);
			processarOrdem(eventoSimulado, acIcon, historyPanel);
		});
		painelPrincipal.add(btnSimular, BorderLayout.NORTH);

		janela.setContentPane(painelPrincipal);
		janela.setLocation(600, 200);
		janela.pack();
		janela.setVisible(true);

		// Conectar à rede com Connection
		try {
			Connection connection = new Connection(host, port, Type.ACTUATOR, area, event -> {
				if (event instanceof AreaTemperatureOrderEvent) {
					AreaTemperatureOrderEvent ordem = (AreaTemperatureOrderEvent) event;
					processarOrdem(ordem, acIcon, historyPanel);
				}
			});
			connection.start();
			janela.setTitle("Ar Condicionado - " + area + " [" + connection.getAssignedDeviceId() + "]");

			janela.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					connection.close();
				}
			});
		} catch (Exception e) {
			System.out.println("[ACActuator] Servidor não disponível em " + host + ":" + port + " (" + e.getMessage() + "). Modo offline.");
		}
	}

	public static void processarOrdem(AreaTemperatureOrderEvent evento, ACIcon icon, ActionHistoryPanel history) {
		AirConditioningMode modo = evento.getMode();
		icon.setModo(modo);
		String acao = (modo == AirConditioningMode.Desligado)
				? "Ar condicionado desligado"
				: "Ar condicionado em modo " + modo;
		history.addAction(acao);
		System.out.println("[AC @" + evento.getArea() + "] Processou ordem: " + acao);
	}
}
