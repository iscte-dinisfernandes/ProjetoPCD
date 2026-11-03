package iscot.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.Event;
import iscot.events.RegistrationEvent;
import iscot.events.RegistrationResponse;

/**
 * Abstração de rede no cliente para envio e receção de eventos.
 * Implementa exatamente a abordagem clássica de sockets ensinada nas aulas do Prof. Luís Mota:
 * - Socket(InetAddress, port) (Slide 30 de Programação em Rede)
 * - ObjectOutputStream e ObjectInputStream (Slide 36 e 40 de Programação em Rede)
 * - Thread dedicada para ler eventos que chegam do servidor (Slide 34 de Programação em Rede)
 */
public class Connection {

	private final String host;
	private final int port;
	private final Type clientType;
	private final Areas area;
	private final EventListener listener;

	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String assignedDeviceId;
	private volatile boolean running = false;
	private Thread listenerThread;

	public Connection(String host, int port, Type clientType, Areas area, EventListener listener) {
		this.host = host;
		this.port = port;
		this.clientType = clientType;
		this.area = area;
		this.listener = listener;
	}

	/**
	 * Estabelece a ligação ao servidor, inicializa os canais de objetos
	 * e efetua o registo inicial do cliente.
	 */
	public synchronized void start() throws IOException, ClassNotFoundException {
		InetAddress address = InetAddress.getByName(host);
		socket = new Socket(address, port);

		// Regra dos slides (slide 38): canais criados com flush no ObjectOutputStream
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());

		// Enviar pedido de registo inicial (Requisito 2.1)
		RegistrationEvent reg = new RegistrationEvent(clientType, area);
		out.writeObject(reg);
		out.flush();

		// Ler resposta de registo do servidor
		Object respObj = in.readObject();
		if (respObj instanceof RegistrationResponse) {
			RegistrationResponse resp = (RegistrationResponse) respObj;
			if (resp.isSuccess()) {
				this.assignedDeviceId = resp.getDeviceId();
				System.out.println("[Connection] Registado com sucesso no servidor! ID: " + assignedDeviceId);
			} else {
				close();
				throw new IOException("Registo recusado pelo servidor: " + resp.getMessage());
			}
		} else {
			close();
			throw new IOException("Resposta inesperada do servidor: " + respObj);
		}

		running = true;

		// Se tiver listener registado (Decisores e Atuadores), inicia uma thread para ler do socket
		if (listener != null) {
			listenerThread = new Thread("ClientListener-" + assignedDeviceId) {
				@Override
				public void run() {
					listenLoop();
				}
			};
			listenerThread.start();
		}
	}

	/**
	 * Ciclo de escuta de eventos vindos do servidor.
	 */
	private void listenLoop() {
		try {
			while (running) {
				Object obj = in.readObject();
				if (obj instanceof Event && listener != null) {
					listener.onEventReceived((Event) obj);
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			if (running) {
				System.err.println("[Connection] Ligação terminada: " + e.getMessage());
			}
		} finally {
			close();
		}
	}

	/**
	 * Envia um evento ao servidor de forma sincronizada.
	 */
	public synchronized void sendEvent(Event event) throws IOException {
		if (out == null || socket == null || socket.isClosed()) {
			throw new IOException("Não conectado ao servidor.");
		}
		// Reset conforme slide 38 para evitar cache de objetos modificados
		out.reset();
		out.writeObject(event);
		out.flush();
	}

	public String getAssignedDeviceId() {
		return assignedDeviceId;
	}

	public Areas getArea() {
		return area;
	}

	public Type getClientType() {
		return clientType;
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
