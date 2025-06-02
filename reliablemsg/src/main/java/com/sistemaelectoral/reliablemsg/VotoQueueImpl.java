package com.sistemaelectoral.reliablemsg;

import Ice.Current;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.sistemaelectoral.reliablemsg.ReliableMsg._VotoQueueDisp;

import javax.jms.*;
import java.util.concurrent.ConcurrentLinkedQueue;

// Asegúrate de que la interfaz ReliableMsg.VotoQueue esté correctamente importada y exista.
// Si no existe, debes crearla en el paquete com.sistemaelectoral.reliablemsg, por ejemplo:
//
// package com.sistemaelectoral.reliablemsg;
// public interface VotoQueue {
//     void enviarComando(String comando, com.zeroc.Ice.Current current);
//     // Otros métodos según sea necesario
// }

public class VotoQueueImpl extends _VotoQueueDisp {
    private final Logger logger;
    private final Connection connection;
    private final Session session;
    private final MessageProducer commandProducer;
    private final MessageConsumer votoConsumer;
    private final MessageProducer eventProducer;
    private final ConcurrentLinkedQueue<String> votoQueue;
    
    private static final String COMMAND_QUEUE = "COMMAND_QUEUE";
    private static final String VOTO_QUEUE = "VOTO_QUEUE";
    private static final String EVENT_TOPIC = "EVENT_TOPIC";
    
    public VotoQueueImpl() throws JMSException {
        logger = new Logger("VotoQueue");
        votoQueue = new ConcurrentLinkedQueue<>();
        
        // Configurar conexión ActiveMQ
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        connection = factory.createConnection();
        connection.start();
        
        // Crear sesión
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        // Configurar colas y tópicos
        Queue commandQueue = session.createQueue(COMMAND_QUEUE);
        Queue votoQueue = session.createQueue(VOTO_QUEUE);
        Topic eventTopic = session.createTopic(EVENT_TOPIC);
        
        // Crear productores y consumidores
        commandProducer = session.createProducer(commandQueue);
        votoConsumer = session.createConsumer(votoQueue);
        eventProducer = session.createProducer(eventTopic);
        
        // Configurar listener para votos
        votoConsumer.setMessageListener(message -> {
            try {
                if (message instanceof TextMessage) {
                    String voto = ((TextMessage) message).getText();
                    this.votoQueue.offer(voto);
                    logger.log("Voto recibido: " + voto);
                }
            } catch (JMSException e) {
                logger.log("Error al procesar voto: " + e.getMessage());
            }
        });
        
        logger.log("VotoQueue inicializado");
    }
    
    @Override
    public void enviarComando(String comando, Current current) {
        try {
            TextMessage message = session.createTextMessage(comando);
            commandProducer.send(message);
            logger.log("Comando enviado: " + comando);
        } catch (JMSException e) {
            logger.log("Error al enviar comando: " + e.getMessage());
            throw new RuntimeException("Error al enviar comando", e);
        }
    }
    
    @Override
    public String getVoto(Current current) {
        String voto = votoQueue.poll();
        if (voto != null) {
            logger.log("Voto entregado: " + voto);
        }
        return voto;
    }
    
    @Override
    public void publicarEvento(String evento, Current current) {
        try {
            TextMessage message = session.createTextMessage(evento);
            eventProducer.send(message);
            logger.log("Evento publicado: " + evento);
        } catch (JMSException e) {
            logger.log("Error al publicar evento: " + e.getMessage());
            throw new RuntimeException("Error al publicar evento", e);
        }
    }
    
    public void shutdown() {
        try {
            if (connection != null) {
                connection.close();
            }
            logger.log("VotoQueue cerrado");
        } catch (JMSException e) {
            logger.log("Error al cerrar conexión: " + e.getMessage());
        }
    }
} 