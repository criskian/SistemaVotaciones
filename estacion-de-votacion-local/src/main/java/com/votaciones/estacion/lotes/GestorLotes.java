package com.votaciones.estacion.lotes;

import com.votaciones.estacion.GestionMesasProxy;
import VotingSystem.Voto;
import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Gestor de lotes: agrupa votos validados en lotes de 20 o cada 15 minutos.
 * Almacena los lotes en la base de datos local y los envía cuando corresponde.
 */
public class GestorLotes {
    private static final String DB_PATH = "db/ColaVotosLocal.db";
    private static final int TAMANO_LOTE = 20;
    private static final long TIEMPO_MAXIMO = 15 * 60 * 1000; // 15 minutos en milisegundos
    private List<Voto> votosPendientes;
    private final Connection conn;
    private final Timer timer;
    private long ultimoEnvio;

    public GestorLotes() {
        this.votosPendientes = new ArrayList<>();
        this.conn = crearConexion();
        this.timer = new Timer(true);
        this.ultimoEnvio = System.currentTimeMillis();
        crearTablaSiNoExiste();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                enviarLoteSiCorresponde(true, null);
            }
        }, TIEMPO_MAXIMO, TIEMPO_MAXIMO);
    }

    private Connection crearConexion() {
        try {
            File dbDir = new File("db");
            if (!dbDir.exists()) dbDir.mkdirs();
            return DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo conectar a la base de datos local", e);
        }
    }

    private void crearTablaSiNoExiste() {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS lotes (id INTEGER PRIMARY KEY AUTOINCREMENT, votos TEXT, enviado INTEGER)");
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo crear la tabla de lotes", e);
        }
    }

    public synchronized void agregarVoto(Voto voto) {
        votosPendientes.add(voto);
        enviarLoteSiCorresponde(false, null);
    }

    public synchronized void enviarLoteSiCorresponde(boolean forzadoPorTiempo, GestionMesasProxy proxy) {
        if (votosPendientes.isEmpty()) return;
        if (!forzadoPorTiempo && votosPendientes.size() < TAMANO_LOTE) return;
        try {
            String fecha = LocalDateTime.now().toString();
            // Serializar votos a texto para almacenamiento local
            StringBuilder sb = new StringBuilder();
            for (Voto v : votosPendientes) {
                sb.append(v.idVotante).append(",").append(v.idCandidato).append(";");
            }
            String votosStr = sb.toString();
            boolean exito = false;
            if (proxy != null) {
                exito = proxy.enviarLoteVotos(new ArrayList<>(votosPendientes));
            }
            if (exito) {
                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO lotes (votos, enviado) VALUES (?, 1);")) {
                    stmt.setString(1, votosStr);
                    stmt.executeUpdate();
                }
                System.out.println("[GestorLotes] Lote enviado al sistema central: " + votosStr);
                votosPendientes.clear();
            } else {
                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO lotes (votos, enviado) VALUES (?, 0);")) {
                    stmt.setString(1, votosStr);
                    stmt.executeUpdate();
                }
                System.out.println("[GestorLotes] Lote guardado localmente (no enviado): " + votosStr);
                votosPendientes.clear();
            }
        } catch (SQLException e) {
            System.err.println("[GestorLotes] Error al guardar/enviar lote: " + e.getMessage());
        }
    }

    public void enviarLotesPendientes(GestionMesasProxy proxy) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id, votos FROM lotes WHERE enviado IS NULL OR enviado = 0")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String votosStr = rs.getString("votos");
                // Deserializar votosStr a List<Voto>
                List<Voto> votos = new ArrayList<>();
                for (String votoStr : votosStr.split(";")) {
                    if (votoStr.trim().isEmpty()) continue;
                    String[] partes = votoStr.split(",");
                    if (partes.length == 2) {
                        Voto v = new Voto();
                        v.idVotante = partes[0];
                        v.idCandidato = Integer.parseInt(partes[1]);
                        votos.add(v);
                    }
                }
                boolean exito = proxy.enviarLoteVotos(votos);
                if (exito) {
                    try (PreparedStatement update = conn.prepareStatement("UPDATE lotes SET enviado = 1 WHERE id = ?")) {
                        update.setInt(1, id);
                        update.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[GestorLotes] Error al enviar lotes pendientes: " + e.getMessage());
        }
    }
} 