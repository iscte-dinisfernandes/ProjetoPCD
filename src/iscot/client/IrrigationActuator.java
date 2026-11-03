package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.ActionHistoryPanel;
import iscot.client.gui.IrrigationIcon;
import iscot.enums.Areas;
import iscot.events.AreaIrrigationOrderEvent;

/**
 * Cliente Atuador: Irrigação / Sistema de Rega
 *
 * Argumentos: IP PORT AreaDispositivo [TipoDispositivo]
 * Exemplo: localhost 8080 Garden
 */
public class IrrigationActuator {

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

		JFrame janela = new JFrame("Irrigação - " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));

		IrrigationIcon irrigationIcon = new IrrigationIcon();
		ActionHistoryPanel historyPanel = new ActionHistoryPanel();

		painelPrincipal.add(irrigationIcon, BorderLayout.CENTER);
		painelPrincipal.add(historyPanel, BorderLayout.SOUTH);

		// Botão para simulação local de eventos da Fase 3
		JButton btnSimular = new JButton("Simular Alternância Rega (Fase 3)");
		btnSimular.addActionListener(e -> {
			boolean novoEstado = !irrigationIcon.isAtiva();
			AreaIrrigationOrderEvent eventoSimulado = new AreaIrrigationOrderEvent("decisor-simulado", area, novoEstado);
			processarOrdem(eventoSimulado, irrigationIcon, historyPanel);
		});
		painelPrincipal.add(btnSimular, BorderLayout.NORTH);

		janela.setContentPane(painelPrincipal);
		janela.setLocation(800, 200);
		janela.pack();
		janela.setVisible(true);

		// Demonstração automática inicial da Fase 3
		new Thread(() -> {
			try {
				Thread.sleep(2000);
				processarOrdem(new AreaIrrigationOrderEvent("decisor-auto", area, true), irrigationIcon, historyPanel);
				Thread.sleep(3000);
				processarOrdem(new AreaIrrigationOrderEvent("decisor-auto", area, false), irrigationIcon, historyPanel);
			} catch (InterruptedException ignored) {}
		}).start();
	}

	public static void processarOrdem(AreaIrrigationOrderEvent evento, IrrigationIcon icon, ActionHistoryPanel history) {
		boolean ligar = evento.isTurnOn();
		icon.setAtiva(ligar);
		String acao = ligar ? "Rega ativada" : "Rega desativada";
		history.addAction(acao);
		System.out.println("[Irrigação @" + evento.getArea() + "] Recebeu: " + evento + " -> " + acao);
	}
}
