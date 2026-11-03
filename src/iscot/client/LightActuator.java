package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.ActionHistoryPanel;
import iscot.client.gui.LightIcon;
import iscot.enums.Areas;
import iscot.events.AreaLightingOrderEvent;

/**
 * Cliente Atuador: Iluminação / Lâmpada
 *
 * Argumentos: IP PORT AreaDispositivo [TipoDispositivo]
 * Exemplo: localhost 8080 LivingRoom
 */
public class LightActuator {

	public static void main(String[] args) throws IOException, InterruptedException {
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

		JFrame janela = new JFrame("Iluminação - " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));

		LightIcon lightIcon = new LightIcon();
		ActionHistoryPanel historyPanel = new ActionHistoryPanel();

		painelPrincipal.add(lightIcon, BorderLayout.CENTER);
		painelPrincipal.add(historyPanel, BorderLayout.SOUTH);

		// Botão para simulação local de eventos da Fase 3
		JButton btnSimular = new JButton("Simular Alternância Lâmpada (Fase 3)");
		btnSimular.addActionListener(e -> {
			boolean novoEstado = !lightIcon.isLigada();
			AreaLightingOrderEvent eventoSimulado = new AreaLightingOrderEvent("decisor-simulado", area, novoEstado);
			processarOrdem(eventoSimulado, lightIcon, historyPanel);
		});
		painelPrincipal.add(btnSimular, BorderLayout.NORTH);

		janela.setContentPane(painelPrincipal);
		janela.setLocation(400, 200);
		janela.pack();
		janela.setVisible(true);

		// Demonstração automática inicial da Fase 3 após 2 segundos
		new Thread(() -> {
			try {
				Thread.sleep(2000);
				processarOrdem(new AreaLightingOrderEvent("decisor-auto", area, true), lightIcon, historyPanel);
				Thread.sleep(3000);
				processarOrdem(new AreaLightingOrderEvent("decisor-auto", area, false), lightIcon, historyPanel);
			} catch (InterruptedException ignored) {}
		}).start();
	}

	public static void processarOrdem(AreaLightingOrderEvent evento, LightIcon icon, ActionHistoryPanel history) {
		boolean ligar = evento.isTurnOn();
		icon.setLigada(ligar);
		String acao = ligar ? "Lâmpada ligada" : "Lâmpada desligada";
		history.addAction(acao);
		System.out.println("[Lâmpada @" + evento.getArea() + "] Recebeu: " + evento + " -> " + acao);
	}
}
