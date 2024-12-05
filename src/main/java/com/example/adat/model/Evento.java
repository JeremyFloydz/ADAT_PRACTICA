package com.example.adat.model;

import java.util.Objects;

public class Evento {

	private String nombre;
	private Olimpiada olimpiada;
	private Deporte deporte;

	public Evento() {}

	public Evento(String nombre, Olimpiada olimpiada, Deporte deporte) {
		this.nombre = nombre;
		this.olimpiada = olimpiada;
		this.deporte = deporte;
	}

	@Override
	public int hashCode() {
		return Objects.hash(deporte, nombre, olimpiada);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Evento other = (Evento) obj;
		return Objects.equals(deporte, other.deporte) && Objects.equals(nombre, other.nombre)
				&& Objects.equals(olimpiada, other.olimpiada);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Olimpiada getOlimpiada() {
		return olimpiada;
	}

	public void setOlimpiada(Olimpiada olimpiada) {
		this.olimpiada = olimpiada;
	}

	public Deporte getDeporte() {
		return deporte;
	}

	public void setDeporte(Deporte deporte) {
		this.deporte = deporte;
	}

}
