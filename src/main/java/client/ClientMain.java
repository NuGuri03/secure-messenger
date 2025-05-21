package client;

import client.ui.LoginUI;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ClientMain {
    public static void main(String[] args) {


        try {
            ChatClient chatClient = new ChatClient();
            chatClient.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        new LoginUI();
    }
}