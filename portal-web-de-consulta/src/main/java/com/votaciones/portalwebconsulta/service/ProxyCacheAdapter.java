package com.votaciones.portalwebconsulta.service;

import VotingSystem.ProxyCacheDBCiudad;
import VotingSystem.QueryStation;
import VotingSystem.Votante;
import VotingSystem.Candidato;
import VotingSystem.Zona;
import com.zeroc.Ice.Current;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyCacheAdapter implements QueryStation {
    private static final Logger logger = LoggerFactory.getLogger(ProxyCacheAdapter.class);
    private final ProxyCacheDBCiudad proxy;

    public ProxyCacheAdapter(ProxyCacheDBCiudad proxy) {
        this.proxy = proxy;
    }

    @Override
    public String consultVotingStation(String citizenId, Current current) {
        try {
            return proxy.ConsultarMesaDescriptiva(citizenId, null);
        } catch (Exception e) {
            logger.error("Error al consultar información de votación", e);
            return "Error al consultar información de votación: " + e.getMessage();
        }
    }

    @Override
    public String consultZone(String citizenId, Current current) {
        try {
            Zona zona = proxy.ZonaMesaAsignada(citizenId, null);
            if (zona == null) {
                return "No se encontró zona para la cédula: " + citizenId;
            }
            
            // Obtener información del colegio asociado a la zona
            String mesaInfo = proxy.ConsultarMesaDescriptiva(citizenId, null);
            String[] partes = mesaInfo.split(" - ");
            String colegio = partes.length > 1 ? partes[1] : "No disponible";
            
            return String.format("Zona Electoral: %s\nCódigo de Zona: %s\nColegio Asignado: %s", 
                               zona.nombre, zona.codigo, colegio);
        } catch (Exception e) {
            logger.error("Error al consultar zona", e);
            return "Error al consultar zona: " + e.getMessage();
        }
    }

    @Override
    public String consultCandidates(Current current) {
        try {
            Candidato[] candidatos = proxy.ConsultarCandidatos(null);
            if (candidatos == null || candidatos.length == 0) {
                return "No hay candidatos registrados";
            }
            StringBuilder sb = new StringBuilder("Lista de candidatos:\n");
            for (Candidato c : candidatos) {
                sb.append(String.format("- %s %s (Partido: %s)\n", c.nombres, c.apellidos, c.partido));
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error("Error al consultar candidatos", e);
            return "Error al consultar candidatos: " + e.getMessage();
        }
    }

    @Override
    public String consultVoteCount(Current current) {
        try {
            Candidato[] candidatos = proxy.ConsultarCandidatos(null);
            if (candidatos == null || candidatos.length == 0) {
                return "No hay candidatos registrados";
            }
            StringBuilder sb = new StringBuilder("Conteo de votos por candidato:\n");
            int votosEnBlanco = 0;
            for (Candidato c : candidatos) {
                int conteo = proxy.GetConteoVotosPorCandidato(c.id, null);
                String nombreCompleto = (c.nombres + " " + c.apellidos).toLowerCase();
                if (nombreCompleto.contains("blanco")) {
                    votosEnBlanco += conteo;
                } else {
                    sb.append(String.format("%s %s: %d votos\n", c.nombres, c.apellidos, conteo));
                }
            }
            sb.append(String.format("Votos en blanco: %d\n", votosEnBlanco));
            return sb.toString();
        } catch (Exception e) {
            logger.error("Error al consultar conteo de votos", e);
            return "Error al consultar conteo de votos: " + e.getMessage();
        }
    }
} 