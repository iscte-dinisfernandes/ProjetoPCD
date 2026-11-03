package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.ActionHistoryPanel;
import iscot.client.gui.IrrigationIcon;
import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.AreaIrrigationOrderEvent;

/**
 * Cliente Atuador: Irrigação / Sistema de Rega
 *
 * Argumentos aceites:
 *   java iscot.client.IrrigationActuator [Area]
 *   java iscot.client.IrrigationActuator <IP> <PORT> <Area>
 * Exemplo: localhost 8080 Garden
 */
public class IrrigationActuator {

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

		JFrame janela = new JFrame("Irrigação - " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));

		IrrigationIcon irrigationIcon = new IrrigationIcon();
		ActionHistoryPanel historyPanel = new ActionHistoryPanel();

		painelPrincipal.add(irrigationIcon, BorderLayout.CENTER);
		painelPrincipal.add(historyPanel, BorderLayout.SOUTH);

		final Areas finalArea = area;
		JButton btnSimular = new JButton("Alternar Rega Manualmente");
		btnSimular.addActionListener(e -> {
			boolean novoEstado = !irrigationIcon.isAtiva();
			AreaIrrigationOrderEvent eventoSimulado = new AreaIrrigationOrderEvent("manual", finalArea, novoEstado);
			processarOrdem(eventoSimulado, irrigationIcon, historyPanel);
		});
		painelPrincipal.add(btnSimular, BorderLayout.NORTH);

		janela.setContentPane(painelPrincipal);
		janela.setLocation(800, 200);
		janela.pack();
		janela.setVisible(true);

		// Conectar à rede com Connection
		try {
			Connection connection = new Connection(host, port, Type.ACTUATOR, area, event -> {
				if (event instanceof AreaIrrigationOrderEvent) {
					AreaIrrigationOrderEvent ordem = (AreaIrrigationOrderEvent) event;
					processarOrdem(ordem, irrigationIcon, historyPanel);
				}
			});
			connection.start();
			janela.setTitle("Irrigação - " + area + " [" + connection.getAssignedDeviceId() + "]");

			janela.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					connection.close();
				}
			});
		} catch (Exception e) {
			System.out.println("[IrrigationActuator] Servidor não disponível em " + host + ":" + port + " (" + e.getMessage() + "). Modo offline.");
		}
	}

	public static void processarOrdem(AreaIrrigationOrderEvent evento, IrrigationIcon icon, ActionHistoryPanel history) {
		boolean ligar = evento.isTurnOn();
		icon.setAtiva(ligar);
		String acao = ligar ? "Rega ativada" : "Rega desativada";
		history.addAction(acao);
		System.out.println("[Irrigação @" + evento.getArea() + "] Processou ordem: " + acao);
	}
}
