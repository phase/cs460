/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package computationserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Golde
 */
public class ComputationServer {
    
    // declaring and initializing the variables for creating the socket
    private static ServerSocket serverSocket;
    private static int port;
    Socket clientSocket = null;
    
    // constructor function to set the port and socket port 
    public ComputationServer(int port) {
        try {
            ComputationServer.port = port;
            ComputationServer.serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(ComputationServer.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Error starting server on port " + port);
            System.exit(1);
        }

        ComputationServer.port = port;
    }

    // function to run the server and create new threads when connections are revieved
    public void runServerLoop() throws IOException {

        System.out.println("Computation server started");

        while (true) {
            System.out.println("Waiting for connections on port #" + port);

            new Thread(new ComputationThread(serverSocket.accept())).start();
        }
    }

    public static void main(String args[]) throws Exception {
        // create instance of computation server
        // note that hardcoding the port is bad, here we do it just for simplicity reasons
        ComputationServer compServer = new ComputationServer(23656);

        // fire up server loop
        compServer.runServerLoop();
    }
    
}
