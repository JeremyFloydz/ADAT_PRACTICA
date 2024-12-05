package com.example.adat.db;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

public class DB {
    private static ObjectContainer db = null;
    public static ObjectContainer getConnection() {
        if (db == null) {
            try {

                db = Db4oEmbedded.openFile("db.db4o");
                System.out.println("Conexión creada correctamente.");
            } catch (Exception e) {
                throw new RuntimeException("Error al conectar con la base de datos: " + e.getMessage());
            }
        }
        return db;
    }
    public static void closeConnection() {
        if (db != null) {
            db.close();
            System.out.println("Conexión cerrada.");
        }
    }

  
}
