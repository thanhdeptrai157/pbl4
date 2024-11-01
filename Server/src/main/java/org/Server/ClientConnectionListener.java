package org.Server;
//bên ui overide lại hàm này để biết lúc nào 1 client kết nối tới
public interface ClientConnectionListener {
    void onClientConnected(String clientIP);
}
