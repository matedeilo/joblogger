import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;
//import java.util.logging.Logger;

public interface Loggerr {

    void log(String message, Level level);

    default void message(String message){
        this.log("message " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + message, Level.INFO);
    }

    default void error(String message) {
        this.log("error " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + message, Level.SEVERE);
    }

    default void warning(String message){
        this.log("warning " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + message, Level.WARNING);
    }
}
