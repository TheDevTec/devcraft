package world.deni.fang;

import java.io.*;
import java.util.Properties;

public class Config {

    Fang fang = new Fang();

    public String getDefault(String prop){
        String returnVal = "null";
        switch (prop){
            case "ip":
                returnVal = "0.0.0.0";
                break;
            case "port":
                returnVal = "25565";
                break;
            case "whitelist":
                returnVal = "false";
                break;
            case "opLevel":
                returnVal = "4";
                break;
            case "onlineMode":
                returnVal = "true";
                break;
            case "renderDistance":
                returnVal = "8";
                break;
            case "entityDistance":
                returnVal = "8";
                break;
            case "useFlatGenerator":
                returnVal = "true";
                break;
        }
        return returnVal;
    }

    private void write(String prop, String value){
        Properties properties = new Properties();

        try(OutputStream outputStream = new FileOutputStream(fang.CONFIG_LOCATION + "/server.properties")){
            properties.setProperty(prop, value);
            properties.store(outputStream, null);
        } catch (IOException e) {
            System.out.println("Exception while writing config");
            e.printStackTrace();
        }
    }

    private String read(String prop){
        Properties properties = new Properties();

        try {
            InputStream in = new FileInputStream(fang.CONFIG_LOCATION + "/server.properties");
            properties.load(in);
        } catch (IOException e) {
            System.out.println("Exception while reading config");
            e.printStackTrace();
        }
        //Getting the value from  our properties file.
        return properties.getProperty(prop).trim();
    }
}
