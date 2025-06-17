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
        String sql = "SELECT id, nombre, partido, propuestas FROM candidatos";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Candidato c = new Candidato(
                    rs.getInt("id"),
                    "", // documento
                    rs.getString("nombre"), // nombres
                    "", // apellidos
                    rs.getString("partido")
                );
                candidatos.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidatos;
    }

    public Votante consultarVotantePorCedula(String cedula) {
        String sql = "SELECT c.id, c.documento, c.nombre, c.ciudad_id, ac.zona_id, mv.id as mesa_id " +
                    "FROM ciudadanos c " +
                    "LEFT JOIN asignaciones_ciudadanos ac ON c.id = ac.ciudadano_id " +
                    "LEFT JOIN mesas_votacion mv ON c.id = mv.id " +
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
        String sql = "SELECT z.id, z.nombre, z.codigo " +
                    "FROM zonas_electorales z " +
                    "JOIN asignaciones_ciudadanos ac ON z.id = ac.zona_id " +
                    "JOIN ciudadanos c ON ac.ciudadano_id = c.id " +
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
        String sql = "SELECT z.id FROM zonas_electorales z " +
                    "JOIN asignaciones_ciudadanos ac ON z.id = ac.zona_id " +
                    "JOIN ciudadanos c ON ac.ciudadano_id = c.id " +
                    "WHERE c.documento = ? AND ac.activo = true";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
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
        String sql = "INSERT INTO votos (ciudadano_id, candidato_id, mesa_id, fecha_hora) " +
                    "SELECT c.id, ?, ?, ? FROM ciudadanos c WHERE c.documento = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, voto.candidatoId);
            ps.setInt(2, voto.mesaId);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, voto.documentoVotante);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean agregarSospechoso(String cedula, String motivo) {
        String sql = "INSERT INTO sospechosos (ciudadano_id, motivo, fecha_registro) " +
                    "SELECT c.id, ?, CURRENT_TIMESTAMP FROM ciudadanos c WHERE c.documento = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setString(2, cedula);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registrarLogs(LogEntry log) {
        String sql = "INSERT INTO logs (tipo, mensaje, fecha_hora) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, log.tipo);
            ps.setString(2, log.mensaje);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String consultarMesaDescriptiva(String cedula) {
        String sql = "SELECT m.numero, c.nombre as colegio, z.nombre as zona " +
                    "FROM mesas_votacion m " +
                    "JOIN colegios c ON m.colegio_id = c.id " +
                    "JOIN zonas_electorales z ON c.zona_id = z.id " +
                    "JOIN asignaciones_ciudadanos ac ON z.id = ac.zona_id " +
                    "JOIN ciudadanos ci ON ac.ciudadano_id = ci.id " +
                    "WHERE ci.documento = ? AND ac.activo = true";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return String.format("Mesa %d - %s - Zona %s",
                        rs.getInt("numero"),
                        rs.getString("colegio"),
                        rs.getString("zona"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "No se encontró información para la cédula: " + cedula;
    }

    public boolean yaVoto(String cedula) {
        String sql = "SELECT COUNT(*) as total FROM votos v " +
                    "JOIN ciudadanos c ON v.ciudadano_id = c.id " +
                    "WHERE c.documento = ?";
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
        String sql = "SELECT COUNT(*) as total FROM sospechosos s " +
                    "JOIN ciudadanos c ON s.ciudadano_id = c.id " +
                    "WHERE c.documento = ?";
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