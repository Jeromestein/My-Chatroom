import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Chat Room Client
 */
public class ChatRoomClient {
    // define chatroom's server socket
    private Socket clientSocket;

    public ChatRoomClient() {
        try {
            // create a client and link to server
            clientSocket = new Socket("localhost", 12345);
            System.out.println("Client Running:" + clientSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clientStart() {
        // send msg
        new ClientSendThread().start();
        // receive msg
        new ClientReceiveThread().start();
    }

    /**
     * the thread of sending msg
     */
    private class ClientSendThread extends Thread {
        @Override
        public void run() {
            try {
                System.out.println("Broadcast Format [msg]");
                System.out.println("Direct Msg Format[port-msg]");
                // get user's input
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String msg = scanner.next();
                    // sending msg to server
                    BufferedWriter writer = null;
                    writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "utf-8"));
                    writer.write(msg + "\n");
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * the thread of receiving msg
     */
    private class ClientReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    // receive msg from server
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream(), "utf-8"));
                    System.out.println(reader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new ChatRoomClient().clientStart();
    }
}
