package iscot.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * Painel reutilizável para o histórico de ações dos clientes atuadores.
 * Conforme Requisito 1.3 do enunciado:
 * "A cada ação tomada por um cliente atuador, este adiciona a ação tomada a um histórico
 * verificável pelo utilizador de todas as ações tomadas pelo atuador desde a última vez que foi ligado.
 * Por exemplo: uma lâmpada que tenha sido desligada, deverá conter na listagem de ações tomadas
 * uma mensagem como \"Lâmpada desligada - HH:MM:SS\", onde HH:MM:SS é o timestamp do horário da ação."
 */
public class ActionHistoryPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

	private final DefaultListModel<String> listModel;
	private final JList<String> actionList;

	public ActionHistoryPanel() {
		super(new BorderLayout(5, 5));
		setBorder(BorderFactory.createTitledBorder("Histórico de Ações"));

		listModel = new DefaultListModel<>();
		actionList = new JList<>(listModel);
		actionList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

		JScrollPane scrollPane = new JScrollPane(actionList);
		scrollPane.setPreferredSize(new Dimension(280, 150));
		add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Regista uma ação no histórico com o timestamp atual.
	 * Thread-safe: executa na EDT do Swing caso chamado por outra thread.
	 *
	 * @param actionDescription Descrição da ação (ex: "Lâmpada ligada")
	 */
	public void addAction(String actionDescription) {
		String timestamp = LocalTime.now().format(HORA_FORMATTER);
		String entry = actionDescription + " - " + timestamp;
		if (SwingUtilities.isEventDispatchThread()) {
			listModel.addElement(entry);
			actionList.ensureIndexIsVisible(listModel.getSize() - 1);
		} else {
			SwingUtilities.invokeLater(() -> {
				listModel.addElement(entry);
				actionList.ensureIndexIsVisible(listModel.getSize() - 1);
			});
		}
	}

	public void clear() {
		SwingUtilities.invokeLater(listModel::clear);
	}
}
