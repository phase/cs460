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

        System.out.println("Collatz server started");

        while (true) {
            System.out.println("Waiting for connections on port #" + port);

            executorService.submit(new CollatzThread(serverSocket.accept()));
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
