package Application;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        System.out.println(org.mindrot.jbcrypt.BCrypt.hashpw("123456", org.mindrot.jbcrypt.BCrypt.gensalt(12)));
        Application.launch(Loader.class, args);
    }
}
