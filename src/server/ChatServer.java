import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ChatServer {

    private static final ConcurrentHashMap<String,ConcurrentHashMap<String,ClientHandler>> rooms//第一次输入房间号  第二次输入名字
        = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        int port = 8889;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port: " + port);

            while(true) {
                Socket socket = serverSocket.accept();

                ClientHandler handler = new ClientHandler(socket);
                Thread t = new Thread(handler);
                t.start();
            }   
        }catch(IOException e) {
            System.out.println("Start failed: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter writer;
        private String roomNumber;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(),"UTF-8"));

                writer = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(),"UTF-8") , true);
                
                String firstLine = reader.readLine();
                String parts[] = firstLine.split(":",3); // JOIN:666:小明

                this.roomNumber = parts[1];
                this.username = parts[2];

                rooms.putIfAbsent(this.roomNumber,new ConcurrentHashMap<>());
                rooms.get(this.roomNumber).put(this.username,this); // add self to room

                broadcast("[" + this.username + "] joined the room");
                System.out.println("[" + this.username + "]" + " joined room " + "[" + this.roomNumber + "]");
                
                String line;
                while((line = reader.readLine()) != null) {
                    if(line.startsWith("MSG:")) {
                        String content = line.substring(4);
                        broadcast(this.username + ": " + content);
                    }
                }

            }catch(IOException e) {
                System.out.println(this.username + " disconnected");
            }finally {
                if(this.username != null && this.roomNumber != null) {
                    rooms.get(this.roomNumber).remove(this.username);
                    broadcast("[" + this.username + "] left the room");
                    if(rooms.get(this.roomNumber).isEmpty()) {
                        rooms.remove(this.roomNumber);
                    }
                }
                close();
            }
        }

        private void broadcast(String msg) {
            ConcurrentHashMap<String,ClientHandler> room = rooms.get(this.roomNumber);
            if(room != null) {
                for(ClientHandler c : room.values()) {
                    c.writer.println(msg);
                }
            }
        }

        private void close() {
            try {
                socket.close();
            }catch(Exception e) {
                System.out.println("Error " + e.getMessage());
            }
        }
    }
}