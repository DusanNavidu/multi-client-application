package lk.ijse.gdse72.multiclientserverappliction;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerController {
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(4000)) {
                System.out.println("Server started...");

                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected...");

                    ClientHandler client = new ClientHandler(socket);
                    clients.add(client);
                    new Thread(client).start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final DataInputStream input;
        private final DataOutputStream output;
        private String username;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(socket.getOutputStream());
        }

        public void sendText(String message) throws IOException {
            output.writeUTF("TEXT");
            output.writeUTF(message);
            output.flush();
        }

        @Override
        public void run() {
            try {
                this.username = input.readUTF();
                System.out.println(username + " has joined the chat...");
                broadcastText(username + " has joined the chat...", this);

                while (true) {
                    String type = input.readUTF();

                    if (type.equals("TEXT")) {
                        String message = input.readUTF();
                        System.out.println(username + ": " + message);
                        broadcastText(username + ": " + message, this);
                    }
                }

            } catch (IOException e) {
                System.out.println(username + " disconnected.");
            } finally {
                clients.remove(this);
                try {
                    socket.close();
                    input.close();
                    output.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                broadcastText(  username + " has left the chat.", this);
            }
        }

        private void broadcastText(String message, ClientHandler sender) {
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    if (client != sender) {
                        try {
                            client.sendText(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}