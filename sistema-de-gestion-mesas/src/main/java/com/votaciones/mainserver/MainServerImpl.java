package com.votaciones.mainserver;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MainServerImpl implements VotingSystem {
    private final DatabaseConnection dbConnection;
    private final ConcurrentHashMap<Integer, String> estadoMesas;
    private boolean votacionCerrada;

    public MainServerImpl() throws RemoteException {
        this.dbConnection = DatabaseConnection.getInstance();
        this.estadoMesas = new ConcurrentHashMap<>();
        this.votacionCerrada = false;
    }

    @Override
    public String[] obtenerEstadoMesas() throws RemoteException {
        List<String> estados = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT m.id, m.numero, m.estado, col.nombre as colegio, c.nombre as ciudad " +
                 "FROM mesas_votacion m " +
                 "JOIN colegios col ON m.colegio_id = col.id " +
                 "JOIN ciudades c ON col.ciudad_id = c.id " +
                 "ORDER BY m.id")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                estados.add(String.format("%d|%d|%s|%s|%s",
                    rs.getInt("id"),
                    rs.getInt("numero"),
                    rs.getString("estado"),
                    rs.getString("colegio"),
                    rs.getString("ciudad")));
            }
        } catch (SQLException e) {
            throw new RemoteException("Error al obtener estado de mesas", e);
        }
        return estados.toArray(new String[0]);
    }

    @Override
    public String obtenerEstadoMesa(int mesaId) throws RemoteException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT estado FROM mesas_votacion WHERE id = ?")) {
            stmt.setInt(1, mesaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("estado");
            }
        } catch (SQLException e) {
            throw new RemoteException("Error al obtener estado de mesa", e);
        }
        return "NO_ENCONTRADA";
    }

    @Override
    public void actualizarEstadoMesa(int mesaId, String estado) throws RemoteException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE mesas_votacion SET estado = ?, ultima_actualizacion = CURRENT_TIMESTAMP WHERE id = ?")) {
            stmt.setString(1, estado);
            stmt.setInt(2, mesaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RemoteException("Error al actualizar estado de mesa", e);
        }
    }

    @Override
    public int obtenerTotalVotos() throws RemoteException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM votos")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RemoteException("Error al obtener total de votos", e);
        }
        return 0;
    }

    @Override
    public String[] generarResultadosParciales() throws RemoteException {
        List<String> resultados = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT m.id, m.numero, col.nombre as colegio, c.nombre as ciudad, COUNT(v.id) as total_votos " +
                 "FROM mesas_votacion m " +
                 "JOIN colegios col ON m.colegio_id = col.id " +
                 "JOIN ciudades c ON col.ciudad_id = c.id " +
                 "LEFT JOIN votos v ON m.id = v.mesa_id " +
                 "GROUP BY m.id, m.numero, col.nombre, c.nombre")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultados.add(String.format("%d|%d|%s|%s|%d",
                    rs.getInt("id"),
                    rs.getInt("numero"),
                    rs.getString("colegio"),
                    rs.getString("ciudad"),
                    rs.getInt("total_votos")));
            }
        } catch (SQLException e) {
            throw new RemoteException("Error al generar resultados parciales", e);
        }
        return resultados.toArray(new String[0]);
    }

    @Override
    public String[] generarResultadosFinales() throws RemoteException {
        if (!votacionCerrada) {
            throw new RemoteException("La votación no ha sido cerrada");
        }
        List<String> resultados = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT c.nombres, c.apellidos, c.partido_politico, COUNT(v.id) as total_votos " +
                 "FROM candidatos c LEFT JOIN votos v ON c.id = v.candidato_id " +
                 "GROUP BY c.id, c.nombres, c.apellidos, c.partido_politico")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultados.add(String.format("%s %s|%s|%d",
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("partido_politico"),
                    rs.getInt("total_votos")));
            }
        } catch (SQLException e) {
            throw new RemoteException("Error al generar resultados finales", e);
        }
        return resultados.toArray(new String[0]);
    }

    @Override
    public void registrarAlerta(String tipo, String mensaje, int mesaId, String severidad) throws RemoteException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO alertas (tipo, mensaje, mesa_id, severidad, fecha_hora) " +
                 "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)")) {
            stmt.setString(1, tipo);
            stmt.setString(2, mensaje);
            stmt.setInt(3, mesaId);
            stmt.setString(4, severidad);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RemoteException("Error al registrar alerta", e);
        }
    }

    @Override
    public void cerrarVotacion() throws RemoteException {
        votacionCerrada = true;
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE mesas_votacion SET estado = 'CERRADA'")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RemoteException("Error al cerrar votación", e);
        }
    }

    @Override
    public String[] obtenerZonasElectorales() throws RemoteException {
        List<String> zonas = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT z.id, z.nombre, z.codigo, c.nombre as ciudad, " +
                 "COUNT(DISTINCT m.id) as total_mesas, " +
                 "COUNT(DISTINCT a.ciudadano_id) as total_ciudadanos " +
                 "FROM zonas_electorales z " +
                 "LEFT JOIN ciudades c ON z.ciudad_id = c.id " +
                 "LEFT JOIN colegios col ON z.id = col.zona_id " +
                 "LEFT JOIN mesas_votacion m ON col.id = m.colegio_id " +
                 "LEFT JOIN asignaciones_ciudadanos a ON z.id = a.zona_id " +
                 "GROUP BY z.id, z.nombre, z.codigo, c.nombre")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                zonas.add(String.format("%d|%s|%s|%s|%d|%d",
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("codigo"),
                    rs.getString("ciudad"),
                    rs.getInt("total_mesas"),
                    rs.getInt("total_ciudadanos")));
            }
        } catch (SQLException e) {
            throw new RemoteException("Error al obtener zonas electorales", e);
        }
        return zonas.toArray(new String[0]);
    }

    @Override
    public boolean validarMesaZonaAsignada(String documento, int zonaId, int mesaId) throws RemoteException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT COUNT(*) FROM asignaciones_ciudadanos " +
                 "WHERE ciudadano_id = (SELECT id FROM ciudadanos WHERE documento = ?) " +
                 "AND zona_id = ? AND mesa_id = ? AND estado = 'ACTIVA'")) {
            stmt.setString(1, documento);
            stmt.setInt(2, zonaId);
            stmt.setInt(3, mesaId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RemoteException("Error al validar asignación", e);
        }
    }

    @Override
    public void registrarCedula(String documento, int mesaId) throws RemoteException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO votos (candidato_id, mesa_id) " +
                 "SELECT NULL, ? FROM ciudadanos WHERE documento = ?")) {
            stmt.setInt(1, mesaId);
            stmt.setString(2, documento);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RemoteException("Error al registrar cédula", e);
        }
    }

    @Override
    public String[] obtenerEstadisticasZona(int zonaId) throws RemoteException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT " +
                 "COUNT(DISTINCT m.id) as total_mesas, " +
                 "COUNT(DISTINCT CASE WHEN m.estado = 'ACTIVA' THEN m.id END) as mesas_activas, " +
                 "COUNT(DISTINCT a.ciudadano_id) as total_ciudadanos, " +
                 "COUNT(DISTINCT v.id) as total_votos " +
                 "FROM zonas_electorales z " +
                 "LEFT JOIN colegios col ON z.id = col.zona_id " +
                 "LEFT JOIN mesas_votacion m ON col.id = m.colegio_id " +
                 "LEFT JOIN asignaciones_ciudadanos a ON z.id = a.zona_id " +
                 "LEFT JOIN votos v ON m.id = v.mesa_id " +
                 "WHERE z.id = ?")) {
            stmt.setInt(1, zonaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new String[] {
                    String.valueOf(rs.getInt("total_mesas")),
                    String.valueOf(rs.getInt("mesas_activas")),
                    String.valueOf(rs.getInt("total_ciudadanos")),
                    String.valueOf(rs.getInt("total_votos"))
                };
            }
        } catch (SQLException e) {
            throw new RemoteException("Error al obtener estadísticas de zona", e);
        }
        return new String[] {"0", "0", "0", "0"};
    }

    @Override
    public String[] obtenerMesasPorZona(int zonaId) throws RemoteException {
        List<String> mesas = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT m.id, m.numero, m.estado, col.nombre as colegio, c.nombre as ciudad " +
                 "FROM mesas_votacion m " +
                 "JOIN colegios col ON m.colegio_id = col.id " +
                 "JOIN ciudades c ON col.ciudad_id = c.id " +
                 "WHERE col.zona_id = ? " +
                 "ORDER BY m.id")) {
            stmt.setInt(1, zonaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mesas.add(String.format("%d|%d|%s|%s|%s",
                    rs.getInt("id"),
                    rs.getInt("numero"),
                    rs.getString("estado"),
                    rs.getString("colegio"),
                    rs.getString("ciudad")));
            }
        } catch (SQLException e) {
            throw new RemoteException("Error al obtener mesas por zona", e);
        }
        return mesas.toArray(new String[0]);
    }

    // Métodos para GetMesaID y GetZonaIDVotaciones
    public int getMesaId(int numero, int colegioId) throws RemoteException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT id FROM mesas_votacion WHERE numero = ? AND colegio_id = ?")) {
            stmt.setInt(1, numero);
            stmt.setInt(2, colegioId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            throw new RemoteException("Error al obtener mesaId", e);
        }
        return -1;
    }

    public int getZonaId(String codigo) throws RemoteException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT id FROM zonas_electorales WHERE codigo = ?")) {
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            throw new RemoteException("Error al obtener zonaId", e);
        }
        return -1;
    }

    // Método para registrar un voto
    public int registrarVoto(String documento, int mesaId, String candidato) {
        try (Connection conn = dbConnection.getConnection()) {
            // Verificar si el ciudadano existe
            PreparedStatement stmt = conn.prepareStatement("SELECT id FROM ciudadanos WHERE documento = ?");
            stmt.setString(1, documento);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) return 3; // No aparece en la BD
            int ciudadanoId = rs.getInt("id");

            // Verificar si ya votó en esta mesa
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM votos WHERE ciudadano_id = ? AND mesa_id = ?");
            stmt.setInt(1, ciudadanoId);
            stmt.setInt(2, mesaId);
            rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return 2; // Ya votó

            // Verificar si está asignado a esta mesa
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM asignaciones_ciudadanos WHERE ciudadano_id = ? AND mesa_id = ?");
            stmt.setInt(1, ciudadanoId);
            stmt.setInt(2, mesaId);
            rs = stmt.executeQuery();
            if (!rs.next() || rs.getInt(1) == 0) return 1; // No es su mesa

            // Registrar el voto
            stmt = conn.prepareStatement("INSERT INTO votos (ciudadano_id, mesa_id, candidato_id) VALUES (?, ?, (SELECT id FROM candidatos WHERE nombres = ?))");
            stmt.setInt(1, ciudadanoId);
            stmt.setInt(2, mesaId);
            stmt.setString(3, candidato);
            stmt.executeUpdate();

            return 0; // Puede votar y voto registrado
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Error inesperado
        }
    }

    // Método para verificar el estado de un votante
    public int verificarEstado(String documento, int mesaId) {
        try (Connection conn = dbConnection.getConnection()) {
            // Verificar si el ciudadano existe
            PreparedStatement stmt = conn.prepareStatement("SELECT id FROM ciudadanos WHERE documento = ?");
            stmt.setString(1, documento);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) return 3; // No aparece en la BD
            int ciudadanoId = rs.getInt("id");

            // Verificar si ya votó en esta mesa
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM votos WHERE ciudadano_id = ? AND mesa_id = ?");
            stmt.setInt(1, ciudadanoId);
            stmt.setInt(2, mesaId);
            rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return 2; // Ya votó

            // Verificar si está asignado a esta mesa
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM asignaciones_ciudadanos WHERE ciudadano_id = ? AND mesa_id = ?");
            stmt.setInt(1, ciudadanoId);
            stmt.setInt(2, mesaId);
            rs = stmt.executeQuery();
            if (!rs.next() || rs.getInt(1) == 0) return 1; // No es su mesa

            return 0; // Puede votar
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Error inesperado
        }
    }

    // Método para recibir y registrar un lote de votos
    public boolean addLoteVotos(String votos) {
        try (Connection conn = dbConnection.getConnection()) {
            String[] votosArr = votos.split(";");
            for (String voto : votosArr) {
                String[] partes = voto.split(",");
                if (partes.length != 3) continue;
                String documento = partes[0];
                int mesaId = Integer.parseInt(partes[1]);
                String candidato = partes[2];
                // Reutiliza la lógica de registrarVoto
                registrarVoto(documento, mesaId, candidato);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}