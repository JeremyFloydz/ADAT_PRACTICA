package com.example.adat.dao;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.example.adat.model.Deporte;

public class DeporteDao {

	public static void insertar(Deporte dep, ObjectContainer db) {
		db.store(dep);
	}

	public static Deporte obtenerPorNombre(String nombre, ObjectContainer db) {
		Deporte dep=new Deporte();
		dep.setNombre(nombre);
		ObjectSet<Deporte> set=db.queryByExample(dep);
		return set.hasNext() ? set.next() : null;
	}

}
