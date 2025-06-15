package com.votaciones.mainserver;

import VotingSystem.*;
import com.zeroc.Ice.Current;
import java.sql.*;
import java.util.*;

public class MainServerI implements MainServer {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/sistema_votaciones";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";

    @Override
    public MesaInfo[] listarMesas(Current current) {
        List<MesaInfo> mesas = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, nombre_colegio, direccion, numero_mesa, estado FROM mesas";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    MesaInfo mesa = new MesaInfo();
                    mesa.id = rs.getInt("id");
                    mesa.nombreColegio = rs.getString("nombre_colegio");
                    mesa.direccion = rs.getString("direccion");
                    mesa.numeroMesa = rs.getInt("numero_mesa");
                    mesa.estado = rs.getString("estado");
                    mesas.add(mesa);
                }
            }
        } catch (SQLException e) {
            System.err.println("[MainServerI] Error al listar mesas: " + e.getMessage());
        }
        return mesas.toArray(new MesaInfo[0]);
    }

    @Override
    public boolean validarVoto(String idVotante, Current current) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Primero verificar si el ciudadano existe
            String sql = "SELECT id FROM ciudadanos WHERE documento = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, idVotante);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    return false; // Ciudadano no existe
                }
                int ciudadanoId = rs.getInt("id");
                
                // Luego verificar si ya votó
                sql = "SELECT COUNT(*) FROM votos WHERE ciudadano_id = ?";
                stmt.setInt(1, ciudadanoId);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) == 0; // Puede votar si no ha votado antes
                }
            }
        } catch (SQLException e) {
            System.err.println("[MainServerI] Error al validar voto: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean registrarVoto(String idVotante, int idCandidato, Current current) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO votos (ciudadano_id, candidato_id, fecha_hora) VALUES (?, ?, NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, idVotante);
                stmt.setInt(2, idCandidato);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[MainServerI] Error al registrar voto: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean verificarEstado(String idVotante, Current current) {
        return validarVoto(idVotante, current);
    }

    @Override
    public boolean addLoteVotos(LoteVotos lote, Current current) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);
            try {
                String sql = "INSERT INTO votos (ciudadano_id, candidato_id, fecha_hora) VALUES (?, ?, NOW())";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    for (Voto voto : lote.votos) {
                        stmt.setString(1, voto.idVotante);
                        stmt.setInt(2, voto.idCandidato);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("[MainServerI] Error al agregar lote de votos: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("[MainServerI] Error al conectar con la base de datos: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Candidato[] listarCandidatos(Current current) {
        List<Candidato> candidatos = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, nombre, partido FROM candidatos";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Candidato candidato = new Candidato();
                    candidato.id = rs.getInt("id");
                    candidato.nombre = rs.getString("nombre");
                    candidato.partido = rs.getString("partido");
                    candidatos.add(candidato);
                }
            }
        } catch (SQLException e) {
            System.err.println("[MainServerI] Error al listar candidatos: " + e.getMessage());
        }
        return candidatos.toArray(new Candidato[0]);
    }

    @Override
    public void registrarAlerta(AlertaInfo alerta, Current current) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO alertas (tipo, mensaje, fecha) VALUES (?, ?, NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, alerta.tipo);
                stmt.setString(2, alerta.mensaje);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("[MainServerI] Error al registrar alerta: " + e.getMessage());
        }
    }

    @Override
    public AlertaInfo[] listarAlertas(Current current) {
        List<AlertaInfo> alertas = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, tipo, mensaje, fecha FROM alertas ORDER BY fecha DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    AlertaInfo alerta = new AlertaInfo();
                    alerta.id = rs.getInt("id");
                    alerta.tipo = rs.getString("tipo");
                    alerta.mensaje = rs.getString("mensaje");
                    alerta.fecha = rs.getTimestamp("fecha").toString();
                    alertas.add(alerta);
                }
            }
        } catch (SQLException e) {
            System.err.println("[MainServerI] Error al listar alertas: " + e.getMessage());
        }
        return alertas.toArray(new AlertaInfo[0]);
    }

    @Override
    public Estadisticas obtenerEstadisticas(Current current) {
        Estadisticas stats = new Estadisticas();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Total de votos
            String sqlVotos = "SELECT COUNT(*) FROM votos";
            try (PreparedStatement stmt = conn.prepareStatement(sqlVotos)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.totalVotos = rs.getInt(1);
                }
            }

            // Total de mesas
            String sqlMesas = "SELECT COUNT(*) FROM mesas";
            try (PreparedStatement stmt = conn.prepareStatement(sqlMesas)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.totalMesas = rs.getInt(1);
                }
            }

            // Mesas activas
            String sqlMesasActivas = "SELECT COUNT(*) FROM mesas WHERE estado = 'ACTIVA'";
            try (PreparedStatement stmt = conn.prepareStatement(sqlMesasActivas)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.mesasActivas = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[MainServerI] Error al obtener estadísticas: " + e.getMessage());
        }
        return stats;
    }
} 