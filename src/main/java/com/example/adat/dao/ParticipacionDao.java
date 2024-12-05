package com.example.adat.dao;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.example.adat.model.Deportista;
import com.example.adat.model.Evento;
import com.example.adat.model.Participacion;

import java.util.ArrayList;
import java.util.List;

public class ParticipacionDao {

	// Método para insertar una participación en la base de datos
	public static void insertar(Participacion p, ObjectContainer db) {
		db.store(p);
	}

	// Método para obtener una participación por deportista y evento
	public static Participacion obtenerPorDeportistaEvento(Deportista dep, Evento e, ObjectContainer db) {
		Participacion par = new Participacion();
		par.setDeportista(dep);
		par.setEvento(e);
		ObjectSet<Participacion> set = db.queryByExample(par);
		return set.hasNext() ? set.next() : null;
	}

	// Método para obtener todas las participaciones de un evento
	public static List<Participacion> conseguirPorEvento(Evento e, ObjectContainer db) {
		ObjectSet<Participacion> participacionesSet = db.query(new Predicate<Participacion>() {
			@Override
			public boolean match(Participacion par) {
				return par.getEvento().equals(e);
			}
		});

		List<Participacion> participaciones = new ArrayList<>();
		while (participacionesSet.hasNext()) {
			participaciones.add(participacionesSet.next());
		}
		return participaciones;
	}

	// Método para obtener todas las participaciones de un deportista
	public static List<Participacion> conseguirPorDeportista(Deportista d, ObjectContainer db) {
		ObjectSet<Participacion> participacionesSet = db.query(new Predicate<Participacion>() {
			@Override
			public boolean match(Participacion par) {
				return par.getDeportista().equals(d);
			}
		});

		List<Participacion> participaciones = new ArrayList<>();
		while (participacionesSet.hasNext()) {
			participaciones.add(participacionesSet.next());
		}
		return participaciones;
	}

	// Método para actualizar la medalla de una participación
	public static void actualizarMedallas(String medalla, Deportista dep, Evento e, ObjectContainer db) {
		Participacion p = obtenerPorDeportistaEvento(dep, e, db);
		if (p != null) {
			p.setMedalla(medalla);
			db.store(p);  // Guarda los cambios en la base de datos
		} else {
			System.out.println("No se encontró la participación.");
		}
	}

	// Método para eliminar una participación
	public static void eliminar(Deportista dep, Evento e, ObjectContainer db) {
		Participacion p = obtenerPorDeportistaEvento(dep, e, db);
		if (p != null) {
			db.delete(p);  // Elimina la participación de la base de datos
		} else {
			System.out.println("No se encontró la participación.");
		}
	}
}
