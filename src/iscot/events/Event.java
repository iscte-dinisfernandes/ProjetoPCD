package iscot.events;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import iscot.enums.Areas;

/**
 * Superclasse de todos os eventos do sistema.
 *
 * É "abstract" porque nunca se cria um Event genérico: cria-se sempre
 * um evento concreto (uma leitura de luz, uma ordem de rega, ...).
 *
 * Implementa Serializable para, mais tarde (fase 6), poder ser enviado
 * pela rede com ObjectOutputStream/ObjectInputStream.
 */
public abstract class Event implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

	/** Id do dispositivo que criou o evento (atribuído pelo servidor no registo). */
	private final String deviceId;
	/** Área da casa a que o evento diz respeito. */
	private final Areas area;
	/** Hora a que o evento foi criado. */
	private final LocalTime time;

	protected Event(String deviceId, Areas area) {
		this.deviceId = deviceId;
		this.area = area;
		this.time = LocalTime.now();
	}

	public String getDeviceId() {
		return deviceId;
	}

	public Areas getArea() {
		return area;
	}

	public LocalTime getTime() {
		return time;
	}

	/** Hora no formato HH:MM:SS, útil para o histórico dos atuadores. */
	public String getTimeString() {
		return time.format(HORA);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[área=" + area + ", origem=" + deviceId
				+ ", hora=" + getTimeString() + "]";
	}
}
