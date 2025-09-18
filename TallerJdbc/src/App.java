import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import dao.EstudianteDAO;
import model.EstadoCivil;
import model.Estudiante;

public class App {
    private final Scanner sc = new Scanner(System.in);
    private final EstudianteDAO dao = new EstudianteDAO();

    public static void main(String[] args) {
        new App().run();
    }

    private void run() {
        try {
            dao.crearTablaSiNoExiste();
        } catch (SQLException e) {
            System.err.println("No se pudo asegurar la tabla 'estudiantes':");
            e.printStackTrace();
        }

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1 -> insertarEstudiante();
                case 2 -> actualizarEstudiante();
                case 3 -> eliminarEstudiante();
                case 4 -> listarEstudiantes();
                case 5 -> consultarPorEmail();
                case 6 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción inválida");
            }
            System.out.println();
        } while (opcion != 6);
    }

    private void mostrarMenu() {
        System.out.println("==== MENÚ ESTUDIANTES ====");
        System.out.println("1. Insertar Estudiante");
        System.out.println("2. Actualizar Estudiante");
        System.out.println("3. Eliminar Estudiante");
        System.out.println("4. Consultar todos los estudiantes");
        System.out.println("5. Consultar Estudiante por email");
        System.out.println("6. Salir");
    }

    private void insertarEstudiante() {
        System.out.println("-- Insertar Estudiante --");
        String nombre = leerTexto("Nombre: ");
        String apellido = leerTexto("Apellido: ");
        String correo = leerTexto("Correo (único): ");
        int edad = leerEntero("Edad: ");
        EstadoCivil estadoCivil = leerEstadoCivil();
        Estudiante e = new Estudiante(nombre, apellido, correo, edad, estadoCivil);
        try {
            dao.crear(e);
            System.out.println("Insertado con id: " + e.getId());
        } catch (SQLException ex) {
            if (mensajeUnique(ex)) {
                System.out.println("Error: el correo ya existe.");
            } else {
                ex.printStackTrace();
            }
        }
    }

    private void actualizarEstudiante() {
        System.out.println("-- Actualizar Estudiante por correo --");
        String correo = leerTexto("Correo (clave): ");
        String nombre = leerTexto("Nuevo nombre: ");
        String apellido = leerTexto("Nuevo apellido: ");
        int edad = leerEntero("Nueva edad: ");
        EstadoCivil estadoCivil = leerEstadoCivil();
        Estudiante e = new Estudiante(null, nombre, apellido, correo, edad, estadoCivil);
        try {
            dao.actualizarPorCorreo(correo, e);
            System.out.println("Actualización realizada (si existía).");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void eliminarEstudiante() {
        System.out.println("-- Eliminar Estudiante por correo --");
        String correo = leerTexto("Correo: ");
        try {
            dao.eliminarPorCorreo(correo);
            System.out.println("Eliminación realizada (si existía).");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void listarEstudiantes() {
        System.out.println("-- Listar Estudiantes --");
        try {
            List<Estudiante> lista = dao.listarTodos();
            if (lista.isEmpty()) {
                System.out.println("No hay registros.");
            } else {
                lista.forEach(System.out::println);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void consultarPorEmail() {
        System.out.println("-- Consultar por Email --");
        String correo = leerTexto("Correo: ");
        try {
            Estudiante e = dao.buscarPorCorreo(correo);
            System.out.println(e != null ? e : "No encontrado");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private int leerEntero(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine();
            try {
                return Integer.parseInt(s.trim());
            } catch (Exception ignored) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }
    
    private String leerTexto(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private EstadoCivil leerEstadoCivil() {
        System.out.println("Estado Civil:");
        EstadoCivil[] vals = EstadoCivil.values();
        for (int i = 0; i < vals.length; i++) {
            System.out.printf("%d) %s\n", i, vals[i]);
        }
        int idx;
        while (true) {
            idx = leerEntero("Seleccione (0-" + (vals.length - 1) + "): ");
            if (idx >= 0 && idx < vals.length) break;
            System.out.println("Opción inválida.");
        }
        return vals[idx];
    }

    private boolean mensajeUnique(SQLException ex) {
        String m = ex.getMessage();
        return m != null && (m.contains("Duplicate") || m.contains("duplicate") || m.contains("UNIQUE"));
    }
}