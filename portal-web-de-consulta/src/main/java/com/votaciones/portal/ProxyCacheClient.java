package com.votaciones.portal;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import VotingSystem.ProxyCacheDBCiudadPrx;
import VotingSystem.Votante;
import VotingSystem.Candidato;

public class ProxyCacheClient {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            // Crear proxy al servidor
            ObjectPrx base = communicator.stringToProxy("ProxyCacheDBCiudad:default -h localhost -p 10000");
            ProxyCacheDBCiudadPrx proxy = ProxyCacheDBCiudadPrx.checkedCast(base);
            
            if (proxy == null) {
                throw new Error("Proxy inválido");
            }

            System.out.println("Conectado al servidor ProxyCacheDBCiudad");

            // Probar consulta de ciudadano
            try {
                VotingSystem.Votante votante = proxy.ConsultarVotantePorCedula("1234567890");
                System.out.println("Resultado consulta ciudadano: " + votante.documento + " - " + votante.nombres + " " + votante.apellidos);
            } catch (Exception e) {
                System.err.println("Error al consultar ciudadano: " + e.getMessage());
            }

            // Probar consulta de candidatos
            try {
                VotingSystem.Candidato[] candidatos = proxy.ConsultarCandidatos();
                System.out.println("Resultado consulta candidatos: " + candidatos.length + " candidatos encontrados");
            } catch (Exception e) {
                System.err.println("Error al consultar candidatos: " + e.getMessage());
            }

            // Probar conteo de votos
            try {
                int conteo = proxy.GetConteoVotos(1);
                System.out.println("Resultado conteo votos: " + conteo);
            } catch (Exception e) {
                System.err.println("Error al contar votos: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 