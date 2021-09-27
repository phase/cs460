import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class CollatzClient {

    public static void main(String[] args) {
        String hostName;
        int portNumber;

        // Check command line arguments for host name and port number
        if (args.length != 2) {
            hostName = "localhost";
            portNumber = 23657;
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
            Scanner scanner = new Scanner(System.in);

            System.out.print("Input a number: ");
            int input = Integer.parseInt(scanner.nextLine());
            toServer.writeInt(input);
            int response = fromServer.readInt();
            System.out.println("Collatz Conjecture took " + response + " steps!\n");

            toServer.close();
            fromServer.close();
            serverSocket.close();
        }
        // Exception thrown for unknown host
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        }

        // Exception thrown for incorrect port numbers
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}
