package com.example.adat.dao;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.example.adat.model.Olimpiada;

import java.util.List;

public class OlimpiadaDao {

	public static void insertar(Olimpiada o, ObjectContainer db) {
		db.store(o);
	}

	public static List<Olimpiada> obtenerPorTemporada(String temporada, ObjectContainer db){
		List<Olimpiada> olimpiadas=db.query(new Predicate<Olimpiada>() {

			@Override
			public boolean match(Olimpiada o) {
				return o.getTemporada().equals(temporada);
			}
		});
		return olimpiadas;
	}

	public static Olimpiada obtenerPorNombre(String nombre, ObjectContainer db) {
		Olimpiada dep=new Olimpiada();
		dep.setNombre(nombre);
		ObjectSet<Olimpiada> set=db.queryByExample(dep);
		return set.hasNext() ? set.next() : null;
	}

}

