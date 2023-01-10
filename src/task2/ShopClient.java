package task2;

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
        String userdata = dis.readUTF().replaceAll("\\[|\\]|\\s", "");
        if (userdata.equals("")) {
            cart.clear();
            System.out.println(currentuser + " shopping cart loaded (Empty Cart)");
        } else {
            String[] userdataarray = userdata.trim().split(",");
            for (int i = 0; i < userdataarray.length; i++) {
                cart.add(userdataarray[i]);
            }
            System.out.println(currentuser + " shopping cart loaded");

        }



        // Do Something;
        while (true) {
            String inputcommand = cons.readLine(">");
            String[] inputarray = inputcommand.trim().split(" ");

            if (currentuser.equals("")) {
                System.out.println("Please load a user cart (eg. load fred)");

            } else if (inputarray.length == 1 & inputarray[0].equals("list")) {
                if (cart.size() == 0) {
                    System.out.println("Your cart is empty");
                } else {
                    for (int i = 0; i < cart.size(); i++) {
                        System.out.printf("%d. %s \n", i + 1, cart.get(i));
                    }
                }

            } else if (inputarray.length >= 2 && inputarray[0].equals("add")) {

                for (int i = 1; i < inputarray.length; i++) {
                    String itemtoadd = inputarray[i].replace(",", "");
                    if (cart.contains(itemtoadd)) {
                        System.out.println("You have " + itemtoadd + " in your cart");
                    } else {
                        cart.add(itemtoadd);
                        System.out.println(itemtoadd + " added to the cart");
                    }

                }

            } else if (inputarray.length == 2 && inputarray[0].equals("delete")) {

                if (Integer.parseInt(inputarray[1]) > cart.size()) {
                    System.out.println("inccorrect item index");
                } else {
                    int deleteindex = Integer.parseInt(inputarray[1]) - 1;
                    String itemtodelete = cart.get(deleteindex);
                    cart.remove(deleteindex);
                    System.out.println(itemtodelete + " removed from the cart");
                }
            } else if (inputarray.length == 1 && inputarray[0].equals("save")) {
                dos.writeUTF(cart.toString()); // send the newdata to server for saving
                System.out.println(dis.readUTF()); // return the server status

            } else if (inputarray.length == 1 && inputarray[0].equals("exit")) {
                dos.writeUTF("exit");
                currentuser = "";
                cart.clear();
                break;

            } else {
                System.out.println("Invalid Command , Please try again...");
            }

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
