package com.votaciones.mainserver;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class VotingSystemClientUI extends JFrame {
    private VotingSystem stub;
    private DefaultTableModel tableModel;

    public VotingSystemClientUI() {
        setTitle("Sistema de Gestión de Mesas - Cliente");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initRMI();
        initUI();
        loadMesas();
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

    private void initUI() {
        tableModel = new DefaultTableModel(new Object[]{"ID Mesa", "Estado"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        JButton refreshBtn = new JButton("Refrescar");
        refreshBtn.addActionListener(e -> loadMesas());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);
        add(panel);
    }

    private void loadMesas() {
        tableModel.setRowCount(0);
        try {
            String[] estados = stub.obtenerEstadoMesas();
            for (String estado : estados) {
                String[] parts = estado.split("\\|");
                tableModel.addRow(parts);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al obtener datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VotingSystemClientUI().setVisible(true));
    }
} 