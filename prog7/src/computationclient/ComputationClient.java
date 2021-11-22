/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package computationclient;

/**
 *
 * @author Golde
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.net.*;
import java.io.*;

public class ComputationClient {

    final static int BUF_SIZE = 1024;

    public static void main(String[] args) {
        String hostName;
        int portNumber;

        // Check command line arguments for host name and port number
        if (args.length != 2) {
            hostName = "localhost";
            portNumber = 23656;
        } else {
            hostName = args[0];
            portNumber = Integer.parseInt(args[1]);
        }

        try (DatagramSocket socket = new DatagramSocket()) {
            // Creating client socket and getting input / output streams
            System.out.println("Client connect to " + hostName + " " + portNumber + "\n");
            System.out.print("Input an expression: ");
            Scanner scanner = new Scanner(System.in);

            //recieving the input for the server to run from the client
            String input = scanner.nextLine();
            // loop to send and recieve data fromt he server
            while (!(input.equals("quit")) && !input.trim().isEmpty()) {
                // send packet
                DatagramPacket packet = new DatagramPacket(input.getBytes(), input.length(), InetAddress.getByName(hostName), portNumber);
                socket.send(packet);
                
                byte[] buf = new byte[BUF_SIZE];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                DataInputStream fromServer = new DataInputStream(new ByteArrayInputStream(packet.getData()));
                boolean success = fromServer.readBoolean();
                // check if the function was valid and didn't break any rules
                if (success) {
                    // only reading if it could calculate a result
                    int response = fromServer.readInt();
                    System.out.println("Computation from server: " + response);
                } else {
                    System.out.println("An error occurred!");
                }
                fromServer.close();
                // asking for the next expression if there is one
                System.out.print("Input an expression: ");
                input = scanner.nextLine();
            }

            System.out.println("Closing connection!");
        } // Exception thrown for unknown host
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } // Exception thrown for incorrect port numbers
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to "
                    + hostName);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
