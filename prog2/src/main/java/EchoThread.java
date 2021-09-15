import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class EchoThread implements Runnable {
    // clientSocket initialization and EchoThread constructor
    Socket clientSocket;

    public EchoThread(Socket theClientSocket) {
        clientSocket = theClientSocket;
    }

    public void run() {

        try (
                // accept a connection
                // create a thread to deal with the client
                DataOutputStream toClient = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream fromClient = new DataInputStream(clientSocket.getInputStream());
        ) {
            System.out.println("Connection with client established");

            // Initialize char and String variables for use in reading client input
            char charFromClient;
            boolean keepGoing = true;

            // Loop reads client input one character at a time and echoes it to client
            // Loop continues until user inputs "quit" sequence
            while (keepGoing) {
                try {
                    charFromClient = (char) fromClient.readByte();
                    System.out.print(charFromClient);
                } catch (IOException e) {
                    System.err.println("Error reading character from client");
                    return;
                }

                try {
                    toClient.writeByte(charFromClient);
                } catch (IOException e) {
                    System.err.println("Error writing character to client");
                    return;
                }

                if (charFromClient == 'q') {
                    keepGoing = false;
                }
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + 23657 + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}