package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.ActionHistoryPanel;
import iscot.client.gui.VentilatorIcon;
import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.AreaVentilationOrderEvent;

/**
 * Cliente Atuador: Ventilador
 *
 * Argumentos aceites:
 *   java iscot.client.Ventilator [Area]
 *   java iscot.client.Ventilator <IP> <PORT> <Area>
 * Exemplo: localhost 8080 LivingRoom
 */
public class Ventilator {

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

		JFrame janela = new JFrame("Ventilador - " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));

		VentilatorIcon ventilatorIcon = new VentilatorIcon();
		ActionHistoryPanel historyPanel = new ActionHistoryPanel();

		painelPrincipal.add(ventilatorIcon, BorderLayout.CENTER);
		painelPrincipal.add(historyPanel, BorderLayout.SOUTH);

		final Areas finalArea = area;
		JButton btnSimular = new JButton("Alternar Ventilador Manualmente");
		btnSimular.addActionListener(e -> {
			boolean novoEstado = !ventilatorIcon.isLigado();
			AreaVentilationOrderEvent eventoSimulado = new AreaVentilationOrderEvent("manual", finalArea, novoEstado);
			processarOrdem(eventoSimulado, ventilatorIcon, historyPanel);
		});
		painelPrincipal.add(btnSimular, BorderLayout.NORTH);

		janela.setContentPane(painelPrincipal);
		janela.setLocation(200, 200);
		janela.pack();
		janela.setVisible(true);

		// Conectar à rede com Connection
		try {
			Connection connection = new Connection(host, port, Type.ACTUATOR, area, event -> {
				if (event instanceof AreaVentilationOrderEvent) {
					AreaVentilationOrderEvent ordem = (AreaVentilationOrderEvent) event;
					processarOrdem(ordem, ventilatorIcon, historyPanel);
				}
			});
			connection.start();
			janela.setTitle("Ventilador - " + area + " [" + connection.getAssignedDeviceId() + "]");

			janela.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					connection.close();
				}
			});
		} catch (Exception e) {
			System.out.println("[Ventilator] Servidor não disponível em " + host + ":" + port + " (" + e.getMessage() + "). Modo offline.");
		}
	}

	public static void processarOrdem(AreaVentilationOrderEvent evento, VentilatorIcon icon, ActionHistoryPanel history) {
		boolean ligar = evento.isTurnOn();
		icon.setLigado(ligar);
		String acao = ligar ? "Ventilador ligado" : "Ventilador desligado";
		history.addAction(acao);
		System.out.println("[Ventilador @" + evento.getArea() + "] Processou ordem: " + acao);
	}
}
