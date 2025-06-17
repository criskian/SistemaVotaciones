package com.votaciones.admin;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminSwingUI extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(AdminSwingUI.class);
    private AdminServerI adminServer;
    private JTextArea logArea;
    private JTextField nombreField, partidoField, propuestasField, idField, zonaField;
    private JTextArea resultadosArea;

    public AdminSwingUI() {
        adminServer = new AdminServerI();
        setupUI();
    }

    private void setupUI() {
        setTitle("Panel de Administración y Reportes - Sistema de Votaciones");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Panel principal con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pestaña de Gestión de Candidatos
        tabbedPane.addTab("Gestión de Candidatos", createCandidatosPanel());
        
        // Pestaña de Reportes
        tabbedPane.addTab("Reportes y Resultados", createReportesPanel());
        
        // Pestaña de Logs
        tabbedPane.addTab("Logs del Sistema", createLogsPanel());

        add(tabbedPane);
        
        logMessage("Panel de administración iniciado correctamente");
    }

    private JPanel createCandidatosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Gestión de Candidatos"));

        // Panel de entrada
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        nombreField = new JTextField(20);
        partidoField = new JTextField(20);
        propuestasField = new JTextField(20);
        idField = new JTextField(20);

        inputPanel.add(new JLabel("Nombre:"));
        inputPanel.add(nombreField);
        inputPanel.add(new JLabel("Partido:"));
        inputPanel.add(partidoField);
        inputPanel.add(new JLabel("Propuestas:"));
        inputPanel.add(propuestasField);
        inputPanel.add(new JLabel("ID (para modificar/eliminar):"));
        inputPanel.add(idField);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton agregarBtn = new JButton("Agregar Candidato");
        JButton modificarBtn = new JButton("Modificar Candidato");
        JButton eliminarBtn = new JButton("Eliminar Candidato");
        JButton listarBtn = new JButton("Listar Candidatos");

        buttonPanel.add(agregarBtn);
        buttonPanel.add(modificarBtn);
        buttonPanel.add(eliminarBtn);
        buttonPanel.add(listarBtn);

        // Área de resultados
        resultadosArea = new JTextArea(10, 50);
        resultadosArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultadosArea);

        // Eventos
        agregarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    adminServer.agregarCandidato(nombreField.getText(), partidoField.getText(), propuestasField.getText(), null);
                    logMessage("Candidato agregado: " + nombreField.getText());
                    limpiarCampos();
                } catch (Exception ex) {
                    logMessage("Error al agregar candidato: " + ex.getMessage());
                }
            }
        });

        modificarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    adminServer.modificarCandidato(idField.getText(), nombreField.getText(), partidoField.getText(), propuestasField.getText(), null);
                    logMessage("Candidato modificado: " + nombreField.getText());
                    limpiarCampos();
                } catch (Exception ex) {
                    logMessage("Error al modificar candidato: " + ex.getMessage());
                }
            }
        });

        eliminarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    adminServer.eliminarCandidato(idField.getText(), null);
                    logMessage("Candidato eliminado con ID: " + idField.getText());
                    limpiarCampos();
                } catch (Exception ex) {
                    logMessage("Error al eliminar candidato: " + ex.getMessage());
                }
            }
        });

        listarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String[] candidatos = adminServer.listarCandidatos(null);
                    StringBuilder sb = new StringBuilder();
                    for (String candidato : candidatos) {
                        sb.append(candidato).append("\n");
                    }
                    resultadosArea.setText(sb.toString());
                    logMessage("Lista de candidatos actualizada");
                } catch (Exception ex) {
                    logMessage("Error al listar candidatos: " + ex.getMessage());
                }
            }
        });

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReportesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Reportes y Resultados"));

        // Panel de entrada
        JPanel inputPanel = new JPanel(new FlowLayout());
        zonaField = new JTextField(20);
        inputPanel.add(new JLabel("Código de Zona:"));
        inputPanel.add(zonaField);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton procesarBtn = new JButton("Procesar Votos");
        JButton resultadosZonaBtn = new JButton("Resultados por Zona");
        JButton resultadosGlobalesBtn = new JButton("Resultados Globales");
        JButton csvBtn = new JButton("Generar CSV");
        JButton excelBtn = new JButton("Exportar Excel");

        buttonPanel.add(procesarBtn);
        buttonPanel.add(resultadosZonaBtn);
        buttonPanel.add(resultadosGlobalesBtn);
        buttonPanel.add(csvBtn);
        buttonPanel.add(excelBtn);

        // Área de resultados
        JTextArea reportesArea = new JTextArea(15, 50);
        reportesArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportesArea);

        // Eventos
        procesarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    adminServer.procesarVotos(zonaField.getText(), null);
                    logMessage("Votos procesados para zona: " + zonaField.getText());
                } catch (Exception ex) {
                    logMessage("Error al procesar votos: " + ex.getMessage());
                }
            }
        });

        resultadosZonaBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String resultados = adminServer.obtenerResultadosZona(zonaField.getText(), null);
                    reportesArea.setText(resultados);
                    logMessage("Resultados obtenidos para zona: " + zonaField.getText());
                } catch (Exception ex) {
                    logMessage("Error al obtener resultados: " + ex.getMessage());
                }
            }
        });

        resultadosGlobalesBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String resultados = adminServer.obtenerResultadosGlobales(null);
                    reportesArea.setText(resultados);
                    logMessage("Resultados globales obtenidos");
                } catch (Exception ex) {
                    logMessage("Error al obtener resultados globales: " + ex.getMessage());
                }
            }
        });

        csvBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    adminServer.generarReporteCSV("GLOBAL", null);
                    logMessage("Reporte CSV generado exitosamente");
                } catch (Exception ex) {
                    logMessage("Error al generar CSV: " + ex.getMessage());
                }
            }
        });

        excelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    adminServer.exportarResultadosExcel(null);
                    logMessage("Resultados exportados a Excel exitosamente");
                } catch (Exception ex) {
                    logMessage("Error al exportar Excel: " + ex.getMessage());
                }
            }
        });

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Logs del Sistema"));

        // Área de logs
        logArea = new JTextArea(20, 60);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton limpiarBtn = new JButton("Limpiar Logs");
        JButton validarBtn = new JButton("Validar Integridad");

        buttonPanel.add(limpiarBtn);
        buttonPanel.add(validarBtn);

        // Eventos
        limpiarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logMessage("Logs limpiados");
            }
        });

        validarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    boolean valido = adminServer.validarIntegridadResultados(null);
                    if (valido) {
                        logMessage("✅ Validación de integridad: EXITOSA");
                    } else {
                        logMessage("❌ Validación de integridad: FALLIDA");
                    }
                } catch (Exception ex) {
                    logMessage("Error en validación: " + ex.getMessage());
                }
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void logMessage(String message) {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        String logEntry = "[" + timestamp + "] " + message + "\n";
        logArea.append(logEntry);
        logger.info(message);
    }

    private void limpiarCampos() {
        nombreField.setText("");
        partidoField.setText("");
        propuestasField.setText("");
        idField.setText("");
        zonaField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdminSwingUI().setVisible(true);
            }
        });
    }
} 