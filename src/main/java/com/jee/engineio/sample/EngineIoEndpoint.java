/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jee.engineio.sample;

import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.EngineIoWebSocket;
import io.socket.parseqs.ParseQS;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

/**
 *
 * @author jeevanantham
 */
public final class EngineIoEndpoint extends Endpoint {

    private Session mSession;
    private Map<String, String> mQuery;
    private EngineIoWebSocket mEngineIoWebSocket;

    ServerStore serverStore;

    private EngineIoServer mEngineIoServer;// The engine.io server instance

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        try {
            System.out.println("inside onOpen");
            mSession = session;
            mQuery = ParseQS.decode(session.getQueryString());

            if (mEngineIoServer == null) {
                serverStore = (ServerStore) new InitialContext().lookup("java:global/engine.io/ServerStore!com.jee.engineio.sample.ServerStore");
                mEngineIoServer = serverStore.getmEngineIoServer();
            }

            mEngineIoWebSocket = new EngineIoWebSocketImpl();

            /*
         * These cannot be converted to lambda because of runtime type inference
         * by server.
             */
            mSession.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    mEngineIoWebSocket.emit("message", message);
                }
            });
            mSession.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                @Override
                public void onMessage(byte[] message) {
                    mEngineIoWebSocket.emit("message", (Object) message);
                }
            });

            mEngineIoServer.handleWebSocket(mEngineIoWebSocket);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("End of onOpen code block");
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);

        mEngineIoWebSocket.emit("close");
        mSession = null;
    }

    @Override
    public void onError(Session session, Throwable thr) {
        super.onError(session, thr);
        System.out.println("inside onError block...............");
        thr.printStackTrace();
        mEngineIoWebSocket.emit("error", "unknown error", thr.getMessage());
    }

    private class EngineIoWebSocketImpl extends EngineIoWebSocket {

        private RemoteEndpoint.Basic mBasic;

        EngineIoWebSocketImpl() {
            mBasic = mSession.getBasicRemote();
        }

        @Override
        public Map<String, String> getQuery() {
            return mQuery;
        }

        @Override
        public void write(String message) throws IOException {
            mBasic.sendText(message);
        }

        @Override
        public void write(byte[] message) throws IOException {
            mBasic.sendBinary(ByteBuffer.wrap(message));
        }

        @Override
        public void close() {
            try {
                mSession.close();
            } catch (IOException ignore) {
            }
        }
    }
}
