package client;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

import javax.swing.JOptionPane;
import crypto.CryptoUtil;

public class ServerInfo {
    private static final String CONFIG_FILE_NAME = "server_info.txt";
    private static final int DEFAULT_PORT = 23456;

    private String serverAddress;
    private int serverTcpPort;
    private PublicKey serverPublicKey;

    private ServerInfo(String serverAddress, PublicKey serverPublicKey) throws IllegalArgumentException {
        URI uri = URI.create(serverAddress);

        this.serverTcpPort = uri.getPort();
        this.serverTcpPort = (this.serverTcpPort == -1) ? DEFAULT_PORT : this.serverTcpPort;

        // uri.getHost() returns null on localhost
        this.serverAddress = serverAddress.replaceAll(".+://", "").replaceAll(":.+", "");
        this.serverPublicKey = serverPublicKey;

        System.out.println("Server Address: " + serverAddress);

        if (this.serverAddress == null || this.serverAddress.isEmpty()) {
            showErrorDialog("서버 주소가 올바르지 않습니다.");
            throw new IllegalArgumentException("Invalid server address");
        }
    }


    public static ServerInfo readConfiguration() throws RuntimeException {
        Path filePath = getConfigFilePath();

        try {
            String[] lines = Files.readString(filePath).split("\n");
            String serverAddress = lines[0];
            lines[0] = "";

            StringReader publicKeyReader = new StringReader(String.join("\n", lines));
            PublicKey serverPublicKey = CryptoUtil.loadRSAPublicKey(publicKeyReader);

            return new ServerInfo(serverAddress, serverPublicKey);

        } catch (IOException e) {
            showErrorDialog("server_info.txt를 읽어들이지 못했습니다.");
            throw new RuntimeException(e);

        } catch (GeneralSecurityException e) {
            showErrorDialog("server_info.txt의 서버 공개키가 올바르지 않습니다.");
            throw new RuntimeException(e);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAddress() {
        return serverAddress;
    }

    public PublicKey getPublicKey() {
        return serverPublicKey;
    }

    public int getTcpPort() {
        return serverTcpPort;
    }

    public static Path getConfigFilePath() {
        return Paths.get(System.getProperty("user.dir"), CONFIG_FILE_NAME);
    }


    private static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "오류", JOptionPane.ERROR_MESSAGE);
    }
}
