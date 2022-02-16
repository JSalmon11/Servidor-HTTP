package HTTP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTP {

    public static void main(String[] args) {
        ServerSocket ss = getServerSocket();

        while (ss.isBound() && !ss.isClosed()) {
            //Se aceptan comunicaciones y se crean hilos para los clientes
            Socket s = aceptarComs(ss);
            Thread hilo = new Thread(new ServerThread(s));
            hilo.start();
        }
        cerrarCanales(ss);
    }// main()

    private static ServerSocket getServerSocket() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(8081);
        } catch (IOException e) {
            System.out.println(e);
        }
        return ss;
    }// getServerSocket()

    private static Socket aceptarComs(ServerSocket ss) {
        Socket socket = null;
        try {
            socket = ss.accept();
        } catch (IOException e) {
            System.out.println(e);
        }
        return socket;
    }// aceptarComs()

    private static void cerrarCanales(ServerSocket ss) {
        try {
            ss.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }// cerrarCanales()

}// HTTP
