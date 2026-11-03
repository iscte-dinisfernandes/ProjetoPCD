package iscot.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.Event;
import iscot.events.RegistrationEvent;
import iscot.events.RegistrationResponse;

/**
 * Thread dedicada a gerir a ligação com um cliente remoto (Requisito 1.1).
 * Responsável por:
 * - Realizar o aperto de mão inicial e validação de tipo/área;
 * - Receber eventos enviados pelo cliente e encaminhá-los para o Server;
 * - Enviar eventos para o cliente através do seu ObjectOutputStream.
 */
public class ClientHandler extends Thread {

	private final Socket socket;
	private final Server server;

	private ObjectOutputStream out;
	private ObjectInputStream in;

	private String deviceId;
	private Type clientType;
	private Areas area;
	private volatile boolean running = true;

	public ClientHandler(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			// Nos slides: abrir canais de objetos (out primeiro no servidor também, seguido de flush)
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());

			// 1. Receber registo inicial
			Object firstObj = in.readObject();
			if (!(firstObj instanceof RegistrationEvent)) {
				sendReject("Primeiro evento tem de ser RegistrationEvent");
				return;
			}

			RegistrationEvent reg = (RegistrationEvent) firstObj;
			this.clientType = reg.getType();
			this.area = reg.getArea();

			// Validar se o tipo é válido (Requisito 2.1: se o tipo não existir a ligação é recusada)
			if (clientType == null || clientType == Type.DWC) {
				sendReject("Tipo de dispositivo desconhecido ou inválido: " + clientType);
				return;
			}

			// Gerar ID único no servidor
			this.deviceId = server.registerClient(this);

			// Responder com sucesso
			RegistrationResponse okResp = new RegistrationResponse(deviceId, area, true, "Registado com sucesso");
			out.writeObject(okResp);
			out.flush();

			System.out.println("[ClientHandler] Cliente registado com sucesso: ID=" + deviceId
					+ " (" + clientType + " em " + area + ")");

			// 2. Ciclo de leitura de eventos do cliente
			while (running) {
				Object obj = in.readObject();
				if (obj instanceof Event) {
					Event event = (Event) obj;
					System.out.println("[ClientHandler @" + deviceId + "] Evento recebido do cliente: " + event);
					server.onEventReceived(this, event);
				}
			}

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("[ClientHandler] Cliente " + (deviceId != null ? deviceId : "não-registado") + " desligou-se.");
		} finally {
			close();
			if (deviceId != null) {
				server.unregisterClient(this);
			}
		}
	}

	/**
	 * Envia um evento a este cliente de forma sincronizada.
	 */
	public synchronized void sendEvent(Event event) {
		if (!running || out == null) return;
		try {
			out.reset();
			out.writeObject(event);
			out.flush();
		} catch (IOException e) {
			System.err.println("[ClientHandler] Erro ao enviar evento para " + deviceId + ": " + e.getMessage());
			close();
		}
	}

	private void sendReject(String reason) {
		try {
			System.err.println("[ClientHandler] Recusando ligação: " + reason);
			RegistrationResponse failResp = new RegistrationResponse(null, area, false, reason);
			out.writeObject(failResp);
			out.flush();
		} catch (IOException ignored) {}
		close();
	}

	public String getDeviceId() {
		return deviceId;
	}

	public Type getClientType() {
		return clientType;
	}

	public Areas getArea() {
		return area;
	}

	public synchronized void close() {
		running = false;
		try {
			if (in != null) in.close();
		} catch (IOException ignored) {}
		try {
			if (out != null) out.close();
		} catch (IOException ignored) {}
		try {
			if (socket != null && !socket.isClosed()) socket.close();
		} catch (IOException ignored) {}
	}
}
