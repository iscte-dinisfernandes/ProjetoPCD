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
import iscot.events.AreaTemperatureOrderEvent;

/**
 * Cliente Atuador: Ar Condicionado
 *
 * Argumentos: IP PORT AreaDispositivo [TipoDispositivo]
 * Exemplo: localhost 8080 BedRoom
 */
public class ACActuator {

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

		JFrame janela = new JFrame("Ar Condicionado - " + area);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));

		ACIcon acIcon = new ACIcon();
		ActionHistoryPanel historyPanel = new ActionHistoryPanel();

		painelPrincipal.add(acIcon, BorderLayout.CENTER);
		painelPrincipal.add(historyPanel, BorderLayout.SOUTH);

		// Array com os vários modos para alternar na simulação
		AirConditioningMode[] modos = {
			AirConditioningMode.Aquecimento,
			AirConditioningMode.Frio,
			AirConditioningMode.Ventilação,
			AirConditioningMode.Desligado
		};
		final int[] modoIndex = {0};

		JButton btnSimular = new JButton("Simular Próximo Modo AC (Fase 3)");
		btnSimular.addActionListener(e -> {
			AirConditioningMode proximoModo = modos[modoIndex[0] % modos.length];
			modoIndex[0]++;
			AreaTemperatureOrderEvent eventoSimulado = new AreaTemperatureOrderEvent("decisor-simulado", area, proximoModo);
			processarOrdem(eventoSimulado, acIcon, historyPanel);
		});
		painelPrincipal.add(btnSimular, BorderLayout.NORTH);

		janela.setContentPane(painelPrincipal);
		janela.setLocation(600, 200);
		janela.pack();
		janela.setVisible(true);

		// Demonstração automática inicial da Fase 3
		new Thread(() -> {
			try {
				Thread.sleep(2000);
				processarOrdem(new AreaTemperatureOrderEvent("decisor-auto", area, AirConditioningMode.Frio), acIcon, historyPanel);
				Thread.sleep(3000);
				processarOrdem(new AreaTemperatureOrderEvent("decisor-auto", area, AirConditioningMode.Aquecimento), acIcon, historyPanel);
				Thread.sleep(3000);
				processarOrdem(new AreaTemperatureOrderEvent("decisor-auto", area, AirConditioningMode.Desligado), acIcon, historyPanel);
			} catch (InterruptedException ignored) {}
		}).start();
	}

	public static void processarOrdem(AreaTemperatureOrderEvent evento, ACIcon icon, ActionHistoryPanel history) {
		AirConditioningMode modo = evento.getMode();
		icon.setModo(modo);
		String acao = (modo == AirConditioningMode.Desligado)
				? "Ar condicionado desligado"
				: "Ar condicionado em modo " + modo;
		history.addAction(acao);
		System.out.println("[AC @" + evento.getArea() + "] Recebeu: " + evento + " -> " + acao);
	}
}
