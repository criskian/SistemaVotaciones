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
        String sql = "SELECT c.documento, c.nombre, ac.zona_id, c.id as ciudadano_id FROM ciudadanos c " +
                    "LEFT JOIN asignaciones_ciudadanos ac ON c.id = ac.ciudadano_id " +
                    "WHERE c.documento = ? AND ac.activo = true";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Votante(
                        rs.getString("documento"),
                        rs.getString("nombre"),
                        "", // apellidos no existe en la tabla
                        rs.getInt("ciudadano_id"), // ciudad_id
                        rs.getInt("zona_id"),
                        rs.getInt("ciudadano_id") // mesa_id - usando ciudadano_id como mesa_id
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
        String sql = "SELECT z.id, z.nombre, z.codigo FROM ciudadanos c " +
                    "JOIN asignaciones_ciudadanos ac ON c.id = ac.ciudadano_id " +
                    "JOIN zonas_electorales z ON ac.zona_id = z.id " +
                    "WHERE c.documento = ? AND ac.activo = true";
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
        String sql = "SELECT ac.zona_id FROM ciudadanos c " +
                    "JOIN asignaciones_ciudadanos ac ON c.id = ac.ciudadano_id " +
                    "WHERE c.documento = ? AND ac.activo = true";
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

    public int getConteoVotosPorCandidato(int candidatoId) {
        String sql = "SELECT COUNT(*) as total FROM votos WHERE candidato_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, candidatoId);
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
        String sql = "INSERT INTO votos (documento_votante, candidato_id, mesa_id, fecha_hora) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, voto.documentoVotante);
            ps.setInt(2, voto.candidatoId);
            ps.setInt(3, voto.mesaId);
            ps.setString(4, voto.fechaHora);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean agregarSospechoso(String cedula, String motivo) {
        String sql = "INSERT INTO sospechosos (documento, motivo, fecha_registro) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            ps.setString(2, motivo);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registrarLogs(LogEntry log) {
        String sql = "INSERT INTO logs (tipo, mensaje, fecha_hora) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, log.tipo);
            ps.setString(2, log.mensaje);
            ps.setString(3, log.fechaHora);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String consultarMesaDescriptiva(String cedula) {
        String sql = "SELECT c.nombre, c.documento, " +
                "z.nombre AS nombre_zona, z.codigo AS codigo_zona, " +
                "ci.nombre AS ciudad " +
                "FROM ciudadanos c " +
                "JOIN asignaciones_ciudadanos ac ON c.id = ac.ciudadano_id " +
                "JOIN zonas_electorales z ON ac.zona_id = z.id " +
                "JOIN ciudades ci ON c.ciudad_id = ci.id " +
                "WHERE c.documento = ? AND ac.activo = true";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return String.format(
                        "Votante: %s\nCédula: %s\nZona: %s (Código: %s)\nCiudad: %s",
                        rs.getString("nombre"),
                        rs.getString("documento"),
                        rs.getString("nombre_zona"),
                        rs.getString("codigo_zona"),
                        rs.getString("ciudad")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al consultar la información de la mesa: " + e.getMessage();
        }
        return "No se encontró información para la cédula: " + cedula;
    }

    public boolean yaVoto(String cedula) {
        String sql = "SELECT COUNT(*) as total FROM votos WHERE documento_votante = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean esSospechoso(String cedula) {
        String sql = "SELECT COUNT(*) as total FROM sospechosos WHERE documento = ? AND (estado = 'ACTIVO' OR estado IS NULL)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
} 