package client;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {

    public static void main(String args[]) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Server IP (default 127.0.0.1): ");
        String ip = scanner.nextLine().trim();
        if (ip.isEmpty()) {
            ip = "127.0.0.1";
        }

        System.out.print("Port (default 8889): ");
        String stringPort = scanner.nextLine().trim();
        int port;
        if (stringPort.isEmpty()) {
            port = 8889;
        } else {
            port = Integer.parseInt(stringPort);
        }
        System.out.print("Room number: ");
        String roomNumber = scanner.nextLine().trim();

        System.out.print("Your name: ");
        String username = scanner.nextLine().trim();

        Socket socket = new Socket(ip, port);
        System.out.println("Connected to server!");

        PrintWriter writer = new PrintWriter(
            new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
            socket.getInputStream(), "UTF-8"));

        writer.println("JOIN:" + roomNumber + ":" + username);
        System.out.println("Joined room [" + roomNumber + "], start chatting!\n");

        Thread receiver = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String msg;
                    while ((msg = reader.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server");
                }
            }
        });
        receiver.start();

        while (true) {
            String input = scanner.nextLine();
            if (input.equals("/quit")) {
                break;
            }
            writer.println("MSG:" + input);
        }

        socket.close();
        System.out.println("Goodbye!");

    }

}
