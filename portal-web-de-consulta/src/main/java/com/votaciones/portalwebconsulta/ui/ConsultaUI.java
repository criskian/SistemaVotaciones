package com.votaciones.portalwebconsulta.ui;

import com.votaciones.portalwebconsulta.controller.ConsultaController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConsultaUI extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(ConsultaUI.class);
    private final ConsultaController controller;
    private final JTextField cedulaField;
    private final JTextArea resultadoArea;

    public ConsultaUI() {
        super("Sistema de Votaciones - Portal de Consulta");
        this.controller = new ConsultaController();

        // Configurar la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Panel superior para el título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(51, 105, 232));
        titlePanel.setPreferredSize(new Dimension(600, 50));
        JLabel titleLabel = new JLabel("Portal de Consulta Electoral");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Panel para entrada de cédula
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(Color.WHITE);
        JLabel cedulaLabel = new JLabel("Número de Cédula:");
        cedulaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        cedulaField = new JTextField(15);
        cedulaField.setPreferredSize(new Dimension(150, 25));
        inputPanel.add(cedulaLabel);
        inputPanel.add(cedulaField);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Crear botones con iconos y texto
        JButton consultarZonaButton = createButton("Consultar Zona", "[Z]");
        JButton listarCandidatosButton = createButton("Ver Candidatos", "[C]");
        JButton verVotosButton = createButton("Ver Votos", "[V]");
        JButton limpiarButton = createButton("Limpiar", "[X]");

        buttonPanel.add(consultarZonaButton);
        buttonPanel.add(listarCandidatosButton);
        buttonPanel.add(verVotosButton);
        buttonPanel.add(limpiarButton);

        // Panel superior combinado
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        // Area de resultados
        resultadoArea = new JTextArea(8, 40);
        resultadoArea.setEditable(false);
        resultadoArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultadoArea.setMargin(new Insets(10, 10, 10, 10));
        resultadoArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(resultadoArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Resultados"));

        // Agregar componentes al panel principal
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        // Agregar panel principal a la ventana
        add(mainPanel);

        // Configurar acciones de los botones
        consultarZonaButton.addActionListener(e -> {
            try {
                String cedula = cedulaField.getText().trim();
                if (cedula.isEmpty()) {
                    mostrarMensaje("Por favor ingrese una cédula", true);
                    return;
                }
                String zonaInfo = controller.consultarZona(cedula);
                String mesaInfo = controller.consultarMesaVotacion(cedula);
                resultadoArea.setText("Información de Votación:\n\n" + zonaInfo + "\n\n" + mesaInfo);
                resultadoArea.setForeground(Color.BLACK);
            } catch (Exception ex) {
                logger.error("Error al consultar zona y mesa", ex);
                mostrarMensaje("Error al consultar información de votación: " + ex.getMessage(), true);
            }
        });

        listarCandidatosButton.addActionListener(e -> {
            try {
                String candidatos = controller.consultarCandidatos();
                mostrarMensaje(candidatos, false);
            } catch (Exception ex) {
                logger.error("Error al listar candidatos", ex);
                mostrarMensaje("Error al listar candidatos: " + ex.getMessage(), true);
            }
        });

        verVotosButton.addActionListener(e -> {
            try {
                String votos = controller.consultarVotos();
                mostrarMensaje(votos, false);
            } catch (Exception ex) {
                logger.error("Error al consultar votos", ex);
                mostrarMensaje("Error al consultar votos: " + ex.getMessage(), true);
            }
        });

        limpiarButton.addActionListener(e -> {
            cedulaField.setText("");
            resultadoArea.setText("");
            resultadoArea.setForeground(Color.BLACK);
        });
    }

    private JButton createButton(String text, String icon) {
        JButton button = new JButton("<html><center>" + icon + "<br>" + text + "</center></html>");
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efectos hover más sutiles
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(245, 245, 245));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        return button;
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        resultadoArea.setForeground(esError ? Color.RED : Color.BLACK);
        resultadoArea.setText(esError ? "ERROR: " + mensaje : mensaje);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ConsultaUI ui = new ConsultaUI();
            ui.setVisible(true);
        });
    }
}