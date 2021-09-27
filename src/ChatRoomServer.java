import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Chat Room Server
 */
public class ChatRoomServer {
    // define date format
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // define chatroom's server socket
    private ServerSocket serverSocket;
    // define a list to store all the clients' info
    private List<Socket> clients;

    /**
     * constructor, initialization
     */
    public ChatRoomServer() {
        try {
            // create a server
            serverSocket = new ServerSocket(12345);
            System.out.println("Chatroom Server[" + serverSocket.getLocalPort() + "] started successfully...");
            // initialize the client list
            clients = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * server starts to run, and accept clients' requests
     */
    public void serverStart() throws IOException {
        // waiting for clients
        while (true) {
            Socket socket = serverSocket.accept();
            // add it to client list
            clients.add(socket);
            // broadcast to all about the entrance of new client
            serverSendAll("Client[" + socket.getPort() + "] in");
            // start the msg thread
            new MsgThread(socket).start();
        }
    }

    /**
     * broadcast msg to all clients
     *
     * @param msg
     */
    public void serverSendAll(String msg) {
        try {
            for (Socket client : clients) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "utf-8"));
                writer.write(msg + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * the thread of msg
     */
    class MsgThread extends Thread {
        // current clients
        private Socket socket;

        public MsgThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // processing client's msg
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
                    final String msg = reader.readLine();
                    // check if direct msg or broadcast
                    if (msg.contains("-")) {
                        // direct msg
                        clients.stream().filter((c) -> c.getPort() == Integer.parseInt(msg.split("-")[0]))
                                .forEach((s) -> {
                                    try {
                                        BufferedWriter writer = new BufferedWriter(
                                                new OutputStreamWriter(s.getOutputStream(), "utf-8"));
                                        writer.write(msg + "\n");
                                        writer.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                    } else {
                        // boradcast
                        String newMsg = "Server[" + socket.getPort() + "]Time[" + format.format(new Date()) + "]:"
                                + msg;
                        serverSendAll(newMsg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // start a chatroom server
        new ChatRoomServer().serverStart();
    }
}
