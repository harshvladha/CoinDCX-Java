package com.harshvladha.coindcx.publicapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Log4j2
public class CoinTicker {
    private static final String WS_URI = "https://stream.coindcx.com";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // Websocket
    private Socket ws;


    // Subscription and message mgmt
    private Set<String> subscribedChannels;
    private Consumer<List<CoinTick>> tickListener;

    public CoinTicker() {
        IO.Options options = new IO.Options();
        // low-level engine options
        options.transports = new String[] { WebSocket.NAME };
        options.upgrade = true;
        options.rememberUpgrade = false;
        options.reconnection = true;
        options.reconnectionAttempts = Integer.MAX_VALUE;
        options.reconnectionDelay = 1000;
        options.reconnectionDelayMax = 5_000;
        options.timeout = 50_0000;

        ws = IO.socket(URI.create(WS_URI), options); // the main namespace

        setOnConnect();
        setOnDisconnect();
        setOnError();
        setOnMessage();

        subscribedChannels = new HashSet<>();
    }

    public void connect() {
        ws.connect();
    }
    private void setOnError() {
        ws.on(Socket.EVENT_ERROR, objects -> {
            log.error("CoinDCX Socket : Error while event");
        });

        ws.on(Socket.EVENT_RECONNECT_ERROR, objects -> {
            log.error("CoinDCX Socket : Error while reconnecting");
        });
        ws.on(Socket.EVENT_CONNECT_ERROR, objects -> {
            log.error("CoinDCX Socket : Error while connecting");
        });
    }

    private void setOnMessage() {
        ws.on("new-trade", objects -> {
            ArrayList<CoinTick> coinTicks = new ArrayList<>();
            for (Object object : objects) {
                try {
                    coinTicks.add(objectMapper.readValue(((JSONObject)object).getString("data"), CoinTick.class));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    log.error(String.format("CoinDCX Socket : Exception while tick deserialization %s", object));
                }
            }
            if(!coinTicks.isEmpty()) {
                tickListener.accept(coinTicks);
            }
        });
    }

    private void setOnDisconnect() {
        ws.on(Socket.EVENT_DISCONNECT, objects -> {
            log.error("CoinDCX Socket : Got disconnected.");
        });
    }

    private void setOnConnect() {
        ws.on(Socket.EVENT_CONNECT, objects -> {
            subscribe(subscribedChannels);
        });

        ws.on(Socket.EVENT_RECONNECT_FAILED, objects -> {
            log.error("CoinDCX Socket : Reconnect failed.");
        });
    }

    void setTickListener(Consumer<List<CoinTick>> tickListener) {
        this.tickListener = tickListener;
    }

    void subscribe(Set<String> channels) {
        subscribedChannels.addAll(channels);
        for (String channel : channels) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("channelName", channel);
            ws.emit("join", jsonObject);
        }
    }

    void unsubscribe(List<String> channels) {
        subscribedChannels.addAll(channels);
        for (String channel : channels) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("channelName", channel);
            ws.emit("leave", jsonObject);
        }
    }
}


