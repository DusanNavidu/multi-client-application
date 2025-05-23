package lk.ijse.gdse72.multiclientserverappliction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ClientController {
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 4000);
                System.out.println("Connected to Server");

                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                Scanner scanner = new Scanner(System.in);

                System.out.print("Enter your username: ");
                String username = scanner.nextLine();
                output.writeUTF(username);

                Thread readThread = new Thread(() -> {
                    try {
                        while (true) {
                            String type = input.readUTF();
                            if (type.equals("TEXT")) {
                                String message = input.readUTF();
                                System.out.println(message);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Connection closed.");
                    }
                });
                readThread.start();

                while (true) {
                    String msg = scanner.nextLine();

                    if (msg.equalsIgnoreCase("BYE")) {
                        break;
                    }

                    if (msg.equalsIgnoreCase("TIME")) {
                        LocalTime time = LocalTime.now();
                        String formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                        System.out.println("Local Time: " + formattedTime);
                        continue;
                    }

                    if (msg.equalsIgnoreCase("DATE")) {
                        LocalDate date = LocalDate.now();
                        System.out.println("Local Date: " + date);
                        continue;
                    }

                    output.writeUTF("TEXT");
                    output.writeUTF(msg);
                    output.flush();
                }

                socket.close();
                scanner.close();
                System.out.println("Disconnected from Server.");

            } catch (Exception e) {
                System.out.println("Client Error: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}
