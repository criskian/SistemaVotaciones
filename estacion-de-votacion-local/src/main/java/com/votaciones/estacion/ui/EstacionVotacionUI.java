package com.votaciones.estacion.ui;

import com.votaciones.estacion.GestionMesasProxy;
import com.votaciones.estacion.lotes.GestorLotes;
import com.votaciones.estacion.MesaVotacion;
import com.votaciones.estacion.SecurityServiceHelper;
import VotingSystem.Voto;
import VotingSystem.Candidato;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;

public class EstacionVotacionUI extends JFrame {
    private static final int MESA_ID = 1; // ID de la mesa por defecto
    private static final List<String> CANDIDATOS = Arrays.asList("Candidato A", "Candidato B", "Candidato C");
    
    // Componentes UI
    private JTable votosTable;
    private DefaultTableModel tableModel;
    private JTextArea logArea;
    private JLabel estadoLabel;
    private JLabel horaLabel;
    private JLabel mesaIdLabel;
    private JProgressBar estadoBar;
    private JButton registrarVotoBtn;
    private JButton enviarLotesBtn;
    private JButton mostrarEstadisticasBtn;
    
    // Componentes principales del sistema
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private GestionMesasProxy proxy;
    private GestorLotes gestorLotes;
    private MesaVotacion mesaVotacion;
    private SecurityServiceHelper securityHelper;
    private boolean sistemaActivo = false;
    
    // Buffer para logs antes de que la UI esté lista
    private final java.util.List<String> logBuffer = new java.util.ArrayList<>();

    private static String zonaVotacion = "";
    private static int numeroMesa = 1;

    private int zonaIdColegio = -1;
    private int mesaId = -1;
    private int colegioId = -1;

    private static int puerto = 10010;
    private static int idMesaConfig = 1;
    private static int idZonaConfig = 1;
    private static String colegioConfig = "";
    private static int numeroMesaConfig = 1;

    static {
        // Cargar configuración siguiendo el patrón de otros nodos
        try (InputStream input = EstacionVotacionUI.class.getClassLoader().getResourceAsStream("estacion.properties")) {
            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
                puerto = Integer.parseInt(prop.getProperty("mesa.puerto.default", "10010"));
                idMesaConfig = Integer.parseInt(prop.getProperty("mesa.id.default", "1"));
                idZonaConfig = Integer.parseInt(prop.getProperty("mesa.zona.default", "1"));
                System.out.println("[DEBUG] Configuración cargada desde estacion.properties");
            } else {
                System.out.println("[DEBUG] No se encontró estacion.properties, usando valores por defecto");
            }
        } catch (Exception e) {
            System.err.println("[WARN] Error leyendo estacion.properties: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("[DEBUG] Argumentos recibidos: " + java.util.Arrays.toString(args));
        
        // Validar argumentos (siguiendo el patrón de otros nodos)
        if (args.length < 2) {
            JOptionPane.showMessageDialog(null, 
                "Argumentos requeridos: [colegio] [numero_mesa]\n" +
                "Ejemplo: \"Colegio San José\" 101", 
                "Faltan argumentos", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        colegioConfig = args[0];
        try {
            numeroMesaConfig = Integer.parseInt(args[1].trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, 
                "El número de mesa debe ser un entero.\nRecibido: '" + args[1] + "'", 
                "Argumento inválido", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Validar mesa directamente contra BD (patrón de otros nodos exitosos)
        if (!validarMesaEnBaseDatos(colegioConfig, numeroMesaConfig)) {
            System.exit(1);
        }
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error al establecer Look and Feel: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            try {
                EstacionVotacionUI app = new EstacionVotacionUI();
                app.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Error al iniciar la aplicación:\n" + e.getMessage(),
                    "Error Crítico",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    public EstacionVotacionUI() {
        super("Estación de Votación Local - Mesa: " + idMesaConfig + " | Zona: " + idZonaConfig + " | Colegio: " + colegioConfig + " | Número: " + numeroMesaConfig);
        
        try {
            System.out.println("[DEBUG] Iniciando constructor EstacionVotacionUI...");
            
            if (!validarColegioYMesaPorId(idMesaConfig, idZonaConfig)) {
                JOptionPane.showMessageDialog(null,
                    "La mesa o la zona no existen o no están relacionadas correctamente en la base de datos central.",
                    "Error de configuración",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            
            System.out.println("[DEBUG] Activando mesa en BD...");
            activarMesaEnBD();
            
            // Inicializar componentes
            System.out.println("[DEBUG] Inicializando GestionMesasProxy...");
            proxy = new GestionMesasProxy();
            
            System.out.println("[DEBUG] Inicializando GestorLotes...");
            gestorLotes = new GestorLotes();
            
            System.out.println("[DEBUG] Inicializando MesaVotacion...");
            mesaVotacion = new MesaVotacion(numeroMesaConfig, CANDIDATOS);
            
            System.out.println("[DEBUG] Inicializando SecurityServiceHelper...");
            securityHelper = new SecurityServiceHelper();
            
            System.out.println("[DEBUG] Inicializando UI...");
            initializeUI();
            
            System.out.println("[DEBUG] Iniciando servicios...");
            startServices();
            
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    desactivarMesaEnBD();
                    shutdown();
                }
            });
            
            System.out.println("[DEBUG] Constructor completado exitosamente");
            mostrarMensajeInicio();
            
        } catch (Exception e) {
            System.err.println("[ERROR] Error en constructor EstacionVotacionUI: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error durante la inicialización:\n" + e.getClass().getSimpleName() + ": " + e.getMessage(),
                "Error de Inicialización",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        add(createPanelSuperior(), BorderLayout.NORTH);
        add(createPanelCentral(), BorderLayout.CENTER);
        add(createPanelInferior(), BorderLayout.SOUTH);
        
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Configurar ventana como aplicación independiente
        setAlwaysOnTop(true);
        toFront();
        requestFocus();
        
        // Después de 2 segundos, quitar always on top
        Timer timer = new Timer(2000, e -> setAlwaysOnTop(false));
        timer.setRepeats(false);
        timer.start();
    }

    private JPanel createPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Estación de Votación Local"));
        panel.setBackground(new Color(240, 248, 255));
        
        // Panel de estado
        JPanel statusPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Estado del Sistema"));
        
        mesaIdLabel = new JLabel("Mesa ID: " + idMesaConfig);
        JLabel zonaLabel = new JLabel("Zona: " + colegioConfig);
        estadoLabel = new JLabel("Estado: CONECTADO");
        horaLabel = new JLabel("Hora: --:--:--");
        
        Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 14);
        mesaIdLabel.setFont(labelFont);
        zonaLabel.setFont(labelFont);
        estadoLabel.setFont(labelFont);
        horaLabel.setFont(labelFont);
        
        estadoLabel.setForeground(new Color(0, 100, 0));
        
        statusPanel.add(mesaIdLabel);
        statusPanel.add(zonaLabel);
        statusPanel.add(estadoLabel);
        statusPanel.add(horaLabel);
        
        // Panel de controles
        JPanel controlsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Controles"));
        
        registrarVotoBtn = createStyledButton("Registrar Voto", "[1]");
        enviarLotesBtn = createStyledButton("Enviar Lotes Pendientes", "[2]");
        mostrarEstadisticasBtn = createStyledButton("Mostrar Estadísticas", "[3]");
        
        registrarVotoBtn.addActionListener(e -> registrarVoto());
        enviarLotesBtn.addActionListener(e -> enviarLotesPendientes());
        mostrarEstadisticasBtn.addActionListener(e -> mostrarEstadisticas());
        
        controlsPanel.add(registrarVotoBtn);
        controlsPanel.add(enviarLotesBtn);
        controlsPanel.add(mostrarEstadisticasBtn);
        
        panel.add(statusPanel, BorderLayout.WEST);
        panel.add(controlsPanel, BorderLayout.EAST);
        
        return panel;
    }

    private JButton createStyledButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }

    private JPanel createPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Registro de Votos"));
        
        String[] columnas = {
            "ID Voto", "Cédula", "Candidato", "Hora", "Estado"
        };
        
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        votosTable = new JTable(tableModel);
        votosTable.setFillsViewportHeight(true);
        votosTable.setRowHeight(25);
        
        // Configurar renderizador para filas alternadas
        votosTable.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        
        JScrollPane scrollPane = new JScrollPane(votosTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Log del Sistema"));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        // Volcar logs acumulados en el buffer
        for (String logMessage : logBuffer) {
            logArea.append(logMessage);
        }
        logBuffer.clear(); // Limpiar el buffer
        
        // Mover el cursor al final
        if (logArea.getDocument().getLength() > 0) {
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(800, 150));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void registrarVoto() {
        String cedula = JOptionPane.showInputDialog(this, "Ingrese la cédula del votante:");
        if (cedula == null || cedula.trim().isEmpty()) {
            agregarLog("[DEBUG] Votación cancelada: cédula vacía o nula");
            return;
        }

        agregarLog("Iniciando proceso de votación para cédula: " + cedula);

        // ==== VALIDACIONES COMPLETAS DE SEGURIDAD (E8, E12, E13) ====
        // Usar el nodo de seguridad para todas las validaciones según diagrama de deployment
        agregarLog("[DEBUG] Iniciando validaciones de seguridad...");
        
        boolean validacionSeguridad = securityHelper.validarSeguridadCompleta(cedula, idMesaConfig, zonaIdColegio);
        agregarLog("[DEBUG] Resultado validación seguridad: " + validacionSeguridad);
        
        if (!validacionSeguridad) {
            String mensaje = "VOTO DENEGADO: El ciudadano no pasó las validaciones de seguridad.\n" +
                           "Motivos posibles:\n" +
                           "- Antecedentes criminales (E12)\n" +
                           "- Ya votó anteriormente (E13)\n" +
                           "- Mesa/zona incorrecta (E8)";
            JOptionPane.showMessageDialog(this, mensaje, "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            agregarLog("VOTO DENEGADO por validaciones de seguridad: " + cedula);
            return;
        }

        agregarLog("Validaciones de seguridad exitosas para: " + cedula);

        // ==== VALIDACIÓN ADICIONAL CON SISTEMA CENTRAL (OPCIONAL) ====
        // Verificar también contra el sistema central como respaldo
        agregarLog("[DEBUG] Iniciando validación con sistema central...");
        
        boolean validacionCentralExitosa = false;
        try {
            boolean puedeVotarCentral = proxy.verificarEstadoZona(cedula, String.valueOf(zonaIdColegio));
            agregarLog("[DEBUG] Resultado validación central: " + puedeVotarCentral);
            
            if (!puedeVotarCentral) {
                JOptionPane.showMessageDialog(this,
                    "El sistema central indica que el votante no puede votar en esta zona.",
                    "Validación Central Fallida",
                    JOptionPane.WARNING_MESSAGE);
                agregarLog("VOTO DENEGADO por sistema central: " + cedula);
                return;
            }
            validacionCentralExitosa = true;
        } catch (Exception e) {
            // Si la validación central falla, continuar con validación básica
            agregarLog("[WARNING] Error en validación central: " + e.getClass().getSimpleName());
            agregarLog("[INFO] Continuando con validación básica...");
            
            try {
                boolean validacionBasica = proxy.validarVotante(cedula);
                agregarLog("[DEBUG] Resultado validación básica: " + validacionBasica);
                
                if (!validacionBasica) {
                    // En vez de denegar, mostrar advertencia pero continuar
                    agregarLog("[WARNING] Validación básica falló, pero continuando por validaciones de seguridad exitosas");
                    int respuesta = JOptionPane.showConfirmDialog(this,
                        "Las validaciones centrales no están disponibles, pero las validaciones de seguridad locales pasaron.\n" +
                        "¿Desea continuar con el registro del voto?",
                        "Modo Failsafe",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    
                    if (respuesta != JOptionPane.YES_OPTION) {
                        agregarLog("VOTO CANCELADO por el usuario en modo failsafe: " + cedula);
                        return;
                    }
                    agregarLog("[INFO] Usuario autoriza voto en modo failsafe para: " + cedula);
                } else {
                    validacionCentralExitosa = true;
                }
            } catch (Exception e2) {
                // Si todas las validaciones centrales fallan, modo failsafe automático
                agregarLog("[WARNING] Todas las validaciones centrales fallaron. Modo failsafe automático activado.");
                agregarLog("[INFO] Permitiendo voto basado en validaciones de seguridad locales exitosas.");
                
                int respuesta = JOptionPane.showConfirmDialog(this,
                    "El sistema central no está disponible, pero las validaciones de seguridad locales pasaron.\n" +
                    "¿Desea continuar con el registro del voto en modo offline?",
                    "Modo Failsafe - Sistema Central Offline",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (respuesta != JOptionPane.YES_OPTION) {
                    agregarLog("VOTO CANCELADO por el usuario en modo offline: " + cedula);
                    return;
                }
                agregarLog("[INFO] Usuario autoriza voto en modo offline para: " + cedula);
            }
        }

        agregarLog("[DEBUG] Todas las validaciones pasaron. Procediendo a selección de candidato...");

        // ==== SELECCIÓN DE CANDIDATO ====
        String candidato = seleccionarCandidato();
        agregarLog("[DEBUG] Candidato seleccionado: " + candidato);
        
        if (candidato == null) {
            agregarLog("Votación cancelada para: " + cedula);
            return;
        }

        agregarLog("[DEBUG] Creando objeto voto...");
        Voto voto = new Voto();
        voto.idVotante = cedula;
        voto.idCandidato = Integer.parseInt(candidato);
        
        // ==== REGISTRO EN SISTEMA CENTRAL ====
        agregarLog("Registrando voto en sistema central para: " + cedula);
        
        boolean registroExitoso = false;
        try {
            registroExitoso = proxy.registrarVoto(cedula, voto.idCandidato, idMesaConfig, String.valueOf(zonaIdColegio));
            agregarLog("[DEBUG] Resultado registro central: " + registroExitoso);
            
            if (!registroExitoso) {
                // Si el registro central falla, continuar con registro local únicamente
                agregarLog("[WARNING] Error al registrar en sistema central, continuando con registro local");
                int respuesta = JOptionPane.showConfirmDialog(this,
                    "No se pudo registrar el voto en el sistema central.\n" +
                    "¿Desea continuar con el registro local? (Se sincronizará cuando esté disponible)",
                    "Registro Local",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (respuesta != JOptionPane.YES_OPTION) {
                    agregarLog("VOTO CANCELADO por el usuario - registro central falló: " + cedula);
                    return;
                }
                agregarLog("[INFO] Continuando con registro local para: " + cedula);
            }
        } catch (Exception e) {
            // Error de comunicación con sistema central
            agregarLog("[WARNING] Error de comunicación con sistema central: " + e.getClass().getSimpleName());
            int respuesta = JOptionPane.showConfirmDialog(this,
                "No se puede conectar con el sistema central.\n" +
                "¿Desea registrar el voto localmente? (Se sincronizará cuando esté disponible)",
                "Sistema Central No Disponible",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (respuesta != JOptionPane.YES_OPTION) {
                agregarLog("VOTO CANCELADO por el usuario - sistema central no disponible: " + cedula);
                return;
            }
            agregarLog("[INFO] Usuario autoriza registro local para: " + cedula);
        }

        // ==== REGISTRO EN LOTE LOCAL ====
        agregarLog("[DEBUG] Agregando voto al lote local...");
        gestorLotes.agregarVoto(voto);
        actualizarTablaVotos();
        
        // ==== CONFIRMACIÓN EXITOSA ====
        JOptionPane.showMessageDialog(this, 
            "¡Voto registrado correctamente!\n" +
            "Cédula: " + cedula + "\n" +
            "Candidato: " + candidato + "\n" +
            "Mesa: " + idMesaConfig + " | Zona: " + zonaIdColegio,
            "Voto Exitoso", 
            JOptionPane.INFORMATION_MESSAGE);
        
        agregarLog("VOTO EXITOSO registrado para: " + cedula + " (Candidato: " + candidato + ")");
    }

    // Método eliminado: validarCiudadanoEnZona - ahora se usa el nodo de seguridad

    private void enviarLotesPendientes() {
        try {
            // Aquí deberías recolectar los votos pendientes y enviarlos usando el nuevo método
            gestorLotes.enviarLotesPendientes(proxy, numeroMesaConfig, colegioConfig);
            agregarLog("Lotes pendientes enviados correctamente");
        } catch (Exception e) {
            agregarLog("Error al enviar lotes: " + e.getMessage());
        }
    }

    private void mostrarEstadisticas() {
        StringBuilder stats = new StringBuilder();
        stats.append("Estadísticas de la Mesa ").append(numeroMesaConfig).append("\n\n");
        
        // Mostrar votos por candidato
        stats.append("Votos por candidato:\n");
        mesaVotacion.getResumenVotos().forEach((candidato, votos) -> 
            stats.append(String.format("- %s: %d votos\n", candidato, votos))
        );
        
        JOptionPane.showMessageDialog(this,
            stats.toString(),
            "Estadísticas",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarTablaVotos() {
        // Implementar la actualización de la tabla con los votos registrados
        // Esto dependerá de cómo esté implementada la clase MesaVotacion
    }

    private void agregarLog(String mensaje) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String logMessage = String.format("[%s] %s%n", timestamp, mensaje);
        
        if (logArea != null) {
            // UI está lista, escribir directamente
            logArea.append(logMessage);
            logArea.setCaretPosition(logArea.getDocument().getLength());
        } else {
            // UI no está lista, acumular en buffer
            logBuffer.add(logMessage);
            // También imprimir en consola para debug
            System.out.print("[LOG] " + logMessage);
        }
    }

    private void mostrarMensajeInicio() {
        agregarLog("=== SISTEMA DE ESTACIÓN DE VOTACIÓN LOCAL ===");
        agregarLog("Colegio: " + colegioConfig + " | Mesa: " + numeroMesaConfig);
        agregarLog("ID Mesa: " + idMesaConfig + " | ID Zona: " + zonaIdColegio);
        agregarLog("Puerto: " + puerto);
        agregarLog("===================================");
        
        // Estado de conexiones
        agregarLog("Conectado al servidor de gestión de mesas (puerto 10004)");
        if (securityHelper.isConnected()) {
            agregarLog("[OK] Conectado al nodo de seguridad (puerto 10005)");
            agregarLog("[OK] Validaciones E8, E12, E13 habilitadas");
        } else {
            agregarLog("[WARNING] Sin conexión al nodo de seguridad");
            agregarLog("[WARNING] Modo failsafe: validaciones básicas únicamente");
        }
        
        agregarLog("Sistema listo para recibir votantes");
    }

    private void startServices() {
        // Programar actualización de hora
        scheduler.scheduleAtFixedRate(this::actualizarHora, 0, 1, TimeUnit.SECONDS);
        
        // Programar verificación de estado
        scheduler.scheduleAtFixedRate(this::verificarEstado, 0, 5, TimeUnit.SECONDS);
    }

    private void actualizarHora() {
        SwingUtilities.invokeLater(() -> {
            String hora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            horaLabel.setText("Hora: " + hora);
        });
    }

    private void verificarEstado() {
        // Implementar verificación de estado del sistema
    }

    private void shutdown() {
        try {
            agregarLog("Cerrando sistema de estación de votación...");
            
            // Cerrar conexiones
            if (securityHelper != null) {
                securityHelper.close();
            }
            if (proxy != null) {
                proxy.close();
            }
            
            // Cerrar scheduler
            scheduler.shutdown();
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            
            agregarLog("Sistema cerrado correctamente");
        } catch (Exception e) {
            agregarLog("Error al cerrar el sistema: " + e.getMessage());
        }
    }

    private static class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
            }
            return c;
        }
    }

    private boolean validarColegioYMesaPorId(int idMesa, int idZonaConfig) {
        try {
            String url = "jdbc:postgresql://localhost:5432/sistema_votaciones";
            String user = "postgres";
            String pass = "postgres";
            try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, pass)) {
                // Validar mesa y obtener colegio_id
                String sqlMesa = "SELECT colegio_id FROM mesas_votacion WHERE id = ?";
                int colegioIdLocal = -1;
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlMesa)) {
                    stmt.setInt(1, idMesa);
                    java.sql.ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null,
                            "No se encontró la mesa con id: '" + idMesa + "' en la base de datos.",
                            "Error de mesa",
                            JOptionPane.ERROR_MESSAGE);
                        System.out.println("Mesa no encontrada: id=" + idMesa);
                        return false;
                    }
                    colegioIdLocal = rs.getInt("colegio_id");
                    System.out.println("Mesa encontrada: id=" + idMesa + ", colegio_id=" + colegioIdLocal);
                }
                // Validar que el colegio pertenezca a la zona y obtener zona_id real
                String sqlColegio = "SELECT zona_id FROM colegios WHERE id = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlColegio)) {
                    stmt.setInt(1, colegioIdLocal);
                    java.sql.ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null,
                            "No se encontró el colegio con id: '" + colegioIdLocal + "' en la base de datos.",
                            "Error de colegio",
                            JOptionPane.ERROR_MESSAGE);
                        System.out.println("Colegio no encontrado: id=" + colegioIdLocal);
                        return false;
                    }
                    int zonaIdBD = rs.getInt("zona_id");
                    zonaIdColegio = zonaIdBD; // Asignar el zona_id real para validación
                    if (zonaIdBD != idZonaConfig) {
                        JOptionPane.showMessageDialog(null,
                            "La zona de la mesa no coincide con la zona configurada.",
                            "Error de zona",
                            JOptionPane.ERROR_MESSAGE);
                        System.out.println("Zona de colegio: " + zonaIdBD + " vs zona configurada: " + idZonaConfig);
                        return false;
                    }
                    System.out.println("Colegio encontrado: id=" + colegioIdLocal + ", zona_id=" + zonaIdBD);
                }
                return true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Error validando mesa y zona: " + e.getMessage(),
                "Error de validación",
                JOptionPane.ERROR_MESSAGE);
            System.err.println("Error validando mesa y zona: " + e.getMessage());
            return false;
        }
    }

    private String seleccionarCandidato() {
        try {
            agregarLog("[DEBUG] Obteniendo lista de candidatos del sistema central...");
            java.util.List<Candidato> candidatos = proxy.listarCandidatos();
            agregarLog("[DEBUG] Candidatos obtenidos: " + candidatos.size());
            
            if (candidatos.isEmpty()) {
                agregarLog("[DEBUG] No hay candidatos disponibles");
                JOptionPane.showMessageDialog(this, "No hay candidatos disponibles.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            String[] nombres = candidatos.stream().map(c -> c.nombre + " (" + c.partido + ")").toArray(String[]::new);
            agregarLog("[DEBUG] Mostrando diálogo de selección con " + nombres.length + " opciones");
            
            String seleccion = (String) JOptionPane.showInputDialog(this, "Seleccione el candidato:", "Registro de Voto", JOptionPane.QUESTION_MESSAGE, null, nombres, nombres[0]);
            agregarLog("[DEBUG] Usuario seleccionó: " + seleccion);
            
            if (seleccion == null) {
                agregarLog("[DEBUG] Usuario canceló la selección");
                return null;
            }
            
            // Buscar el candidato seleccionado
            for (Candidato c : candidatos) {
                if (seleccion.startsWith(c.nombre)) {
                    agregarLog("[DEBUG] Candidato encontrado - ID: " + c.id + ", Nombre: " + c.nombre);
                    return String.valueOf(c.id);
                }
            }
            
            agregarLog("[DEBUG] ERROR: No se pudo encontrar el candidato seleccionado");
            return null;
        } catch (Exception e) {
            agregarLog("[DEBUG] ERROR obteniendo candidatos: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error al obtener candidatos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void activarMesaEnBD() {
        try {
            agregarLog("[DEBUG] Intentando activar mesa en BD - ID: " + idMesaConfig);
            String url = "jdbc:postgresql://localhost:5432/sistema_votaciones";
            String user = "postgres";
            String pass = "postgres";
            try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, pass)) {
                String sql = "UPDATE mesas_votacion SET estado = 'ACTIVA', ultima_actualizacion = NOW() WHERE id = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, idMesaConfig);
                    int updated = stmt.executeUpdate();
                    agregarLog("[DEBUG] Mesa " + idMesaConfig + " activada. Filas actualizadas: " + updated);
                    if (updated > 0) {
                        agregarLog("[OK] Mesa activada exitosamente en la base de datos");
                    } else {
                        agregarLog("[WARNING] No se pudo activar la mesa - ID no encontrado: " + idMesaConfig);
                    }
                }
            }
        } catch (Exception e) {
            agregarLog("[ERROR] Error activando mesa en BD: " + e.getMessage());
            System.err.println("Error activando mesa en BD: " + e.getMessage());
        }
    }

    /**
     * Validar mesa directamente contra BD siguiendo el patrón de otros nodos exitosos
     * Similar a como lo hace el sistema de gestión de mesas
     */
    private static boolean validarMesaEnBaseDatos(String colegio, int numeroMesa) {
        try {
            // Cargar configuración BD desde properties interno
            Properties prop = new Properties();
            try (InputStream input = EstacionVotacionUI.class.getClassLoader().getResourceAsStream("estacion.properties")) {
                if (input != null) {
                    prop.load(input);
                }
            }
            
            String dbUrl = prop.getProperty("db.url", "jdbc:postgresql://localhost:5432/sistema_votaciones");
            String dbUser = prop.getProperty("db.user", "postgres");
            String dbPassword = prop.getProperty("db.password", "postgres");
            
            try (java.sql.Connection conn = java.sql.DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                // Buscar mesa por nombre de colegio y número (patrón de validación directo)
                String sql = "SELECT m.id, m.numero, c.id as colegio_id, c.zona_id " +
                            "FROM mesas_votacion m " +
                            "INNER JOIN colegios c ON m.colegio_id = c.id " +
                            "WHERE LOWER(c.nombre) = LOWER(?) AND m.numero = ?";
                
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, colegio);
                    stmt.setInt(2, numeroMesa);
                    java.sql.ResultSet rs = stmt.executeQuery();
                    
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null,
                            "No se encontró la mesa en la base de datos:\n" +
                            "Colegio: '" + colegio + "'\n" +
                            "Mesa: " + numeroMesa + "\n\n" +
                            "Verifica que la mesa esté registrada en el sistema central.",
                            "Mesa no encontrada", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    
                    // Configurar variables globales
                    idMesaConfig = rs.getInt("id");
                    idZonaConfig = rs.getInt("zona_id");
                    
                    System.out.println("[DEBUG] Mesa validada exitosamente:");
                    System.out.println("  - ID Mesa: " + idMesaConfig);
                    System.out.println("  - ID Zona: " + idZonaConfig);
                    System.out.println("  - Colegio: " + colegio);
                    System.out.println("  - Número Mesa: " + numeroMesa);
                    
                    return true;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Error conectando a la base de datos:\n" + e.getMessage() + "\n\n" +
                "Verifica que PostgreSQL esté ejecutándose y que la base de datos exista.",
                "Error de conexión", JOptionPane.ERROR_MESSAGE);
            System.err.println("[ERROR] Error validando mesa: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void desactivarMesaEnBD() {
        try {
            String url = "jdbc:postgresql://localhost:5432/sistema_votaciones";
            String user = "postgres";
            String pass = "postgres";
            try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, pass)) {
                String sql = "UPDATE mesas_votacion SET estado = 'INACTIVA', ultima_actualizacion = NOW() WHERE id = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, idMesaConfig);
                    int updated = stmt.executeUpdate();
                    System.out.println("[DEBUG] Mesa " + idMesaConfig + " desactivada. Filas actualizadas: " + updated);
                }
            }
        } catch (Exception e) {
            System.err.println("Error desactivando mesa en BD: " + e.getMessage());
        }
    }
} 