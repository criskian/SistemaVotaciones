package com.votaciones.portalwebconsulta;

import VotingSystem.QueryStationPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PortalWebConsultaClient {
    private static final Logger logger = LoggerFactory.getLogger(PortalWebConsultaClient.class);
    private static QueryStationPrx queryStation;
    private static Communicator communicator;

    public static void main(String[] args) {
        try {
            // Inicializar el comunicador Ice
            communicator = Util.initialize(args);

            // Obtener el proxy
            ObjectPrx base = communicator.stringToProxy("PortalWebConsulta:default -p 10000");
            queryStation = QueryStationPrx.checkedCast(base);
            if (queryStation == null) {
                throw new Error("Invalid proxy");
            }

            // Crear y mostrar la interfaz gráfica
            SwingUtilities.invokeLater(() -> createAndShowGUI());

        } catch (Exception e) {
            logger.error("Error al iniciar el cliente", e);
            JOptionPane.showMessageDialog(null,
                    "Error al iniciar el cliente: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private static void createAndShowGUI() {
        // Crear la ventana principal
        JFrame frame = new JFrame("Portal Web de Consulta");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        // Panel principal con GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campo de documento
        JTextField documentField = new JTextField(20);
        JButton consultButton = new JButton("Consultar");
        JTextArea resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Botones adicionales
        JButton listCandidatesButton = new JButton("Listar Candidatos");
        JButton showVoteCountButton = new JButton("Mostrar Conteo de Votos");

        // Agregar componentes al panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Documento:"), gbc);

        gbc.gridx = 1;
        mainPanel.add(documentField, gbc);

        gbc.gridx = 2;
        mainPanel.add(consultButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        mainPanel.add(scrollPane, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(listCandidatesButton, gbc);

        gbc.gridx = 1;
        mainPanel.add(showVoteCountButton, gbc);

        frame.add(mainPanel);

        // Agregar listeners
        consultButton.addActionListener(e -> {
            String document = documentField.getText().trim();
            if (document.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Por favor ingrese un número de documento",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String stationInfo = queryStation.consultVotingStation(document);
                String zoneInfo = queryStation.consultZone(document);
                resultArea.setText("Mesa de Votación:\n" + stationInfo + "\n\nZona:\n" + zoneInfo);
            } catch (Exception ex) {
                logger.error("Error al consultar información", ex);
                JOptionPane.showMessageDialog(frame,
                        "Error al consultar: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        listCandidatesButton.addActionListener(e -> {
            try {
                String candidates = queryStation.consultCandidates();
                resultArea.setText("Lista de Candidatos:\n" + candidates);
            } catch (Exception ex) {
                logger.error("Error al listar candidatos", ex);
                JOptionPane.showMessageDialog(frame,
                        "Error al listar candidatos: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        showVoteCountButton.addActionListener(e -> {
            try {
                String voteCount = queryStation.consultVoteCount();
                resultArea.setText("Conteo de Votos:\n" + voteCount);
            } catch (Exception ex) {
                logger.error("Error al consultar votos", ex);
                JOptionPane.showMessageDialog(frame,
                        "Error al consultar votos: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Agregar shutdown hook
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (communicator != null) {
                    try {
                        communicator.destroy();
                    } catch (Exception ex) {
                        logger.error("Error al cerrar el comunicador", ex);
                    }
                }
            }
        });

        frame.setVisible(true);
    }
}