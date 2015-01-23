package cs455.overlay;

/**
 * Created by Cullen on 1/22/2015.
 */
public class EventFactory {

    private static EventFactory singleton = null;

    public static EventFactory get() {
        if (singleton == null) {
            singleton = new EventFactory();
        }
        return singleton;
    }
}
