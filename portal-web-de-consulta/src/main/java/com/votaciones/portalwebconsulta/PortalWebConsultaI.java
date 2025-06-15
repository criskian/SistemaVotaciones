package com.votaciones.portalwebconsulta;

import VotingSystem.QueryStation;
import com.votaciones.common.db.DatabaseConnection;
import com.zeroc.Ice.Current;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortalWebConsultaI implements QueryStation {
    private static final Logger logger = LoggerFactory.getLogger(PortalWebConsultaI.class);
    private final DataSource dataSource;

    public PortalWebConsultaI() {
        this.dataSource = DatabaseConnection.getDataSource();
    }

    @Override
    public String consultVotingStation(String citizenId, Current current) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT c.nombres, c.apellidos, " +
                                "m.numero as mesa_numero, " +
                                "col.nombre as colegio_nombre, col.direccion as colegio_direccion, " +
                                "ciu.nombre as ciudad_nombre " +
                                "FROM ciudadanos c " +
                                "JOIN mesas_votacion m ON c.mesa_id = m.id " +
                                "JOIN colegios col ON m.colegio_id = col.id " +
                                "JOIN ciudades ciu ON col.ciudad_id = ciu.id " +
                                "WHERE c.documento = ?")) {

            stmt.setString(1, citizenId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return String.format(
                            "Información de votación para %s %s:\n" +
                                    "Mesa: %d\n" +
                                    "Colegio: %s\n" +
                                    "Dirección: %s\n" +
                                    "Ciudad: %s",
                            rs.getString("nombres"),
                            rs.getString("apellidos"),
                            rs.getInt("mesa_numero"),
                            rs.getString("colegio_nombre"),
                            rs.getString("colegio_direccion"),
                            rs.getString("ciudad_nombre"));
                }
                return "No se encontró información de votación para la cédula " + citizenId;
            }
        } catch (SQLException e) {
            logger.error("Error al consultar la información de votación", e);
            return "Error al consultar la información de votación: " + e.getMessage();
        }
    }

    @Override
    public String consultZone(String citizenId, Current current) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT zone_name FROM zones WHERE citizen_id = ?")) {
            stmt.setString(1, citizenId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("zone_name");
                }
                return "No se encontró información de la zona para el ciudadano " + citizenId;
            }
        } catch (SQLException e) {
            logger.error("Error al consultar la zona", e);
            return "Error al consultar la zona: " + e.getMessage();
        }
    }

    @Override
    public String consultCandidates(Current current) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT name FROM candidates ORDER BY name");
                ResultSet rs = stmt.executeQuery()) {
            StringBuilder result = new StringBuilder();
            while (rs.next()) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(rs.getString("name"));
            }
            return result.length() > 0 ? result.toString() : "No hay candidatos registrados";
        } catch (SQLException e) {
            logger.error("Error al consultar los candidatos", e);
            return "Error al consultar los candidatos: " + e.getMessage();
        }
    }

    @Override
    public String consultVoteCount(Current current) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT c.name, COUNT(v.id) as votes FROM candidates c LEFT JOIN votes v ON c.id = v.candidate_id GROUP BY c.id, c.name ORDER BY votes DESC");
                ResultSet rs = stmt.executeQuery()) {
            StringBuilder result = new StringBuilder();
            while (rs.next()) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(rs.getString("name"))
                        .append(": ")
                        .append(rs.getInt("votes"))
                        .append(" votos");
            }
            return result.length() > 0 ? result.toString() : "No hay votos registrados";
        } catch (SQLException e) {
            logger.error("Error al consultar el conteo de votos", e);
            return "Error al consultar el conteo de votos: " + e.getMessage();
        }
    }
}