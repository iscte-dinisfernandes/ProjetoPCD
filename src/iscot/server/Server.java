package iscot.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.Event;
import iscot.events.HighLevelOrderEvent;
import iscot.events.SensorEvent;

/**
 * Servidor central IscoT (Message Broker).
 *
 * Segue a arquitetura clássica dos slides de PCD:
 * - ServerSocket num porto conhecido;
 * - Ciclo de aceitação de ligações (accept bloqueante);
 * - Lança uma thread dedicada (ClientHandler) por cada cliente ligado;
 * - Gere registos, atribuição de IDs únicos e encaminhamento de eventos.
 */
public class Server {

	public static final int DEFAULT_PORT = 8080;

	private final int port;
	private final List<ClientHandler> clients = new ArrayList<>();
	private final Topics topics = new Topics();
	private int idCounter = 1;
	private volatile boolean running = true;
	private ServerSocket serverSocket;

	public Server(int port) {
		this.port = port;
	}

	public void start() throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("==================================================");
		System.out.println("   Servidor IscoT iniciado no porto " + port);
		System.out.println("==================================================");

		try {
			while (running) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("[Server] Nova ligação recebida de " + clientSocket.getRemoteSocketAddress());
				ClientHandler handler = new ClientHandler(clientSocket, this);
				handler.start();
			}
		} catch (IOException e) {
			if (running) {
				System.err.println("[Server] Erro no ServerSocket: " + e.getMessage());
			}
		} finally {
			stop();
		}
	}

	/**
	 * Regista um novo cliente e atribui-lhe um ID único no sistema (Requisito 2.1).
	 * Método sincronizado (recurso partilhado protegido).
	 */
	public synchronized String registerClient(ClientHandler handler) {
		String id = "dev-" + handler.getClientType().name().toLowerCase() + "-" + (idCounter++);
		clients.add(handler);
		return id;
	}

	/**
	 * Remove um cliente quando este se desliga.
	 */
	public synchronized void unregisterClient(ClientHandler handler) {
		clients.remove(handler);
		System.out.println("[Server] Cliente removido: " + handler.getDeviceId() + " (Total ativos: " + clients.size() + ")");
	}

	/**
	 * Callback chamado quando um ClientHandler recebe um evento do seu cliente.
	 * 1. Publica nos tópicos/áreas;
	 * 2. Encaminha o evento para os clientes subscritos (Fases 5 a 7):
	 *    - Sensores emitem SensorEvent -> entregue a Decisores da mesma área;
	 *    - Decisores emitem HighLevelOrderEvent -> entregue a Atuadores da mesma área.
	 */
	public void onEventReceived(ClientHandler sender, Event event) {
		synchronized (this) {
			// Armazena na estrutura de tópicos
			topics.publish(event);
		}

		routeEvent(event);
	}

	/**
	 * Encaminha o evento aos clientes interessados no tópico e área respetivos.
	 */
	private void routeEvent(Event event) {
		Areas area = event.getArea();
		List<ClientHandler> targets = new ArrayList<>();

		synchronized (this) {
			if (event instanceof SensorEvent) {
				// Leituras de sensores interessam a Decisores que operem na mesma área
				for (ClientHandler client : clients) {
					if (client.getClientType() == Type.DECIDER && client.getArea() == area) {
						targets.add(client);
					}
				}
			} else if (event instanceof HighLevelOrderEvent) {
				// Ordens de alto nível interessam a Atuadores da mesma área
				for (ClientHandler client : clients) {
					if (client.getClientType() == Type.ACTUATOR && client.getArea() == area) {
						targets.add(client);
					}
				}
			}
		}

		for (ClientHandler target : targets) {
			target.sendEvent(event);
		}
	}

	public synchronized void stop() {
		running = false;
		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (IOException ignored) {}

		synchronized (this) {
			for (ClientHandler client : clients) {
				client.close();
			}
			clients.clear();
		}
		System.out.println("[Server] Servidor IscoT encerrado.");
	}

	public static void main(String[] args) {
		int port = DEFAULT_PORT;
		if (args.length >= 1) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("Porto inválido, a usar " + DEFAULT_PORT);
			}
		}

		Server server = new Server(port);
		try {
			server.start();
		} catch (IOException e) {
			System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
		}
	}
}
