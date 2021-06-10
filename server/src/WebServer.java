
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class WebServer {

    private static final int port = 5000;
    ArrayList clientOutputStreams;

    public static void main(String[] args) {
        new WebServer().go();
    }

    public void go() {
        System.out.println("Hello Web Server \n");
        System.out.println("Port is " + port);

        clientOutputStreams = new ArrayList();

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while(true) {
                Socket clientSocket = serverSocket.accept();

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                ClientHandler clientHandlerThreadJob = new ClientHandler(clientSocket);
                Thread t = new Thread(clientHandlerThreadJob);
                t.start();
            }
        } catch (IOException ex) { ex.printStackTrace();}
    }

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket socket;


        public ClientHandler(Socket clientSocket) {
            try {
                this.socket = clientSocket;

                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(inputStreamReader);
            } catch (IOException ex) { ex.printStackTrace(); }
        }

        public void run() {
            String message;
            try {
                while((message = reader.readLine()) != null) {
                    System.out.println("읽은 채팅: " + message);
                    send(message);
                }
            } catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    public void send(String message) {
        Iterator iterator = clientOutputStreams.iterator();
        while(iterator.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) iterator.next();
                writer.println(message);
                writer.flush();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

}
