/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jee.engineio.sample;

import io.socket.emitter.Emitter;
import io.socket.engineio.parser.Packet;
import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.EngineIoSocket;
import java.io.IOException;
import java.io.PrintWriter;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jeevanantham
 */
@WebServlet(name = "EngineIoServlet", urlPatterns = {"/*"}, asyncSupported = true)
public class EngineIoServlet extends HttpServlet {

    @EJB
    ServerStore serverStore;

    private final EngineIoServer mEngineIoServer = new EngineIoServer();

    @PostConstruct
    public void initPC() {
        System.out.println("inside PostConstruct");
        if (serverStore.getmEngineIoServer() == null) {
            serverStore.setmEngineIoServer(mEngineIoServer);
            System.out.println("mEngineIoServer?? " + mEngineIoServer);
            mEngineIoServer.on("connection", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    EngineIoSocket socket = (EngineIoSocket) args[0];
                    // Do something with socket like store it somewhere
                    System.out.println("socket :: " + socket);
                    socket.on("packet", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Packet packet = (Packet) args[0];
                            // Do something with packet.
                            System.out.println("packet :: " + packet.data);
                        }
                    });
                    socket.on("message", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Object message = args[0];
                            // message can be either String or byte[]
                            // Do something with message.
                            System.out.println("message ::" + message);
//                            
                        }
                    });
                    socket.send(new Packet<>(Packet.MESSAGE, "foo"));
                }
            });
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        mEngineIoServer.handleRequest(request, response);
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet appning</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet appning at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
