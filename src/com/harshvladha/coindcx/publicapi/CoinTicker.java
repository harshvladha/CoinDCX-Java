package com.harshvladha.coindcx.publicapi;

import com.neovisionaries.ws.client.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class CoinTicker {
    private static final String WS_URI = "wss://stream.coindcx.com/socket.io/?transport=websocket";

    // Listeners
    private OnTicks onTickerArrivalListener;
    private OnConnect onConnectedListener;
    private OnDisconnect onDisconnectedListener;
    private OnError onErrorListener;
    private OnOrderUpdate orderUpdateListener;

    // Websocket
    private WebSocket ws;

    // Connection mgmt
    int count = 0;
    private int maxRetries = 10;
    private Timer timer = null;
    private boolean tryReconnection = false;
    private final int pingInterval = 2500;
    private final int pongCheckInterval = 2500;
    private int nextReconnectInterval = 0;
    private int maxRetryInterval = 30000;
    private long lastPongAt = 0L;
    private Timer canReconnectTimer = null;
    private boolean canReconnect = true;

    // Subscription and message mgmt
    private List<String> subscribedChannels;

    public CoinTicker() {
        try {
            ws = new WebSocketFactory().createSocket(WS_URI);
        } catch (IOException e) {
            if(onErrorListener != null) {
                onErrorListener.onError(e);
            }
            e.printStackTrace();
            return;
        }
        ws.addListener(getWebsocketAdapter());
        subscribedChannels = new ArrayList<>();
    }

    void subscribe(List<String> channels) {
        subscribedChannels.addAll(channels);
        for (String channel : channels) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("channelName", channel);
            ws.sendText(jsonObject.toString());
        }
    }

    /** Returns a WebSocketAdapter to listen to ticker related events.*/
    public WebSocketAdapter getWebsocketAdapter(){
        return new WebSocketAdapter() {

            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                System.out.println("We are connected");
                count = 0;
                nextReconnectInterval = 0;

                if (onConnectedListener != null) {
                    onConnectedListener.onConnected();
                }

                if (tryReconnection) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer();
                    timer.scheduleAtFixedRate(getTask(), 0, pongCheckInterval);

                }
            }

            @Override
            public void onTextMessage(WebSocket websocket, String message) {
                parseTextMessage(message);
            }

            @Override
            public void onBinaryMessage(WebSocket websocket, byte[] binary) {
                System.out.println("Got binary message");
                try {
                    super.onBinaryMessage(websocket, binary);
                } catch (Exception e) {
                    e.printStackTrace();
                    if(onErrorListener != null) {
                        onErrorListener.onError(e);
                    }
                }
//                ArrayList<Tick> tickerData = parseBinary(binary);

//                if (onTickerArrivalListener != null) {
//                    onTickerArrivalListener.onTicks(tickerData);
//                }
            }

            @Override
            public void onPongFrame(WebSocket websocket, WebSocketFrame frame) {
                System.out.println("Got pong frame");
                try {
                    super.onPongFrame(websocket, frame);
                    Date date = new Date();
                    lastPongAt = date.getTime();
                } catch (Exception e) {
                    e.printStackTrace();
                    if(onErrorListener != null) {
                        onErrorListener.onError(e);
                    }
                }
            }

            /**
             * On disconnection, return statement ensures that the thread ends.
             *
             * @param websocket
             * @param serverCloseFrame
             * @param clientCloseFrame
             * @param closedByServer
             * @throws Exception
             */
            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
                System.out.println("Got disconnected");
                if (onDisconnectedListener != null) {
                    onDisconnectedListener.onDisconnected();
                }
                return;
            }

            @Override
            public void onError(WebSocket websocket, WebSocketException cause) {
                System.out.println("Got error.");
                try {
                    super.onError(websocket, cause);
                } catch (Exception e) {
                    e.printStackTrace();
                    if(onErrorListener != null) {
                        onErrorListener.onError(e);
                    }
                }
            }

        };
    }

    private TimerTask getTask() {
        TimerTask checkForRestartTask = new TimerTask() {
            public void run() {
                if (lastPongAt != 0L) {
                    Date currentDate = new Date();
                    long timeInterval = currentDate.getTime() - lastPongAt;
                    if (timeInterval >= 5000L) {
                        doReconnect();
                    }

                }
            }
        };
        return checkForRestartTask;
    }

    /** Establishes a web socket connection.
     * */
    public void connect() {
        try {
            ws.setPingInterval(pingInterval);
            ws.connect();
        } catch (WebSocketException e){
            e.printStackTrace();
            if(onErrorListener != null) {
                onErrorListener.onError(e);
            }
            if(tryReconnection) {
                if (timer == null) {
                    // this is to handle reconnection first time
                    if (lastPongAt == 0) {
                        lastPongAt = 1;
                    }
                    timer = new Timer();
                    timer.scheduleAtFixedRate(getTask(), 0, pongCheckInterval);
                }
            }
        }
    }

    public void doReconnect() {
        if (this.tryReconnection) {
            if (this.nextReconnectInterval == 0) {
                this.nextReconnectInterval = (int)(2000.0D * Math.pow(2.0D, (double)this.count));
            } else {
                this.nextReconnectInterval = (int)((double)this.nextReconnectInterval * Math.pow(2.0D, (double)this.count));
            }

            if (this.nextReconnectInterval > this.maxRetryInterval) {
                this.nextReconnectInterval = this.maxRetryInterval;
            }

            if (this.count <= this.maxRetries) {
                if (this.canReconnect) {
                    ++this.count;
                    this.canReconnect = false;
                    this.canReconnectTimer = new Timer();
                    this.canReconnectTimer.schedule(new TimerTask() {
                        public void run() {
                            canReconnect = true;
                        }
                    }, this.nextReconnectInterval);
                }
            } else if (this.count > this.maxRetries && this.timer != null) {
                this.timer.cancel();
                this.timer = null;
            }

        }
    }

    /** Parses incoming text message.*/
    private void parseTextMessage(String message) {
        System.out.println("Got a message");
        System.out.println(message);
        JSONObject data;
        try {
            data = new JSONObject(message);
            if(!data.has("type")){
                return;
            }

            String type = data.getString("type");
            if(type.equals("order")) {
                if(orderUpdateListener != null) {
                    System.out.println("Got order update.");
//                    orderUpdateListener.onOrderUpdate(getOrder(data));
                }
            }

            if(type.equals("error")) {
                if(onErrorListener != null) {
                    onErrorListener.onError(data.getString("data"));
                }
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnConnect {
        void onConnected();
    }

    public interface OnDisconnect {
        void onDisconnected();
    }

    public interface OnError {
        void onError(Exception var1);


        void onError(String var1);
    }

    public interface OnOrderUpdate {
//        void onOrderUpdate(Order var1);
    }

    public interface OnTicks {
//        void onTicks(ArrayList<Tick> var1);
    }


}


