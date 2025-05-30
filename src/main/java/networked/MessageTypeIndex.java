package networked;

import java.util.ArrayList;
import java.util.List;

import networked.messages.*;

public class MessageTypeIndex {
    public static List<Class<?>> getAllMessageTypes() {
        var messages = new ArrayList<Class<?>>();

        messages.add(byte[].class);
        messages.add(EncryptedMessage.class);
        messages.add(KeyExchangeMessage.class);

        messages.add(SessionHelloMessage.class);
        messages.add(RegisterRequest.class);
        messages.add(RegisterResponse.class);
        messages.add(LoginRequest.class);
        messages.add(LoginResponse.class);

        return messages;
    }
}
