package com.votaciones.estacion;

import java.util.*;

/**
 * Lógica de la mesa de votación: gestiona candidatos, estado y registro de votos.
 */
public class MesaVotacion {
    private int idMesa;
    private List<String> candidatos;
    private Set<String> cedulasQueYaVotaron;
    private Map<String, Integer> votosPorCandidato;

    public MesaVotacion(int idMesa, List<String> candidatos) {
        this.idMesa = idMesa;
        this.candidatos = new ArrayList<>(candidatos);
        this.cedulasQueYaVotaron = new HashSet<>();
        this.votosPorCandidato = new HashMap<>();
        for (String candidato : candidatos) {
            votosPorCandidato.put(candidato, 0);
        }
    }

    public int getIdMesa() {
        return idMesa;
    }

    public List<String> getCandidatos() {
        return Collections.unmodifiableList(candidatos);
    }

    public boolean yaVoto(String cedula) {
        return cedulasQueYaVotaron.contains(cedula);
    }

    public boolean registrarVoto(String cedula, String candidato) {
        if (yaVoto(cedula)) return false;
        if (!candidatos.contains(candidato)) return false;
        cedulasQueYaVotaron.add(cedula);
        votosPorCandidato.put(candidato, votosPorCandidato.get(candidato) + 1);
        return true;
    }

    public int getVotosCandidato(String candidato) {
        return votosPorCandidato.getOrDefault(candidato, 0);
    }

    public Map<String, Integer> getResumenVotos() {
        return Collections.unmodifiableMap(votosPorCandidato);
    }

    // Puedes agregar más métodos según el diagrama (mostrar error, cancelar voto, etc.)
} 