package task2b;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class ShopServer {

    public static void main(String[] args) throws IOException {
        Path cithPath = Paths.get("");
        File cith = cithPath.toFile();
        String currentuser = "";

        int port = 3000; // default port
        String directorypath = "shoppingcart"; // default directory
        if (args.length == 2) {
            directorypath = args[0];
            port = Integer.parseInt(args[1]);
        }

        System.out.println("Starting " + directorypath + " Server on Port " + port);
        ServerSocket server = new ServerSocket(port);
        System.out.println("Using " + directorypath + " directory for persistence");

        while (true) {

            Socket sock = server.accept();

            try {

                while (true) {
                    boolean stop = false;
                    System.out.println("Connection Received...");
                    OutputStream os = sock.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(os);
                    InputStream is = sock.getInputStream();
                    DataInputStream dis = new DataInputStream(is);
                    currentuser = dis.readUTF(); // get the user request
                    List<String> cart = new LinkedList<>();

                    dos.writeUTF("Connected to " + directorypath + " server at localserver on " + currentuser + " port "
                            + port); // send the connection status to client once connected

                    cithPath = Paths.get("./" + directorypath + "/" + currentuser + ".cart");
                    cith = cithPath.toFile();
                    if (!cith.exists()) {
                        System.out.println(currentuser + " shopping cart loaded (New)");
                        dos.writeUTF(currentuser + " shopping cart loaded on Server (New)");
                    } else {
                        FileReader fr = new FileReader(cith);
                        BufferedReader br = new BufferedReader(fr);
                        String userdata = br.readLine(); // assume only the 1st line consist the data...

                        if (userdata.equals("")) {
                            System.out.println(currentuser + " shopping cart loaded (Empty)");
                            dos.writeUTF(currentuser + " shopping cart loaded on Server (Empty)");
                        } else {
                            String[] userdataarray = userdata.replaceAll("\\[|\\]|\\s", "").trim().split(",");
                            for (int i = 0; i < userdataarray.length; i++) {
                                cart.add(userdataarray[i]);
                            }
                            System.out.println(currentuser + " shopping cart loaded");
                            dos.writeUTF(currentuser + " shopping cart loaded on Server");

                        }

                        br.close();
                        fr.close();

                    }

                    // Do Something
                    while (!stop) {
                        String userinput = dis.readUTF();
                        String itemadded = "";

                        switch (userinput) {
                            case Constant.EXIT:
                                stop = true;
                                break;

                            case Constant.ADD:
                                String inputcommand = dis.readUTF();
                                String[] inputarray = inputcommand.trim().split(" ");
                                dos.writeInt(inputarray.length - 1); // total line to return.
                                for (int i = 1; i < inputarray.length; i++) {
                                    String itemtoadd = inputarray[i].replace(",", "");
                                    itemadded = "";
                                    if (cart.contains(itemtoadd)) {
                                        System.out.println("You have " + itemtoadd + " in your cart");
                                        dos.writeUTF("You have " + itemtoadd + " in your cart");
                                    } else {
                                        cart.add(itemtoadd);
                                        System.out.println(itemtoadd + " added to the cart");
                                        dos.writeUTF(itemtoadd + " added to the cart");
                                    }

                                }
                                break;

                            case Constant.LIST:
                                dos.writeInt(cart.size());
                                if (cart.size() == 0) {
                                    dos.writeUTF("Your cart is empty");
                                } else {
                                    for (int i = 0; i < cart.size(); i++) {
                                        dos.writeUTF(i + 1 + ". " + cart.get(i));
                                    }
                                }

                                System.out.println("Cart Listing Requested and Returned!");
                                break;

                            case Constant.DELETE:
                                int userdelindex = Integer.parseInt(dis.readUTF());
                                if (userdelindex > cart.size()) {
                                    System.out.println("incorrect item index");
                                    dos.writeUTF("incorrect item index");
                                } else {
                                    int deleteindex = userdelindex - 1;
                                    String itemtodelete = cart.get(deleteindex);
                                    cart.remove(deleteindex);
                                    System.out.println(itemtodelete + " removed from the cart");
                                    dos.writeUTF(itemtodelete + " removed from the cart");
                                }
                                break;

                            case Constant.SAVE:
                                FileWriter wr = new FileWriter(cith);
                                wr.write(cart.toString());
                                wr.flush();
                                wr.close();
                                dos.writeUTF("cart content saved to " + currentuser);
                                System.out.println(currentuser + " Shopping List Updated");
                                break;
                            default:
                                System.out.println("Invalid Command");
                                break;

                        }

                    }

                    System.out.println("Terminating client connection");
                    try {
                        sock.close();
                        System.out.println("Terminated");
                        break;
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());

                    }

                }

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

        }

    }

}
