import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CollatzServer {

    private static ServerSocket serverSocket;
    private static int port;
    Socket clientSocket = null;
    ExecutorService executorService = Executors.newFixedThreadPool(4);

    public CollatzServer(int port) {
        try {
            CollatzServer.port = port;
            CollatzServer.serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(CollatzServer.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Error starting server on port " + port);
            System.exit(1);
        }

        CollatzServer.port = port;
    }

    public void runServerLoop() throws IOException {

        System.out.println("Echo server started");

        while (true) {
            System.out.println("Waiting for connections on port #" + port);

            executorService.submit(new CollatzThread(serverSocket.accept()));
        }
    }

    public void handleClient(Socket clientSocket) {

        DataInputStream fromClient = null;
        DataOutputStream toClient = null;

        int numFromClient = 0;
        int state = 0;
        boolean keepGoing = true;

        // show that we are connected to client
        System.out.println("Client connected ...");

        // first get the streams
        try {
            fromClient = new DataInputStream(clientSocket.getInputStream());
            toClient = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error opening network streams");
            return;
        }

        // now talk to the client
        while (keepGoing) {
            if (clientSocket.isClosed()) {
                break;
            }
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

        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket to client");
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

    public static void main(String args[]) throws Exception {
        // create instance of echo server
        // note that hardcoding the port is bad, here we do it just for simplicity reasons
        CollatzServer echoServer = new CollatzServer(23657);

        // fire up server loop
        echoServer.runServerLoop();
    }
}
