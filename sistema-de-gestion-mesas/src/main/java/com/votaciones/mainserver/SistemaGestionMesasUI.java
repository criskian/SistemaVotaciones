package com.votaciones.mainserver;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SistemaGestionMesasUI extends JFrame {
    private VotingSystem stub;
    private DefaultTableModel zonasModel, mesasModel, alertasModel, estadisticasModel;
    private JTextArea reportesArea;
    private int zonaSeleccionadaId = -1;
    private String zonaSeleccionadaNombre = "";

    public SistemaGestionMesasUI() {
        setTitle("Sistema de Gestión de Mesas");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initRMI();
        seleccionarZona();
        initUI();
    }

    private void initRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            stub = (VotingSystem) registry.lookup("VotingSystem");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor RMI", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void seleccionarZona() {
        try {
            String[] zonas = stub.obtenerZonasElectorales();
            String[] nombres = new String[zonas.length];
            int[] ids = new int[zonas.length];
            for (int i = 0; i < zonas.length; i++) {
                String[] parts = zonas[i].split("\\|");
                ids[i] = Integer.parseInt(parts[0]);
                nombres[i] = parts[1] + " (" + parts[2] + ", " + parts[3] + ")";
            }
            String seleccion = (String) JOptionPane.showInputDialog(
                this,
                "Seleccione la zona electoral:",
                "Zona Electoral",
                JOptionPane.QUESTION_MESSAGE,
                null,
                nombres,
                nombres.length > 0 ? nombres[0] : null
            );
            if (seleccion == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una zona para continuar.");
                System.exit(0);
            }
            for (int i = 0; i < nombres.length; i++) {
                if (nombres[i].equals(seleccion)) {
                    zonaSeleccionadaId = ids[i];
                    zonaSeleccionadaNombre = nombres[i];
                    break;
                }
            }
        } catch (Exception e) {
            showError(e);
            System.exit(1);
        }
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Zonas", zonasPanel());
        tabs.addTab("Mesas", mesasPanel());
        tabs.addTab("Validación/Registro", validacionPanel());
        tabs.addTab("Reportes/Estadísticas", reportesPanel());
        tabs.addTab("Alertas/Incidencias", alertasPanel());
        add(tabs);
    }

    private JPanel zonasPanel() {
        zonasModel = new DefaultTableModel(new Object[]{"ID Zona", "Nombre", "Código", "Ciudad", "# Mesas", "# Ciudadanos"}, 0);
        JTable table = new JTable(zonasModel);
        JButton refresh = new JButton("Refrescar");
        refresh.addActionListener(e -> cargarZonas());
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(refresh, BorderLayout.SOUTH);
        cargarZonas();
        return p;
    }

    private void cargarZonas() {
        zonasModel.setRowCount(0);
        try {
            String[] zonas = stub.obtenerZonasElectorales();
            for (String z : zonas) {
                String[] parts = z.split("\\|");
                if (Integer.parseInt(parts[0]) == zonaSeleccionadaId) {
                    zonasModel.addRow(parts);
                }
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private JPanel mesasPanel() {
        mesasModel = new DefaultTableModel(new Object[]{"ID Mesa", "Número", "Estado", "Colegio", "Ciudad"}, 0);
        JTable table = new JTable(mesasModel);
        JButton refresh = new JButton("Refrescar");
        refresh.addActionListener(e -> cargarMesas());
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(refresh, BorderLayout.SOUTH);
        cargarMesas();
        return p;
    }

    private void cargarMesas() {
        mesasModel.setRowCount(0);
        try {
            String[] mesas = stub.obtenerMesasPorZona(zonaSeleccionadaId);
            for (String m : mesas) {
                mesasModel.addRow(m.split("\\|"));
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private boolean mesaPerteneceAZona(String mesaIdStr) {
        try {
            int mesaId = Integer.parseInt(mesaIdStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private JPanel validacionPanel() {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField cedula = new JTextField();
        JTextField mesa = new JTextField();
        JButton validar = new JButton("Validar Asignación");
        JButton registrar = new JButton("Registrar Cédula");
        JLabel resultado = new JLabel();
        validar.addActionListener(e -> {
            try {
                boolean ok = stub.validarMesaZonaAsignada(cedula.getText(), zonaSeleccionadaId, Integer.parseInt(mesa.getText()));
                resultado.setText(ok ? "Asignación válida" : "No asignado correctamente");
            } catch (Exception ex) {
                showError(ex);
            }
        });
        registrar.addActionListener(e -> {
            try {
                stub.registrarCedula(cedula.getText(), Integer.parseInt(mesa.getText()));
                resultado.setText("Cédula registrada");
            } catch (Exception ex) {
                showError(ex);
            }
        });
        p.add(new JLabel("Cédula:")); p.add(cedula);
        p.add(new JLabel("Mesa ID:")); p.add(mesa);
        p.add(validar); p.add(registrar);
        p.add(new JLabel("Resultado:")); p.add(resultado);
        return p;
    }

    private JPanel reportesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        reportesArea = new JTextArea();
        reportesArea.setEditable(false);
        JButton parciales = new JButton("Resultados Parciales");
        JButton finales = new JButton("Resultados Finales");
        JButton estadisticas = new JButton("Estadísticas por Zona");
        JTextField zonaId = new JTextField();
        parciales.addActionListener(e -> mostrarResultados(false));
        finales.addActionListener(e -> mostrarResultados(true));
        estadisticas.addActionListener(e -> mostrarEstadisticas(zonaId.getText()));
        JPanel top = new JPanel();
        top.add(parciales); top.add(finales); top.add(new JLabel("Zona ID:")); top.add(zonaId); top.add(estadisticas);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(reportesArea), BorderLayout.CENTER);
        return p;
    }

    private void mostrarResultados(boolean finales) {
        try {
            String[] res = finales ? stub.generarResultadosFinales() : stub.generarResultadosParciales();
            StringBuilder sb = new StringBuilder();
            for (String r : res) sb.append(r).append("\n");
            reportesArea.setText(sb.toString());
        } catch (Exception e) {
            showError(e);
        }
    }

    private void mostrarEstadisticas(String zonaId) {
        try {
            int id = Integer.parseInt(zonaId);
            String[] stats = stub.obtenerEstadisticasZona(id);
            reportesArea.setText("Total Mesas: " + stats[0] + "\nMesas Activas: " + stats[1] + "\nTotal Ciudadanos: " + stats[2] + "\nTotal Votos: " + stats[3]);
        } catch (Exception e) {
            showError(e);
        }
    }

    private JPanel alertasPanel() {
        alertasModel = new DefaultTableModel(new Object[]{"Tipo", "Mensaje", "Mesa ID", "Severidad", "Fecha/Hora"}, 0);
        JTable table = new JTable(alertasModel);
        JButton refresh = new JButton("Refrescar");
        refresh.addActionListener(e -> cargarAlertas());
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(refresh, BorderLayout.SOUTH);
        cargarAlertas();
        return p;
    }

    private void cargarAlertas() {
        alertasModel.setRowCount(0);
        try {
            // TODO: Implementar método RMI en el servidor para obtener alertas
            // String[] alertas = stub.obtenerAlertas();
            // for (String a : alertas) {
            //     alertasModel.addRow(a.split("\\|"));
            // }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SistemaGestionMesasUI().setVisible(true));
    }
} 