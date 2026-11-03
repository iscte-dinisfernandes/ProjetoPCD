package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.ActionHistoryPanel;
import iscot.client.gui.LightIcon;
import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.AreaLightingOrderEvent;

/**
 * Cliente Atuador: Iluminação / Lâmpada
 *
 * Argumentos aceites:
 *   java iscot.client.LightActuator [Area]
 *   java iscot.client.LightActuator <IP> <PORT> <Area>
 * Exemplo: localhost 8080 LivingRoom
 */
public class LightActuator {

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

		JFrame janela = new JFrame("Iluminação - " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));

		LightIcon lightIcon = new LightIcon();
		ActionHistoryPanel historyPanel = new ActionHistoryPanel();

		painelPrincipal.add(lightIcon, BorderLayout.CENTER);
		painelPrincipal.add(historyPanel, BorderLayout.SOUTH);

		// Botão para testes manuais
		final Areas finalArea = area;
		JButton btnSimular = new JButton("Alternar Lâmpada Manualmente");
		btnSimular.addActionListener(e -> {
			boolean novoEstado = !lightIcon.isLigada();
			AreaLightingOrderEvent eventoSimulado = new AreaLightingOrderEvent("manual", finalArea, novoEstado);
			processarOrdem(eventoSimulado, lightIcon, historyPanel);
		});
		painelPrincipal.add(btnSimular, BorderLayout.NORTH);

		janela.setContentPane(painelPrincipal);
		janela.setLocation(400, 200);
		janela.pack();
		janela.setVisible(true);

		// Conectar à rede com a classe Connection
		try {
			Connection connection = new Connection(host, port, Type.ACTUATOR, area, event -> {
				if (event instanceof AreaLightingOrderEvent) {
					AreaLightingOrderEvent ordem = (AreaLightingOrderEvent) event;
					processarOrdem(ordem, lightIcon, historyPanel);
				}
			});
			connection.start();
			janela.setTitle("Iluminação - " + area + " [" + connection.getAssignedDeviceId() + "]");

			janela.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					connection.close();
				}
			});
		} catch (Exception e) {
			System.out.println("[LightActuator] Servidor não disponível em " + host + ":" + port + " (" + e.getMessage() + "). Modo offline.");
		}
	}

	public static void processarOrdem(AreaLightingOrderEvent evento, LightIcon icon, ActionHistoryPanel history) {
		boolean ligar = evento.isTurnOn();
		icon.setLigada(ligar);
		String acao = ligar ? "Lâmpada ligada" : "Lâmpada desligada";
		history.addAction(acao);
		System.out.println("[Lâmpada @" + evento.getArea() + "] Processou ordem: " + acao);
	}
}
