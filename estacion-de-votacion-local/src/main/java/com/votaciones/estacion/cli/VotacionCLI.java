package com.votaciones.estacion.cli;

import com.votaciones.estacion.GestionMesasProxy;
import com.votaciones.estacion.lotes.GestorLotes;
import VotingSystem.Candidato;
import VotingSystem.Voto;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Interfaz de línea de comandos para capturar la intención de voto del ciudadano.
 * Permite ingresar cédula, mostrar candidatos y registrar el voto.
 */
public class VotacionCLI {
    private final GestionMesasProxy proxy;
    private final GestorLotes gestorLotes;
    private final Scanner scanner;
    private final int mesaId;

    public VotacionCLI(GestionMesasProxy proxy, GestorLotes gestorLotes, int mesaId) {
        this.proxy = proxy;
        this.gestorLotes = gestorLotes;
        this.mesaId = mesaId;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        System.out.println("=== Estación de Votación Local ===");
        while (true) {
            System.out.print("Ingrese cédula del votante (o 'salir'): ");
            String cedula = scanner.nextLine();
            if (cedula.equalsIgnoreCase("salir")) break;

            // Validar si puede votar
            boolean puedeVotar = proxy.verificarEstado(cedula);
            if (!puedeVotar) {
                System.out.println("[!] El votante no puede votar (ya votó o no está habilitado).");
                continue;
            }

            // Listar candidatos
            List<Candidato> candidatos = proxy.listarCandidatos();
            System.out.println("Candidatos disponibles:");
            for (int i = 0; i < candidatos.size(); i++) {
                System.out.printf("%d. %s (%s)\n", i + 1, candidatos.get(i).nombre, candidatos.get(i).partido);
            }
            System.out.print("Seleccione el número del candidato: ");
            int seleccion = Integer.parseInt(scanner.nextLine());
            if (seleccion < 1 || seleccion > candidatos.size()) {
                System.out.println("Selección inválida.");
                continue;
            }
            Candidato candidato = candidatos.get(seleccion - 1);

            // Agregar voto al lote local
            Voto voto = new Voto();
            voto.idVotante = cedula;
            voto.idCandidato = candidato.id;
            gestorLotes.agregarVoto(voto);
            System.out.println("Voto registrado localmente para " + candidato.nombre);
        }
    }
} 