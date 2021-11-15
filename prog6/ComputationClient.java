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
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ComputationClient {

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

        try {
            // Creating client socket and getting input / output streams
            Socket serverSocket = new Socket(hostName, portNumber);
            DataOutputStream toServer = new DataOutputStream(serverSocket.getOutputStream());
            DataInputStream fromServer = new DataInputStream(serverSocket.getInputStream());
            System.out.println("Client connect to " + hostName + " " + portNumber + "\n");
            System.out.print("Input an expression: ");
            Scanner scanner = new Scanner(System.in);

            //recieving the input for the server to run from the client
            String input = scanner.nextLine();
            // loop to send and recieve data fromt he server
            while (input != null && !input.trim().isEmpty()) {
                toServer.writeUTF(input);
                boolean success = fromServer.readBoolean();
                // check if the function was valid and didn't break any rules
                if (success) {
                    // only reading if it could calculate a result
                    int response = fromServer.readInt();
                    System.out.println("Computation from server: " + response);
                } else {
                    System.out.println("An error occurred!");
                }
                // asking for the next expression if there is one
                System.out.print("Input an expression: ");
                input = scanner.nextLine();
            }

            System.out.println("Closing connection!");
            //closing all the connections once it has completed
            toServer.close();
            fromServer.close();
            serverSocket.close();
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
