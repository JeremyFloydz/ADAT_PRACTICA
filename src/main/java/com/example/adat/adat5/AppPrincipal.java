package com.example.adat.adat5;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.db4o.ObjectContainer;
import com.example.adat.dao.*;
import com.example.adat.model.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import com.example.adat.db.DB;

public class AppPrincipal {

    // Método para leer y cargar los datos desde un archivo CSV
    public static void cargarDatosDesdeCsv(File archivoCsv, ObjectContainer conexionBD) {
        try (CSVReader lector = new CSVReader(new FileReader(archivoCsv))) {
            List<String[]> filas = lector.readAll();
            filas.remove(0); // Eliminar la primera fila (cabecera)

            for (String[] fila : filas) {
                Deporte deporte = DeporteDao.obtenerPorNombre(fila[12], conexionBD);
                if (deporte == null) {
                    deporte = new Deporte(fila[12]);
                }
                Deportista deportista = DeportistaDao.obtenerPorNombre(fila[1], conexionBD);
                if (deportista == null) {
                    deportista = crearDeportista(fila);
                }
                Equipo equipo = EquipoDao.obtenerPorNombre(fila[6], conexionBD);
                if (equipo == null) {
                    equipo = new Equipo(fila[6], fila[7]);
                }
                Olimpiada olimpiada = OlimpiadaDao.obtenerPorNombre(fila[8], conexionBD);
                if (olimpiada == null) {
                    olimpiada = new Olimpiada(fila[8], Integer.parseInt(fila[9]), fila[10], fila[11]);
                }
                Evento evento = EventoDao.obtenerPorNombre(fila[13], conexionBD);
                if (evento == null) {
                    evento = new Evento(fila[13], olimpiada, deporte);
                }
                Participacion participacion = ParticipacionDao.obtenerPorDeportistaEvento(deportista, evento, conexionBD);
                if (participacion == null) {
                    participacion = new Participacion(deportista, evento, equipo, Integer.parseInt(fila[3]), fila[14]);
                }

                guardarDatosEnBaseDeDatos(conexionBD, deporte, deportista, equipo, evento, olimpiada, participacion);
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    // Método auxiliar para crear un deportista
    private static Deportista crearDeportista(String[] fila) {
        float altura = convertirAFloat(fila[5]);
        int edad = convertirAInt(fila[4]);
        return new Deportista(fila[1], fila[2].charAt(0), altura, edad);
    }

    // Método auxiliar para convertir de String a float de manera segura
    private static float convertirAFloat(String valor) {
        try {
            return Float.parseFloat(valor);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    // Método auxiliar para convertir de String a int de manera segura
    private static int convertirAInt(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Método para guardar los datos en la base de datos
    private static void guardarDatosEnBaseDeDatos(ObjectContainer db, Deporte deporte, Deportista deportista,
                                                  Equipo equipo, Evento evento, Olimpiada olimpiada, Participacion participacion) {
        DeporteDao.insertar(deporte, db);
        DeportistaDao.insertar(deportista, db);
        EquipoDao.insertar(equipo, db);
        EventoDao.insertar(evento, db);
        OlimpiadaDao.insertar(olimpiada, db);
        ParticipacionDao.insertar(participacion, db);
    }

    public static void main(String[] args) {
        ObjectContainer conexionBD = new DB().getConnection();
        Scanner scanner = new Scanner(System.in);
        File archivoCsv = new File("ficheros/athlete_events-sort.csv");

        System.out.println("1:\tListado de deportistas.");
        System.out.println("2:\tActualizar medalla.");
        System.out.println("3:\tAñadir nueva participación.");
        System.out.println("4:\tEliminar participación.");
        System.out.println("0:\tSalir.");

        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1: mostrarDeportistas(conexionBD, scanner); break;
            case 2: modificarMedalla(conexionBD, scanner); break;
            case 3: registrarParticipacion(conexionBD, scanner); break;
            case 4: eliminarParticipacion(conexionBD, scanner); break;
            case 0: break;
        }

        conexionBD.close();
    }

    // Método para mostrar los deportistas participantes
    private static void mostrarDeportistas(ObjectContainer db, Scanner scanner) {
        String temporada = seleccionarTemporada(scanner);
        List<Olimpiada> olimpiadas = OlimpiadaDao.obtenerPorTemporada(temporada, db);
        Olimpiada olimpiada = elegirOlimpiada(scanner, olimpiadas);
        List<Evento> eventos = EventoDao.obtenerPorOlimpiada(olimpiada, db);

        if (eventos.isEmpty()) {
            System.out.println("No hay eventos registrados.");
        } else {
            mostrarDeportes(scanner, eventos, db);
        }
    }

    private static String seleccionarTemporada(Scanner scanner) {
        String temporada = "Summer";
        System.out.println("Elige temporada:\n1 Winter\n2 Summer");
        int respuesta = scanner.nextInt();
        if (respuesta == 1) {
            temporada = "Winter";
        }
        return temporada;
    }

    private static Olimpiada elegirOlimpiada(Scanner scanner, List<Olimpiada> olimpiadas) {
        int seleccion;
        do {
            System.out.println("Selecciona una edición olímpica:");
            for (int i = 0; i < olimpiadas.size(); i++) {
                System.out.println((i + 1) + " " + olimpiadas.get(i).getNombre());
            }
            seleccion = scanner.nextInt();
        } while (seleccion < 1 || seleccion > olimpiadas.size());
        return olimpiadas.get(seleccion - 1);
    }

    private static void mostrarDeportes(Scanner scanner, List<Evento> eventos, ObjectContainer db) {
        List<Deporte> deportesDisponibles = new ArrayList<>();
        for (Evento evento : eventos) {
            if (!deportesDisponibles.contains(evento.getDeporte())) {
                deportesDisponibles.add(evento.getDeporte());
            }
        }

        int seleccion;
        do {
            System.out.println("Elige el deporte:");
            for (int i = 0; i < deportesDisponibles.size(); i++) {
                System.out.println((i + 1) + " " + deportesDisponibles.get(i).getNombre());
            }
            seleccion = scanner.nextInt();
        } while (seleccion < 1 || seleccion > deportesDisponibles.size());

        Deporte deporte = deportesDisponibles.get(seleccion - 1);
        List<Evento> eventosFiltrados = EventoDao.obtenerPorOlimpiadaDeporte(eventos.get(0).getOlimpiada(), deporte, db);
        // Continuar el flujo según lo necesario
    }

    // Método para modificar la medalla de un deportista
    private static void modificarMedalla(ObjectContainer db, Scanner scanner) {
        int seleccion = 0;
        List<Deportista> deportistas;
        do {
            System.out.println("Introduce el nombre del deportista:");
            String nombre = scanner.nextLine();
            deportistas = DeportistaDao.obtenerPorFragmentoNombre(nombre, db);
            if (deportistas == null) {
                System.out.println("Deportista no encontrado.");
            } else {
                for (int i = 0; i < deportistas.size(); i++) {
                    System.out.println((i + 1) + " " + deportistas.get(i).getNombre());
                }
                seleccion = scanner.nextInt();
            }
        } while (seleccion < 1 || seleccion > deportistas.size());

        Deportista deportista = deportistas.get(seleccion - 1);
        // Continuar con la lógica para modificar la medalla
    }

    // Método para registrar una nueva participación
    private static void registrarParticipacion(ObjectContainer db, Scanner scanner) {
        // Pedir al usuario los datos necesarios
        System.out.println("Introduce el nombre del deportista:");
        String nombreDeportista = scanner.nextLine();

        // Buscar al deportista por nombre
        List<Deportista> deportistas = DeportistaDao.obtenerPorFragmentoNombre(nombreDeportista, db);
        if (deportistas.isEmpty()) {
            System.out.println("Deportista no encontrado.");
            return;
        }

        // Mostrar los deportistas encontrados y permitir al usuario seleccionar uno
        for (int i = 0; i < deportistas.size(); i++) {
            System.out.println((i + 1) + ". " + deportistas.get(i).getNombre());
        }
        System.out.println("Selecciona un deportista:");
        int seleccionDeportista = scanner.nextInt();
        scanner.nextLine();  // Limpiar el buffer de entrada
        Deportista deportistaSeleccionado = deportistas.get(seleccionDeportista - 1);

        // Pedir al usuario el nombre del evento
        System.out.println("Introduce el nombre del evento:");
        String nombreEvento = scanner.nextLine();

        // Buscar el evento por nombre
        Evento evento = EventoDao.obtenerPorNombre(nombreEvento, db);
        if (evento == null) {
            System.out.println("Evento no encontrado.");
            return;
        }

        // Pedir el nombre del equipo
        System.out.println("Introduce el nombre del equipo:");
        String nombreEquipo = scanner.nextLine();

        // Buscar el equipo por nombre
        Equipo equipo = EquipoDao.obtenerPorNombre(nombreEquipo, db);
        if (equipo == null) {
            System.out.println("Equipo no encontrado.");
            return;
        }

        // Preguntar por el número de medalla
        System.out.println("Introduce el número de medalla obtenida (0 si no se ha obtenido):");
        int medalla = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer de entrada

        // Preguntar por el tipo de medalla
        System.out.println("Introduce el tipo de medalla obtenida (Oro, Plata, Bronce, Ninguna):");
        String tipoMedalla = scanner.nextLine();

        // Crear la participación
        Participacion participacion = new Participacion(deportistaSeleccionado, evento, equipo, medalla, tipoMedalla);

        // Insertar la participación en la base de datos
        ParticipacionDao.insertar(participacion, db);
        System.out.println("Participación registrada correctamente.");
    }

    // Método para eliminar una participación
    private static void eliminarParticipacion(ObjectContainer db, Scanner scanner) {
        // Pedir al usuario los datos necesarios para eliminar la participación
        System.out.println("Introduce el nombre del deportista:");
        String nombreDeportista = scanner.nextLine();

        // Buscar al deportista por nombre
        List<Deportista> deportistas = DeportistaDao.obtenerPorFragmentoNombre(nombreDeportista, db);
        if (deportistas.isEmpty()) {
            System.out.println("Deportista no encontrado.");
            return;
        }

        // Mostrar los deportistas encontrados y permitir al usuario seleccionar uno
        for (int i = 0; i < deportistas.size(); i++) {
            System.out.println((i + 1) + ". " + deportistas.get(i).getNombre());
        }
        System.out.println("Selecciona un deportista:");
        int seleccionDeportista = scanner.nextInt();
        scanner.nextLine();  // Limpiar el buffer de entrada
        Deportista deportistaSeleccionado = deportistas.get(seleccionDeportista - 1);

        // Pedir al usuario el nombre del evento
        System.out.println("Introduce el nombre del evento:");
        String nombreEvento = scanner.nextLine();

        // Buscar el evento por nombre
        Evento evento = EventoDao.obtenerPorNombre(nombreEvento, db);
        if (evento == null) {
            System.out.println("Evento no encontrado.");
            return;
        }

        // Obtener la participación de este deportista en el evento
        Participacion participacion = ParticipacionDao.obtenerPorDeportistaEvento(deportistaSeleccionado, evento, db);
        if (participacion != null) {
            // Eliminar la participación de la base de datos
            ParticipacionDao.eliminar(deportistaSeleccionado, evento, db);
            System.out.println("Participación eliminada correctamente.");
        } else {
            System.out.println("No se encontró la participación del deportista en este evento.");
        }
    }


}
