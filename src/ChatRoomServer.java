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
    // 时间转换的格式
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 定义一个聊天室的服务器
    private ServerSocket serverSocket;
    // 定义一个容器存放所有的客户端
    private List<Socket> clients;

    /**
     * 定义一个构造器，初始化一些信息
     */
    public ChatRoomServer() {
        try {
            // 创建一个服务器
            serverSocket = new ServerSocket(12345);
            // 提示信息：服务器已经启动了
            System.out.println("聊天室服务器[" + serverSocket.getLocalPort() + "]启动成功...");
            // 定义存放客户端的容器
            clients = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务器开始工作，并接受客户端的请求
     */
    public void serverStart() throws IOException {
        // 等待客户端的访问
        while (true) {
            Socket socket = serverSocket.accept();
            // 加入到客户列表
            clients.add(socket);
            // 通知其他用户有新用户进入直播间
            serverSendAll("用户[" + socket.getPort() + "]进入聊天室");
            // 开始启动消息推送线程
            new MsgThread(socket).start();
        }
    }

    /**
     * 向所有的客户端发送消息
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
     * 消息线程
     */
    class MsgThread extends Thread {

        // 当前客户端
        private Socket socket;

        public MsgThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // 开始接受客户端的信息
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
                    final String msg = reader.readLine();
                    // 判断这个消息是群聊还是私聊
                    if (msg.contains("-")) {
                        // 私聊
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
                        // 拼接消息
                        String newMsg = "客户端[" + socket.getPort() + "]时间[" + format.format(new Date()) + "]:" + msg;
                        // 群聊-开始向所有客户端发送信息
                        serverSendAll(newMsg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // 启动一个聊天室的服务器
        new ChatRoomServer().serverStart();
    }
}
