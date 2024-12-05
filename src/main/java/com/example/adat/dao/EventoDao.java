package com.example.adat.dao;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.example.adat.model.Deporte;
import com.example.adat.model.Evento;
import com.example.adat.model.Olimpiada;

import java.util.List;

public class EventoDao {

	public static void insertar(Evento e, ObjectContainer db) {
		db.store(e);
	}

	public static Evento obtenerPorNombre(String nombre, ObjectContainer db) {
		Evento e=new Evento();
		e.setNombre(nombre);
		ObjectSet<Evento> set=db.queryByExample(e);
		return set.hasNext() ? set.next() : null;
	}

	public static List<Evento> obtenerPorOlimpiada(Olimpiada o, ObjectContainer db) {
		List<Evento> eventos=db.query(new Predicate<Evento>() {

			@Override
			public boolean match(Evento e) {
				return e.getOlimpiada().equals(o);
			}
		});
		return eventos;

	}

	public static List<Evento> obtenerPorOlimpiadaDeporte(Olimpiada o, Deporte d, ObjectContainer db) {
		List<Evento> eventos=db.query(new Predicate<Evento>() {

			@Override
			public boolean match(Evento e) {
				return e.getOlimpiada().equals(o)&&e.getDeporte().equals(d);
			}
		});
		return eventos;

	}
	
}
