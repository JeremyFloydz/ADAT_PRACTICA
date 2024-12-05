package com.example.adat.dao;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.example.adat.model.Deportista;

import java.util.List;

public class DeportistaDao {

	public static void insertar(Deportista dep, ObjectContainer db) {
		db.store(dep);
	}

	public static Deportista obtenerPorNombre(String nombre, ObjectContainer db) {
		Deportista dep=new Deportista();
		dep.setNombre(nombre);
		ObjectSet<Deportista> set=db.queryByExample(dep);
		return set.hasNext() ? set.next() : null;
	}

	public static List<Deportista> obtenerPorFragmentoNombre(String fragmentoNombre, ObjectContainer db) {
	    List<Deportista> resultados = db.query(new Predicate<Deportista>() {
	        @Override
	        public boolean match(Deportista dep) {
	            return dep.getNombre() != null && dep.getNombre().contains(fragmentoNombre);
	        }
	    });
	    return resultados.isEmpty() ? null : resultados;
	}

	
}
