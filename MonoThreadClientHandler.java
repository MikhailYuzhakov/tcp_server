import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MonoThreadClientHandler implements Runnable {

    private static Socket clientDialog;

    public MonoThreadClientHandler(Socket client) {
        MonoThreadClientHandler.clientDialog = client;
    }

    @Override
    public void run() {

        try {
            DataOutputStream out = new DataOutputStream(clientDialog.getOutputStream());
            DataInputStream in = new DataInputStream(clientDialog.getInputStream());

            while (!clientDialog.isClosed()) {
                String entry = in.readUTF();
                System.out.print("\nServer reading from " + clientDialog.getRemoteSocketAddress() + "> " + entry);
            }

            in.close();
            out.close();
            clientDialog.close();
            System.out.println("Client disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}