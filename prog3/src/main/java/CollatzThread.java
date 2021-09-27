import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CollatzThread implements Runnable {
    // clientSocket initialization and EchoThread constructor
    Socket clientSocket;

    public CollatzThread(Socket theClientSocket) {
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
            boolean keepGoing = true;

            // Loop reads client input one character at a time and echoes it to client
            // Loop continues until user inputs "quit" sequence
            while (keepGoing) {
                if (clientSocket.isClosed()) {
                    break;
                }
                int numFromClient;
                try {
                    numFromClient = fromClient.readInt();
                    System.out.println("Num from client: " + numFromClient);
                } catch (IOException e) {
                    System.err.println("Warning reading character from client");
                    return;
                }

                int steps = countCollatz(numFromClient);

                try {
                    toClient.writeInt(steps);
                } catch (IOException e) {
                    System.err.println("Error writing character to client");
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + 23657 + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }


    public int countCollatz(int a) {
        int count = 0;
        while (a != 1) {
            a = collatz(a);
            count++;
        }
        return count;
    }

    public int collatz(int a) {
        if (a % 2 == 0) {
            return a / 2;
        } else {
            return a * 3 + 1;
        }
    }
}
