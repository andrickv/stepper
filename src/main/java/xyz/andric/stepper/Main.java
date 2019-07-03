package xyz.andric.stepper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import xyz.andric.stepper.Sensor;
import xyz.andric.stepper.StepMotor;
import xyz.andric.stepper.message.Message;
import xyz.andric.stepper.message.MessageType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends WebSocketServer {

    private final static Logger logger = LogManager.getLogger(Main.class);

    private Set<WebSocket> conns;

    private HashMap<String, Object> motorData;

    private Sensor s;

    Timer timer = null;

    private long startTime = 0;
    private long endTime;
    private int rpm = 0;

    public Main(int port) {
        super(new InetSocketAddress(port));
        conns = new HashSet<>();

        motorData = new HashMap<>();
        motorData.put("speed", String.valueOf(50));
        motorData.put("isMotorStarted", "false");
        motorData.put("direction", -1);

        s = new Sensor();
        s.getSensor().addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                if (event.getState().isHigh() && startTime > 0) {
                    endTime = System.currentTimeMillis();
                    rpm = Math.round(60000 / Math.toIntExact(endTime - startTime));
                    startTime = endTime;
                }
            }
        });
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        conns.add(webSocket);

        logger.info("Connection established from: " + webSocket.getRemoteSocketAddress().getHostString());
        // System.out.println("New connection from " +
        // webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        conns.remove(conn);
        // When connection is closed, remove the user.

        logger.info("Connection closed to: " + conn.getRemoteSocketAddress().getHostString());
        // System.out.println("Closed connection to " +
        // conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Message msg = mapper.readValue(message, Message.class);
            switch (msg.getType()) {
            case DATA_CHANGED:
                HashMap<String, Object> newMotorData = mapper.readValue(msg.getData(), HashMap.class);
                if (!Boolean.TRUE.equals(motorData.get("isMotorStarted"))
                        && Boolean.TRUE.equals(newMotorData.get("isMotorStarted"))) {
                    startTime = System.currentTimeMillis();

                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {
                            // do your work
                            Message m = new Message();
                            m.setType(MessageType.RPM);
                            m.setData(String.valueOf(rpm));
                            broadcastMessage(m);
                        }
                    }, 0, 1 * 1000);
                    // System.out.println(startTime);
                }
                motorData = newMotorData;

                broadcastMessage(msg);

                StepMotor.setSpeed(102 - (Integer) motorData.get("speed"));

                if (Boolean.TRUE.equals(motorData.get("isMotorStarted"))) {
                    if ((Integer) motorData.get("direction") == 1) {
                        // System.out.println("BACKWARD");
                        StepMotor.backward();
                    } else {
                        // System.out.println("FORWARD");
                        StepMotor.forward();
                    }
                } else {
                    // System.out.println("STOP");
                    StepMotor.stop();
                    startTime = 0;
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                }

            case USER_JOIN:
                Message newMessage = new Message();
                newMessage.setType(MessageType.DATA_CHANGED);
                newMessage.setData(mapper.writeValueAsString(motorData));
                broadcastMessage(newMessage);
            }

            // System.out.println("Message text: " + msg.getData() + ", type:" +
            // msg.getType());
            // logger.info("Message text: " + msg.getData());
        } catch (IOException e) {
            logger.error("Wrong message format.");
            // return error message to user
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

        if (conn != null) {
            conns.remove(conn);
        }
        assert conn != null;
        System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        System.out.println(ex);
    }

    private void broadcastMessage(Message msg) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String messageJson = mapper.writeValueAsString(msg);
            for (WebSocket sock : conns) {
                sock.send(messageJson);
            }
        } catch (JsonProcessingException e) {
            logger.error("Cannot convert message to json.");
        }
    }

//    public static void main(String[] args) {
//        int port;
//        try {
//            port = Integer.parseInt(System.getenv("PORT"));
//        } catch (NumberFormatException nfe) {
//            port = 9000;
//        }
//        new Main(port).start();
//    }

}
