package com.sistemaelectoral.consulta;

import com.zeroc.Ice.*;
import com.sistemaelectoral.interfaces.*;

public class ConsultaClient {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            ObjectPrx base = communicator.stringToProxy("consultaciudadano:tcp -h localhost -p 10002");
            ConsultaCiudadanoPrx consulta = ConsultaCiudadanoPrx.checkedCast(base);
            
            if (consulta == null) {
                throw new Error("Invalid proxy");
            }

            java.util.Scanner scanner = new java.util.Scanner(System.in);
            while (true) {
                System.out.println("\nSistema de Consulta Electoral");
                System.out.println("1. Consultar lugar de votación");
                System.out.println("2. Consultar mesa de votación");
                System.out.println("3. Consultar zona de votación");
                System.out.println("4. Salir");
                System.out.print("Seleccione una opción: ");
                
                String opcion = scanner.nextLine();
                
                if (opcion.equals("4")) {
                    break;
                }
                
                System.out.print("Ingrese su número de cédula (10 dígitos): ");
                String cedula = scanner.nextLine();
                
                try {
                    String resultado = "";
                    switch (opcion) {
                        case "1":
                            resultado = consulta.consultarLugarVotacion(cedula);
                            break;
                        case "2":
                            resultado = consulta.consultarMesaVotacion(cedula);
                            break;
                        case "3":
                            resultado = consulta.consultarZonaVotacion(cedula);
                            break;
                        default:
                            System.out.println("Opción inválida");
                            continue;
                    }
                    System.out.println("\nResultado de la consulta:");
                    System.out.println(resultado);
                    
                } catch (com.zeroc.Ice.Exception e) {
                    System.err.println("Error en la comunicación: " + e.getMessage());
                } catch (RuntimeException e) {
                    System.err.println("Error en la aplicación: " + e.getMessage());
                }
            }
            
        } catch (com.zeroc.Ice.Exception e) {
            System.err.println("Error en Ice: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (RuntimeException e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
} 