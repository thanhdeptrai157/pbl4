package org.Server.HistoryWeb;


import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class HistoryWeb extends WebSocketServer {

    public HistoryWeb(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Mở kết nối WebSocket với: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Kết nối bị đóng với: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Nhận được tin nhắn: " + message);

        // Xử lý dữ liệu JSON (URL đã được gửi từ trình duyệt)
        System.out.println("URL đã truy cập: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket Server đã bắt đầu.");
    }

    public static void main(String[] args) {
        HistoryWeb server = new HistoryWeb(5000);
        server.start();
        System.out.println("WebSocket server đang chạy trên cổng 5000...");
    }
}
