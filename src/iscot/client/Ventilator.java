package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.ActionHistoryPanel;
import iscot.client.gui.VentilatorIcon;
import iscot.enums.Areas;
import iscot.events.AreaVentilationOrderEvent;

/**
 * Cliente Atuador: Ventilador
 *
 * Argumentos: IP PORT AreaDispositivo [TipoDispositivo]
 * Exemplo: localhost 8080 LivingRoom
 */
public class Ventilator {

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length < 1) {
			System.err.println("Argumentos insuficientes! Uso: <Area> ou <IP> <PORT> <Area>");
			return;
		}

		// Permite passar apenas a Área para testes da Fase 3, ou os 3/4 argumentos padrão
		String areaStr = (args.length == 1) ? args[0] : (args.length >= 3 ? args[2] : args[0]);
		Areas area;
		try {
			area = Areas.valueOf(areaStr);
		} catch (IllegalArgumentException e) {
			System.err.println("Área inválida: " + areaStr + ". Áreas válidas: Kitchen, WC, LivingRoom, BedRoom, Garden");
			return;
		}

		JFrame janela = new JFrame("Ventilador - " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));

		VentilatorIcon ventilatorIcon = new VentilatorIcon();
		ActionHistoryPanel historyPanel = new ActionHistoryPanel();

		painelPrincipal.add(ventilatorIcon, BorderLayout.CENTER);
		painelPrincipal.add(historyPanel, BorderLayout.SOUTH);

		// Botão para simulação local de eventos da Fase 3
		JButton btnSimular = new JButton("Simular Alternância (Fase 3)");
		btnSimular.addActionListener(e -> {
			boolean novoEstado = !ventilatorIcon.isLigado();
			// Simula a receção de um AreaVentilationOrderEvent
			AreaVentilationOrderEvent eventoSimulado = new AreaVentilationOrderEvent("decisor-simulado", area, novoEstado);
			processarOrdem(eventoSimulado, ventilatorIcon, historyPanel);
		});
		painelPrincipal.add(btnSimular, BorderLayout.NORTH);

		janela.setContentPane(painelPrincipal);
		janela.setLocation(200, 200);
		janela.pack();
		janela.setVisible(true);

		// Demonstração automática inicial da Fase 3 após 2 segundos
		new Thread(() -> {
			try {
				Thread.sleep(2000);
				processarOrdem(new AreaVentilationOrderEvent("decisor-auto", area, true), ventilatorIcon, historyPanel);
				Thread.sleep(3000);
				processarOrdem(new AreaVentilationOrderEvent("decisor-auto", area, false), ventilatorIcon, historyPanel);
			} catch (InterruptedException ignored) {}
		}).start();
	}

	/**
	 * Processa o evento de ordem, atualiza o ícone e adiciona entrada no histórico.
	 */
	public static void processarOrdem(AreaVentilationOrderEvent evento, VentilatorIcon icon, ActionHistoryPanel history) {
		boolean ligar = evento.isTurnOn();
		icon.setLigado(ligar);
		String acao = ligar ? "Ventilador ligado" : "Ventilador desligado";
		history.addAction(acao);
		System.out.println("[Ventilador @" + evento.getArea() + "] Recebeu: " + evento + " -> " + acao);
	}
}
