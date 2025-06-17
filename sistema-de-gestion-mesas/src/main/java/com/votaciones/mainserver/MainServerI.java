package com.votaciones.mainserver;

import VotingSystem.*;
import com.zeroc.Ice.Current;
import java.sql.*;
import java.util.*;
import com.votaciones.messaging.ReliableMessageManager;
import com.votaciones.messaging.model.VotingMessage;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import VotingSystem.ProxyCacheDBCiudadPrx;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainServerI implements MainServer {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/sistema_votaciones";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";
    private ReliableMessageManager reliableMessageManager;
    private ProxyCacheDBCiudadPrx proxyCachePrx;
    private ObjectMapper objectMapper = new ObjectMapper();

    public MainServerI() {
        try {
            Communicator communicator = com.zeroc.Ice.Util.initialize();
            String proxyStr = "ProxyCacheDBCiudad:default -h 127.0.0.1 -p 10030";
            ObjectPrx base = communicator.stringToProxy(proxyStr);
            proxyCachePrx = ProxyCacheDBCiudadPrx.checkedCast(base);
            if (proxyCachePrx == null) {
                throw new RuntimeException("No se pudo obtener el proxy de ProxyCacheDBCiudad");
            }
            reliableMessageManager = new ReliableMessageManager(base, votingMsg -> {
                try {
                    Voto[] votos = objectMapper.readValue(votingMsg.getContent(), Voto[].class);
                    return proxyCachePrx.AgregarLoteVotos(votos);
                } catch (Exception e) {
                    System.err.println("[ReliableMessage] Error al enviar lote: " + e.getMessage());
                    return false;
                }
            });
            reliableMessageManager.start();
        } catch (Exception e) {
            System.err.println("[MainServerI] Error inicializando ReliableMessageManager: " + e.getMessage());
        }
    }

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
            // Obtener el id del ciudadano
            String sql = "SELECT id FROM ciudadanos WHERE documento = ?";
            int ciudadanoId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, idVotante);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    return false; // No existe
                }
                ciudadanoId = rs.getInt("id");
            }
            // Obtener la mesa y zona desde el contexto (args de la estación)
            // Suponiendo que la mesa y zona se pasan en el contexto ICE
            int mesaId = -1;
            String zona = null;
            if (current != null && current.ctx != null) {
                if (current.ctx.containsKey("mesaId")) {
                    mesaId = Integer.parseInt(current.ctx.get("mesaId"));
                }
                if (current.ctx.containsKey("zona")) {
                    zona = current.ctx.get("zona");
                }
            }
            if (mesaId == -1 || zona == null) {
                System.err.println("[MainServerI] Faltan parámetros de mesa o zona en el contexto");
                return false;
            }
            // Registrar el voto
            sql = "INSERT INTO votos (ciudadano_id, candidato_id, mesa_id, fecha_hora) VALUES (?, ?, ?, NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, ciudadanoId);
                stmt.setInt(2, idCandidato);
                stmt.setInt(3, mesaId);
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
        try {
            String jsonLote = objectMapper.writeValueAsString(lote.votos);
            VotingMessage msg = new VotingMessage(jsonLote, "LOTE_VOTOS", "MainServer");
            reliableMessageManager.sendMessage(msg);
            return true;
        } catch (Exception e) {
            System.err.println("[MainServerI] Error enviando lote con ReliableMessage: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Candidato[] listarCandidatos(Current current) {
        System.out.println("[MainServerI] Iniciando listarCandidatos()");
        List<Candidato> candidatos = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("[MainServerI] Conexión a BD exitosa");
            String sql = "SELECT id, nombres, partido_politico FROM candidatos";
            System.out.println("[MainServerI] Ejecutando consulta: " + sql);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                int count = 0;
                while (rs.next()) {
                    count++;
                    Candidato candidato = new Candidato();
                    candidato.id = rs.getInt("id");
                    candidato.nombre = rs.getString("nombres");
                    candidato.partido = rs.getString("partido_politico");
                    candidatos.add(candidato);
                    System.out.println("[MainServerI] Candidato " + count + ": ID=" + candidato.id + 
                                     ", Nombre=" + candidato.nombre + ", Partido=" + candidato.partido);
                }
                System.out.println("[MainServerI] Total candidatos encontrados: " + count);
            }
        } catch (SQLException e) {
            System.err.println("[MainServerI] Error al listar candidatos: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("[MainServerI] Devolviendo array de " + candidatos.size() + " candidatos");
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

    @Override
    public boolean verificarEstadoZona(String idVotante, String zona, Current current) {
        System.err.println("[DEBUG] verificarEstadoZona: idVotante=" + idVotante + ", zona=" + zona);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            int zonaId = Integer.parseInt(zona);
            System.err.println("[DEBUG] ZonaId parseado: " + zonaId);
            String sql = "SELECT c.id FROM ciudadanos c JOIN asignaciones_ciudadanos a ON c.id = a.ciudadano_id WHERE c.documento = ? AND a.zona_id = ? AND a.estado = 'ACTIVA'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, idVotante);
                stmt.setInt(2, zonaId);
                ResultSet rs = stmt.executeQuery();
                boolean found = rs.next();
                System.err.println("[DEBUG] Resultado consulta asignación: " + found);
                if (!found) {
                    System.err.println("[MainServerI] Ciudadano no asignado activamente a la zona: " + idVotante + ", zonaId: " + zonaId);
                    return false;
                }
                int ciudadanoId = rs.getInt("id");
                System.err.println("[MainServerI] Ciudadano asignado activamente a la zona: " + idVotante + ", ciudadanoId: " + ciudadanoId);
                sql = "SELECT COUNT(*) FROM votos v JOIN mesas_votacion m ON v.mesa_id = m.id JOIN colegios col ON m.colegio_id = col.id WHERE v.ciudadano_id = ? AND col.zona_id = ? AND v.estado = 'VALIDO'";
                try (PreparedStatement stmt2 = conn.prepareStatement(sql)) {
                    stmt2.setInt(1, ciudadanoId);
                    stmt2.setInt(2, zonaId);
                    ResultSet rs2 = stmt2.executeQuery();
                    if (rs2.next() && rs2.getInt(1) > 0) {
                        System.err.println("[MainServerI] Ciudadano ya votó en la zona: " + idVotante + ", zonaId: " + zonaId);
                        return false;
                    }
                }
                System.err.println("[MainServerI] Ciudadano puede votar en la zona: " + idVotante + ", zonaId: " + zonaId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[MainServerI] Error en verificarEstadoZona: " + e.getMessage());
        }
        return false;
    }
} 