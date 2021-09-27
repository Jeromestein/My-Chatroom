import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class ChatRoomClient {
    // 客户端对象
    private Socket socket;

    public ChatRoomClient() {
        try {
            // 创建客户端并和服务器创建连接
            socket = new Socket("localhost", 12345);
            System.out.println("客户端启动：" + socket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clientStart() {
        // 发送消息
        new ClientSendThread().start();
        // 接受消息
        new ClientReceiveThread().start();
    }

    /**
     * 专门负责发送消息
     */
    private class ClientSendThread extends Thread {
        @Override
        public void run() {
            try {
                System.out.println("群聊格式[直接发言]");
                System.out.println("私聊格式[port-聊天内容]");
                // 开始获取用户的输入
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String msg = scanner.next();
                    // 开始向服务器发送消息
                    BufferedWriter writer = null;
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
                    writer.write(msg + "\n");
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 专门负责接受消息
     */
    private class ClientReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    // 接收服务器发送的消息
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
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
