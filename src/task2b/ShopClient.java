package task2b;

import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ShopClient {

    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.out.println("Please indicate the user and directory...");
            System.exit(1);
        }

        String[] connectarray = args[0].split("\\@|\\:");
        String host = connectarray[1];
        int port = Integer.parseInt(connectarray[2]);
        String currentuser = connectarray[0];
        // boolean stop = false;
        System.out.printf("Connecting to server %s on port %d\n", host, port);
        Socket sock = new Socket(host, port);
        Console cons = System.console();
        List<String> cart = new LinkedList<>();
        Boolean Stop = false;

        // Output and input stream
        OutputStream os = sock.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
        InputStream is = sock.getInputStream();
        DataInputStream dis = new DataInputStream(is);

        // send the username that to be loaded
        dos.writeUTF(currentuser);

        // get Server Status
        String connectstatus = dis.readUTF();
        System.out.println(connectstatus);

        // get the data from the server
        String dataloadingstatus = dis.readUTF();
        System.out.println(dataloadingstatus);

        // Do Something;
        while (!Stop) {
            String inputcommand = cons.readLine(">");
            String[] inputarray = inputcommand.trim().split(" ");

            switch (inputarray[0]) {

                case Constant.EXIT:
                    dos.writeUTF("exit");
                    currentuser = "";
                    cart.clear();
                    Stop = true;
                    break;

                case Constant.ADD:
                    dos.writeUTF("add");
                    dos.writeUTF(inputcommand);
                    int totalreturn = dis.readInt();
                    for (int i = 0; i < totalreturn; i++) {
                        System.out.println(dis.readUTF());
                    }
                    break;

                case Constant.DELETE:
                    dos.writeUTF("delete");
                    dos.writeUTF(inputarray[1]);
                    System.out.println(dis.readUTF());
                    break;

                case Constant.LIST:
                    dos.writeUTF("list");
                    int totallist = dis.readInt();
                    if (totallist == 0) {
                        System.out.println(dis.readUTF());
                    } else {
                        for (int i = 0; i < totallist; i++) {
                            System.out.println(dis.readUTF());
                        }
                    }

                    break;

                case Constant.SAVE:
                    dos.writeUTF("save");
                    System.out.println(dis.readUTF());
                    break;

                default:
                    System.out.println("Invalid Command , Please try again...");
                    break;

            }
            ;

        }

        System.out.println("Closing connection");
        try {
            sock.close();
            System.out.println("Terminated");

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

}
