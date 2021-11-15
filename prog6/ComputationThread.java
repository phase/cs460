/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package computationserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Golde
 */
public class ComputationThread implements Runnable {

    // clientSocket initialization and EchoThread constructor
    Socket clientSocket;

    public ComputationThread(Socket theClientSocket) {
        clientSocket = theClientSocket;
    }

    public void run() {

        try (
                // accept a connection
                // create a thread to deal with the client   
                 DataOutputStream toClient = new DataOutputStream(clientSocket.getOutputStream());  DataInputStream fromClient = new DataInputStream(clientSocket.getInputStream())) {
            System.out.println("Connection with client established");

            // Initialize char and String variables for use in reading client input
            boolean keepGoing = true;

            // Loop reads client input one character at a time and echoes it to client
            // Loop continues until user inputs "quit" sequence
            while (keepGoing) {
                try {
                    // creating a variable to store the data recieved from the client
                    String returnable = fromClient.readUTF();
                    System.out.println("Expression Recieved! Calculating...");
                    
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
                } catch (Exception e) {
                    System.err.println("Client has closed the connection!");
                    keepGoing = false;
                }
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + 23656 + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
