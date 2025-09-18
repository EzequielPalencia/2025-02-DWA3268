package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Estudiante;
import model.EstadoCivil;
import util.DB;

public class EstudianteDAO {
    
    public void crearTablaSiNoExiste() throws SQLException {
        final String ddl = "CREATE TABLE IF NOT EXISTS estudiantes (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "nombre VARCHAR(100) NOT NULL," +
                "apellido VARCHAR(100) NOT NULL," +
                "correo VARCHAR(150) NOT NULL UNIQUE," +
                "edad INT NOT NULL," +
                "estado_civil INT NOT NULL" +
                ")";
        try (Connection con = DB.getConnection(); Statement st = con.createStatement()) {
            st.executeUpdate(ddl);
        }
    }

    public void crear(Estudiante e) throws SQLException {
        final String sql = "INSERT INTO estudiantes (nombre, apellido, correo, edad, estado_civil) VALUES (?,?,?,?,?)";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getApellido());
            ps.setString(3, e.getCorreo());
            ps.setInt(4, e.getEdad());
            if (e.getEstadoCivil() == null) {
                throw new IllegalArgumentException("El estado civil no puede ser null");
            }
            ps.setInt(5, e.getEstadoCivil().ordinal());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setId(rs.getLong(1));
                }
            }
        }
    }

    public void actualizarPorCorreo(String correo, Estudiante e) throws SQLException {
        final String sql = "UPDATE estudiantes SET nombre=?, apellido=?, edad=?, estado_civil=? WHERE correo=?";
        try (Connection con = DB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getApellido());
            ps.setInt(3, e.getEdad());
            if (e.getEstadoCivil() == null) {
                throw new IllegalArgumentException("El estado civil no puede ser null");
            }
            ps.setInt(4, e.getEstadoCivil().ordinal());
            ps.setString(5, correo);
            ps.executeUpdate();
        }
    }

    public void eliminarPorCorreo(String correo) throws SQLException {
        final String sql = "DELETE FROM estudiantes WHERE correo=?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.executeUpdate();
        }
    }

    public List<Estudiante> listarTodos() throws SQLException {
        final String sql = "SELECT id, nombre, apellido, correo, edad, estado_civil FROM estudiantes ORDER BY id";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Estudiante> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            return lista;
        }
    }

    public Estudiante buscarPorCorreo(String correo) throws SQLException {
        final String sql = "SELECT id, nombre, apellido, correo, edad, estado_civil FROM estudiantes WHERE correo=?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    private Estudiante mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        String correo = rs.getString("correo");
        int edad = rs.getInt("edad");
        int estadoOrdinal = rs.getInt("estado_civil");
        EstadoCivil estado = EstadoCivil.fromOrdinal(estadoOrdinal);
        return new Estudiante(id, nombre, apellido, correo, edad, estado);
    }
}
