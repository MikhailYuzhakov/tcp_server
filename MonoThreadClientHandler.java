import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.Locale;
import java.sql.*;
import java.lang.reflect.InvocationTargetException;

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
            Database db = new Database();
            

            while (!clientDialog.isClosed()) {
                db.InsertData(db.parseDate(in.readUTF()));
                System.out.print("\nServer reading from " + clientDialog.getRemoteSocketAddress() + "> ");
            }

            in.close();
            out.close();
            clientDialog.close();
            System.out.println("Client disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

/**
 * Database operation
 */
class Database {
	public void InsertData(JSONObject data) throws InstantiationException, IllegalAccessException, IllegalArgumentException,                 InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
        String url = "jdbc:mysql://95.163.235.4:6033/cd2db?serverTimezone=Europe/Moscow&useSSL=false";
        String username = "root";
        String password = "passwordDB1998/";
        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        try (Connection conn = DriverManager.getConnection(url, username, password);) {
            Statement statement = conn.createStatement();
            String cmd = String.format(Locale.ROOT, "INSERT climatic_data(probe_id, timestamp, temp_rtc, atemp, press_bmp, temp_htu, hum_htu, stemp, rssi, vacc, spc, charge) VALUES (%d, \"%s\", %.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %d, %.2f, %.2f, %d)",
                            Integer.parseInt((String) (data.get("probe_id"))), 
                            data.get("datetime").toString(), 
                            Float.parseFloat((String) data.get("temp_rtc")), 
                            Float.parseFloat((String) data.get("atemp")), 
                            Float.parseFloat((String) data.get("press_bmp")),
                            Float.parseFloat((String) data.get("temp_htu")), 
                            Float.parseFloat((String) data.get("hum_htu")), 
                            Float.parseFloat((String) data.get("stemp")), 
                            Integer.parseInt((String) (data.get("rssi"))),
                            Float.parseFloat((String) data.get("vacc")),
                            Float.parseFloat((String) data.get("spc")),
                            Integer.parseInt((String) (data.get("charge"))));
            System.out.println(cmd);
            statement.executeUpdate(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public JSONObject parseDate(String data) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(data);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}