package iscot.client;

import iscot.enums.Areas;

/**
 * Lança simultaneamente todas as janelas de Sensores e Atuadores
 * para demonstração presencial da Entrega Intermédia (Fases 1 a 3).
 *
 * Demonstra:
 * - Janelas com todos os sensores (Humidade Ar, Temperatura, Luz, Humidade Solo)
 *   a gerar e imprimir eventos na consola quando se interage com os controlos.
 * - Janelas com todos os atuadores (Ventilador, Iluminação, AC, Irrigação)
 *   com o componente gráfico dinâmico, histórico de ações com timestamp
 *   e botão para simulação de ordens em tempo real.
 */
public class GuiDemoAll {

	public static void main(String[] args) {
		System.out.println("=== A INICIAR DEMO DA GUI (Fases 1 a 3 - PCD) ===");

		// 1. Lançar Atuadores
		new Thread(() -> {
			try {
				Ventilator.main(new String[]{Areas.LivingRoom.name()});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		new Thread(() -> {
			try {
				LightActuator.main(new String[]{Areas.LivingRoom.name()});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		new Thread(() -> {
			try {
				ACActuator.main(new String[]{Areas.BedRoom.name()});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		new Thread(() -> {
			try {
				IrrigationActuator.main(new String[]{Areas.Garden.name()});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		// 2. Lançar Sensores
		new Thread(() -> {
			try {
				HumiditySensor.main(new String[]{Areas.LivingRoom.name()});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		new Thread(() -> {
			try {
				LightSensor.main(new String[]{Areas.LivingRoom.name()});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		new Thread(() -> {
			try {
				TemperatureSensor.main(new String[]{Areas.BedRoom.name()});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		new Thread(() -> {
			try {
				GroundMoistureSensor.main(new String[]{Areas.Garden.name()});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		System.out.println("Todas as janelas foram inicializadas.");
	}
}
