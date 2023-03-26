import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class tcp_client {

    public static void main(String[] args) throws InterruptedException {
        HashMap<String, Object> obj = new HashMap<>();
        ClimaticSensor cSensor = new ClimaticSensor();

        try (Socket socket = new Socket("95.163.235.4", 3345);
            DataOutputStream oos = new DataOutputStream(socket.getOutputStream());)
        {
            System.out.println("Client connected to " + socket.getRemoteSocketAddress());     
            while (true) {
                obj.put("datetime", cSensor.readDateTime());
                obj.put("temp_rtc", BigDecimal.valueOf(cSensor.readRTCAirTemp()).setScale(2, RoundingMode.HALF_UP));
                obj.put("atemp", BigDecimal.valueOf(cSensor.readBmpAirTemp()).setScale(2, RoundingMode.HALF_UP));
                obj.put("press_bmp", BigDecimal.valueOf(cSensor.readBMPAtmPressure()).setScale(1, RoundingMode.HALF_UP));
                obj.put("temp_htu", BigDecimal.valueOf(cSensor.readHTUAirTemp()).setScale(2, RoundingMode.HALF_UP));
                obj.put("hum_htu", cSensor.readAirHum());
                obj.put("stemp", BigDecimal.valueOf(cSensor.readSoilTemp()).setScale(2, RoundingMode.HALF_UP));
                obj.put("rssi", cSensor.readRSSI());
                Double vcc = cSensor.readVCC();
                obj.put("vacc", BigDecimal.valueOf(vcc).setScale(2, RoundingMode.HALF_UP));
                obj.put("spc", BigDecimal.valueOf(cSensor.readSPC()).setScale(2, RoundingMode.HALF_UP));
                obj.put("charge", cSensor.calcilateChargeLevel(vcc));
                oos.writeUTF(obj.toString());  
                oos.flush();
                TimeUnit.SECONDS.sleep(10);  
            }       
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  
    }
}

class ClimaticSensor {
    Random rnd = new Random();

    public ClimaticSensor() {
        super();
    }

    public Double readBmpAirTemp() {
        return rnd.nextDouble(-10, 10);
    }

    public Double readBMPAtmPressure() {
        return rnd.nextDouble(740, 760);
    }

    public Double readHTUAirTemp() {
        return rnd.nextDouble(-10, 10);
    }

    public Integer readAirHum() {
        return rnd.nextInt(20, 80);
    }

    public Double readSoilTemp() {
        return rnd.nextDouble(0, 5);
    }

    public Double readRTCAirTemp() {
        return rnd.nextDouble(-10, 10);
    }

    public Integer readRSSI() {
        return rnd.nextInt(-130, -100);
    }
    
    public Double readVCC() {
        return rnd.nextDouble(3.6, 4.2);
    }

    public Double readSPC() {
        return rnd.nextDouble(10, 200);
    }

    public Integer calcilateChargeLevel(Double vcc) { 
        Integer charge = (int)(vcc / 4.2 * 100);
        return charge;
    }

    //datetime': '2023-03-07 07:27:00
    public String readDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

}