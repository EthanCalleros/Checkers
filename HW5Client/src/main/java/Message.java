import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    public String sender;
    public String messageText;
    public ArrayList<String> recipients;
    public int type;

    public Message() {
        recipients = new ArrayList<>();
    }
}
