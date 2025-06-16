package VotingSystem;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataCacheProxy {
    private final HikariDataSource dataSource;

    public DataCacheProxy(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Candidato> consultarCandidatos() {
        List<Candidato> candidatos = new ArrayList<>();
        String sql = "SELECT id, documento, nombres, apellidos, partido_politico FROM candidatos";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Candidato c = new Candidato(
                    rs.getInt("id"),
                    rs.getString("documento"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("partido_politico")
                );
                candidatos.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidatos;
    }

    public Votante consultarVotantePorCedula(String cedula) {
        String sql = "SELECT documento, nombres, apellidos, ciudad_id, zona_id, mesa_id FROM ciudadanos WHERE documento = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Votante(
                        rs.getString("documento"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getInt("ciudad_id"),
                        rs.getInt("zona_id"),
                        rs.getInt("mesa_id")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Zona> getZonasVotacion() {
        List<Zona> zonas = new ArrayList<>();
        String sql = "SELECT id, nombre, codigo FROM zonas_electorales";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Zona z = new Zona(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("codigo")
                );
                zonas.add(z);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return zonas;
    }

    public Zona zonaMesaAsignada(String cedula) {
        String sql = "SELECT z.id, z.nombre, z.codigo FROM ciudadanos c JOIN zonas_electorales z ON c.zona_id = z.id WHERE c.documento = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Zona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("codigo")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int idZonaVotacion(String cedula) {
        String sql = "SELECT zona_id FROM ciudadanos WHERE documento = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("zona_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getConteoVotos(int mesaId) {
        String sql = "SELECT COUNT(*) as total FROM votos WHERE mesa_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mesaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean agregarVoto(Voto voto) {
        String sql = "INSERT INTO votos (candidato_id, mesa_id, fecha_hora, estado) VALUES (?, ?, ?, 'ACTIVO')";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, voto.candidatoId);
            ps.setInt(2, voto.mesaId);
            ps.setString(3, voto.fechaHora);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean agregarSospechoso(String cedula, String motivo) {
        String sql = "INSERT INTO sospechosos (cedula, motivo, fecha_hora) VALUES (?, ?, now())";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            ps.setString(2, motivo);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registrarLogs(LogEntry log) {
        String sql = "INSERT INTO auditorialogs (tipo, mensaje, fecha_hora) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, log.tipo);
            ps.setString(2, log.mensaje);
            ps.setString(3, log.fechaHora);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
} 