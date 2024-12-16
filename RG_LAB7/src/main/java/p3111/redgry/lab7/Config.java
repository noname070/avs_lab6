package p3111.redgry.lab7;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Getter
@Log4j2
public class Config {
    public static final String DB_HOST = "lab7-pg-db";
    public static final String DB_TABLE = "studs";
    public static final String DB_NETWORK = "lab7-network"; // app network
    public static final String DB_PG_URL = "jdbc:postgresql://%s:5432/%s".formatted(DB_HOST, DB_TABLE);
    public static final String DB_ROOT_USER = "lab7server";
    public static final String DB_ROOT_PASSWORD = "supertoppassword337";

    public static final Integer APP_PORT = 3292;
    public static final String APP_HOST = "lab7-server";
    // static {
    //     try {
    //         APP_HOST = InetAddress.getByName("lab7-server").getHostAddress();
    //         System.out.println("App host: " + APP_HOST);
    //     } catch (UnknownHostException e) {
    //         log.error("Ошибка с определением хоста", e);
    //         System.exit(1);
    //     }
    // }

}
