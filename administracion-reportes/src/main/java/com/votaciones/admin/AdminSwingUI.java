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
        JButton importarBtn = new JButton("Importar desde Excel");
        buttonPanel.add(agregarBtn);
        buttonPanel.add(modificarBtn);
        buttonPanel.add(eliminarBtn);
        buttonPanel.add(listarBtn);
        buttonPanel.add(importarBtn);

        // Tabla de resultados
        String[] columnas = {"ID", "Nombre", "Partido", "Propuestas"};
        Object[][] datos = {};
        JTable tablaCandidatos = new JTable(datos, columnas);
        JScrollPane scrollPane = new JScrollPane(tablaCandidatos);

        // Eventos mejorados
        agregarBtn.addActionListener(e -> {
            if (nombreField.getText().trim().isEmpty() || partidoField.getText().trim().isEmpty() || propuestasField.getText().trim().isEmpty()) {
                logMessage("Error: Todos los campos son obligatorios para agregar un candidato");
                return;
            }
            try {
                adminServer.agregarCandidato(nombreField.getText(), partidoField.getText(), propuestasField.getText(), null);
                logMessage("Candidato agregado: " + nombreField.getText());
                limpiarCampos();
            } catch (Exception ex) {
                logMessage("Error al agregar candidato: " + ex.getMessage());
            }
        });
        modificarBtn.addActionListener(e -> {
            String id = idField.getText();
            if (id == null || id.trim().isEmpty() || !id.matches("\\d+")) {
                logMessage("Error: El ID debe ser un número válido");
                return;
            }
            if (nombreField.getText().trim().isEmpty() || partidoField.getText().trim().isEmpty() || propuestasField.getText().trim().isEmpty()) {
                logMessage("Error: Todos los campos son obligatorios para modificar un candidato");
                return;
            }
            try {
                adminServer.modificarCandidato(id, nombreField.getText(), partidoField.getText(), propuestasField.getText(), null);
                logMessage("Candidato modificado: " + nombreField.getText());
                limpiarCampos();
            } catch (Exception ex) {
                logMessage("Error al modificar candidato: " + ex.getMessage());
            }
        });
        eliminarBtn.addActionListener(e -> {
            String id = idField.getText();
            if (id == null || id.trim().isEmpty() || !id.matches("\\d+")) {
                logMessage("Error: El ID debe ser un número válido");
                return;
            }
            try {
                adminServer.eliminarCandidato(id, null);
                logMessage("Candidato eliminado con ID: " + id);
                limpiarCampos();
            } catch (Exception ex) {
                logMessage("Error al eliminar candidato: " + ex.getMessage());
            }
        });
        listarBtn.addActionListener(e -> {
            try {
                String[] candidatos = adminServer.listarCandidatos(null);
                Object[][] tableData = new Object[candidatos.length][4];
                for (int i = 0; i < candidatos.length; i++) {
                    String[] datosC = candidatos[i].split(",", 4);
                    for (int j = 0; j < 4; j++) tableData[i][j] = datosC[j];
                }
                tablaCandidatos.setModel(new javax.swing.table.DefaultTableModel(tableData, columnas));
                logMessage("Lista de candidatos actualizada");
            } catch (Exception ex) {
                logMessage("Error al listar candidatos: " + ex.getMessage());
            }
        });
        importarBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos Excel", "xlsx"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    adminServer.importarCandidatosDesdeExcel(file.getAbsolutePath(), null);
                    logMessage("Candidatos importados desde Excel: " + file.getName());
                } catch (Exception ex) {
                    logMessage("Error al importar candidatos: " + ex.getMessage());
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

        // Panel de botones (sin campo de zona)
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton resultadosZonaBtn = new JButton("Resultados por Zona");
        JButton resultadosGlobalesBtn = new JButton("Resultados Globales");
        JButton csvBtn = new JButton("Generar CSV");
        buttonPanel.add(resultadosZonaBtn);
        buttonPanel.add(resultadosGlobalesBtn);
        buttonPanel.add(csvBtn);

        // Tabla de resultados
        String[] columnas = {"Candidato", "Partido", "Zona", "Mesa", "Votos", "Porcentaje"};
        Object[][] datos = {};
        JTable tablaResultados = new JTable(datos, columnas);
        JScrollPane scrollPane = new JScrollPane(tablaResultados);

        // Eventos
        resultadosZonaBtn.addActionListener(e -> {
            try {
                String resultados = adminServer.obtenerResultadosPorZona(null);
                String[] filas = resultados.split("\n");
                Object[][] tableData = new Object[filas.length][6];
                for (int i = 0; i < filas.length; i++) {
                    String[] datosC = filas[i].split(",");
                    for (int j = 0; j < datosC.length; j++) tableData[i][j] = datosC[j];
                }
                tablaResultados.setModel(new javax.swing.table.DefaultTableModel(tableData, columnas));
                logMessage("Resultados agrupados por zona obtenidos");
            } catch (Exception ex) {
                logMessage("Error al obtener resultados por zona: " + ex.getMessage());
            }
        });
        resultadosGlobalesBtn.addActionListener(e -> {
            try {
                String resultados = adminServer.obtenerResultadosGlobales(null);
                String[] filas = resultados.split("\n");
                Object[][] tableData = new Object[filas.length][4];
                for (int i = 0; i < filas.length; i++) {
                    String[] datosC = filas[i].split(",");
                    for (int j = 0; j < datosC.length; j++) tableData[i][j] = datosC[j];
                }
                tablaResultados.setModel(new javax.swing.table.DefaultTableModel(tableData, new String[]{"Candidato","Partido","Votos","Porcentaje"}));
                logMessage("Resultados globales obtenidos");
            } catch (Exception ex) {
                logMessage("Error al obtener resultados globales: " + ex.getMessage());
            }
        });
        csvBtn.addActionListener(e -> {
            try {
                adminServer.generarReporteCSVCompleto(null);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new java.io.File("Reporte_Completo.csv"));
                int userSelection = fileChooser.showSaveDialog(null);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    java.nio.file.Files.copy(
                        new java.io.File("Reporte_Completo.csv").toPath(),
                        fileChooser.getSelectedFile().toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                    logMessage("Reporte CSV completo descargado exitosamente");
                }
            } catch (Exception ex) {
                logMessage("Error al generar o descargar CSV: " + ex.getMessage());
            }
        });
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(buttonPanel, BorderLayout.NORTH);
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