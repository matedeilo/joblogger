import com.sun.xml.internal.ws.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.logging.Level.INFO;

public class NewJobLogger {

    private static Map<Character, Loggerr> map;

    static {
        map = new HashMap<>();
    }

    public static Loggerr console(Map params){
        if (!map.containsKey('C')) {
            map.put('C', new ConsoleLogger());
        }
        return get('C');
    }

    public static Loggerr file(Map params) throws Exception {
        if (!map.containsKey('F')) {
            map.put('F', new FileLogger(params));
        }
        return get('F');
    }

    public static Loggerr database(Map params) throws Exception {
        if (!map.containsKey('D')) {
            map.put('D', new DatabaseLogger(params));
        }
        return get('D');
    }

    private static Loggerr get(Character c){
        return map.get(c);
    }

    static class ConsoleLogger implements Loggerr {
        Logger logger = Logger.getLogger("MyLog");

        public ConsoleLogger() {
            logger.addHandler(new ConsoleHandler());
        }

        @Override
        public void log(String message, Level level){
            logger.log(INFO, message);
        }
    }

    static class FileLogger implements Loggerr {
        private Logger logger = Logger.getLogger("MyLog");
        private static final String paramFileFolder = "logFileFolder";

        public FileLogger(Map dbParams) throws Exception {
            if (dbParams.containsKey(paramFileFolder)) {
                File logFile = new File(dbParams.get(paramFileFolder) + "/logFile.txt");
                if (!logFile.exists()) {
                    logFile.createNewFile();
                }

                FileHandler fh = new FileHandler(dbParams.get(paramFileFolder) + "/logFile.txt");
                logger.addHandler(fh);
            } else {
                throw new Exception();
            }
        }

        @Override
        public void log(String message, Level level){
            logger.log(INFO, message);
        }
    }

    static class DatabaseLogger implements Loggerr {

        private Connection connection;
        Set<String> requiredKeys = new HashSet<>(Arrays.asList("userName", "password", "dbms", "serverName", "portNumber"));

        public DatabaseLogger (Map dbParams) throws Exception {
            boolean notValid = requiredKeys.stream().map(key -> dbParams.containsKey(key)).anyMatch(e -> !e);
            if (notValid) {
                throw new Exception();
            }

            Properties connectionProps = new Properties();
            connectionProps.put("user", dbParams.get("userName"));
            connectionProps.put("password", dbParams.get("password"));

            connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName")
                    + ":" + dbParams.get("portNumber") + "/", connectionProps);
        }

        @Override
        public void log(String message, Level level){
            Statement stmt = getStatement();
            try {
                stmt.executeUpdate("insert into Log_Values('" + message + "', " + String.valueOf(getT(level)) + ")");
            } catch (SQLException e) {
                throw new RuntimeException("Error on query execution");
            }
        }

        private Statement getStatement() throws RuntimeException {
            try {
                return connection.createStatement();
            } catch (SQLException sqle) {
                throw new RuntimeException("Error on db connection");
            }
        }

        private int getT(Level level){
            switch (level.getName()) {
                case "INFO": return 1;
                case "SEVERE": return 2;
                case "WARNING": return 3;
                default: return 0;
            }
        }
    }

}
