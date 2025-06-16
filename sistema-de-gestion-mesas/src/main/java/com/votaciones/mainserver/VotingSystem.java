package com.votaciones.mainserver;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VotingSystem extends Remote {
    String[] obtenerEstadoMesas() throws RemoteException;
    String obtenerEstadoMesa(int mesaId) throws RemoteException;
    void actualizarEstadoMesa(int mesaId, String estado) throws RemoteException;
    int obtenerTotalVotos() throws RemoteException;
    String[] generarResultadosParciales() throws RemoteException;
    String[] generarResultadosFinales() throws RemoteException;
    void registrarAlerta(String tipo, String mensaje, int mesaId, String severidad) throws RemoteException;
    void cerrarVotacion() throws RemoteException;
    String[] obtenerZonasElectorales() throws RemoteException;
    boolean validarMesaZonaAsignada(String documento, int zonaId, int mesaId) throws RemoteException;
    void registrarCedula(String documento, int mesaId) throws RemoteException;
    String[] obtenerEstadisticasZona(int zonaId) throws RemoteException;
    String[] obtenerMesasPorZona(int zonaId) throws RemoteException;
} 