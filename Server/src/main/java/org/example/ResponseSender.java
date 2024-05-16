package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@AllArgsConstructor
public class ResponseSender {
    private final Socket clientSocket;
    public void sendHttpResponse(byte[] httpResponsePacket) {
        try {
            DataOutputStream dateOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dateOutputStream.writeInt(httpResponsePacket.length);
            dateOutputStream.write(httpResponsePacket);
            dateOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
