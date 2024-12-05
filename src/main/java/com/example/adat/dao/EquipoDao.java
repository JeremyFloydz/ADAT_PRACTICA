package com.example.adat.dao;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.example.adat.model.Equipo;

public class EquipoDao {

	public static void insertar(Equipo e, ObjectContainer db) {
		db.store(e);
	}

	public static Equipo obtenerPorNombre(String nombre, ObjectContainer db) {
		Equipo dep=new Equipo();
		dep.setNombre(nombre);
		ObjectSet<Equipo> set=db.queryByExample(dep);
		return set.hasNext() ? set.next() : null;
	}
	
}
