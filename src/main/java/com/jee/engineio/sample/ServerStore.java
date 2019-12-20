/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jee.engineio.sample;

import io.socket.engineio.server.EngineIoServer;
import java.io.Serializable;
import javax.ejb.Singleton;

/**
 *
 * @author jeevanantham
 */
@Singleton
public class ServerStore implements Serializable {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    EngineIoServer mEngineIoServer = null;

    public EngineIoServer getmEngineIoServer() {
        return mEngineIoServer;
    }

    public void setmEngineIoServer(EngineIoServer mEngineIoServer) {
        this.mEngineIoServer = mEngineIoServer;
    }

}
