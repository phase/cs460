/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package computationserver;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Golde
 */
public class ComputationServer {
    final int BUF_SIZE = 1024;
    // declaring and initializing the variables for creating the socket
    private static DatagramSocket serverSocket;
    private static int port;
    Socket clientSocket = null;
    
    // constructor function to set the port and socket port 
    public ComputationServer(int port) {
        try {
            ComputationServer.port = port;
            ComputationServer.serverSocket = new DatagramSocket(port);
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
            byte[] buf = new byte[BUF_SIZE];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            serverSocket.receive(packet);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(BUF_SIZE);
            DataOutputStream toClient = new DataOutputStream(byteStream);
            String returnable = new String(packet.getData(), 0, packet.getLength());
            // creating a character array to store the data recieved from the client
            char[] instructions = returnable.toCharArray();
            // creating a buffer for the character, integer for the first value, the operation character, and the sqrt flag
            String buffer = "";
            Integer a = null;
            Character operation = null;
            boolean foundSqrt = false;
            // for loop to pars through the character string created from the data recieved
            for (int i = 0; i < instructions.length; i++) {
                // creating an iterator variable for the current character 
                char ins = instructions[i];
                // if the current character is a number or the start of a negative number add it to the buffer
                if ((ins <= '9' && ins >= '0') || (buffer.isEmpty() && ins == '-')) {
                    buffer += ins;
                }
                // if the current character is equal to one of the operands then save it and the numbers that came before
                else if (ins == '-'
                        || ins == '+'
                        || ins == '*'
                        || ins == '/'
                        || ins == '^') {
                    a = Integer.parseInt(buffer);
                    buffer = "";
                    operation = ins;
                } 
                // otherwise it is checking for sqrt and updating the boolean variable
                else if ("sqrt of".indexOf(ins) == i) {
                    foundSqrt = true;
                }
            }
            // try block in order to perform the acutal operation
            try {
                // parsing the second number in character array
                Integer b = Integer.parseInt(buffer);
                // creating a variable to hold the answer
                int answer = 0;
                // checking for the operation and performing the math
                if (operation != null) {
                    if (operation == '+') {
                        answer = a + b;
                    } else if (operation == '-') {
                        answer = a - b;
                    } else if (operation == '*') {
                        answer = a * b;
                    } else if (operation == '/') {
                        answer = a / b;
                    } else if (operation == '^') {
                        answer = (int) Math.pow(a, b);
                    }
                } else if (foundSqrt) {
                    if (b < 0) {
                        throw new Exception("negative sqrt");
                    }
                    answer = (int) Math.floor(Math.sqrt(b));
                }
                // write a boolean of the result and the answer
                toClient.writeBoolean(true);
                toClient.writeInt(answer);
                System.out.println("A solution of " + answer + " was sent to the client");
            } catch (Exception e1) {
                toClient.writeBoolean(false);
            }

            toClient.flush();
            byte[] output = byteStream.toByteArray();
            DatagramPacket packetToSend = new DatagramPacket(output, output.length, packet.getAddress(), packet.getPort());
            serverSocket.send(packetToSend);
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
