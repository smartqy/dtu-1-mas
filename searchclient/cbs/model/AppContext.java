package searchclient.cbs.model;

public class AppContext {
    public static Environment env;

    public static void init(Environment e) {
        env = e;
    }

    public static Environment getEnv() {
        return env;
    }
}
