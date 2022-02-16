package HTTP;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ServerThread implements Runnable {

    private Socket s;

    public ServerThread(Socket s) {
        this.s = s;
    }// ServerThread()

    @Override
    public void run() {
        BufferedReader br = getBufferedReader(s);
        OutputStream os = getOutputStream(s);

        //Recibe la cabecera del cliente
        String mensaje = recibirPeticion(br);
        //Mensaje se queda solo con la primera
        //linea de la cabecera, la peticion
        while (mensaje != null) {
        	//Con men se lee el resto de la cabecera
        	//para completar la comunicacion
            String men = recibirPeticion(br);
            while (!men.equals("")) {
                men = recibirPeticion(br);
            }
            //Devuelve la respuesta adecuada
            //para enviar al cliente y la envia
            byte[] respuesta = componerRespuesta(mensaje);
            enviarRespuesta(os, respuesta);
            mensaje = recibirPeticion(br);
        }
        //Cuabdo el cliente se desconecta, el hilo lee un null
        //como mensaje del cliente, sale del bucle y muere
        cerrarCanales(br, os);
    }// run()

    private static byte[] componerRespuesta(String m) {
    	//Construye la cabecera del mensaje
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        String finLinea = "\r\n";
        String cabecera = "HTTP/1.1 200 OK" + finLinea;

        //Obtiene el nombre archivo solicitado y lo carga
        String archivo = m.split("/")[1].split(" ")[0];
        String ext = archivo.split("\\.")[1];
        File f = new File("./site/" + archivo);

        String preExt = "text";
        if (ext.equals("png") || ext.equals("ico")) {
            preExt = "image";
        }
        cabecera += "Date: " + dateFormat.format(new Date())
                + finLinea + "Content-Type: " + preExt + "/" + ext + finLinea;
        //Transforma el archivo solicitado en bytes, para poder enviarlo al cliente
        byte[] archivoToSend = archivoToBytes(f);
        int tamanio = archivoToSend.length;
        cabecera += "Content-Length: " + tamanio + finLinea + finLinea;
        //Crea el byteBuffer que llevara el mensaje para enviarRespuesta
        //al cliente, con la longitud necesaria para la cabecera y el contenido
        ByteBuffer respuesta = ByteBuffer.allocate(cabecera.getBytes().length + archivoToSend.length + finLinea.getBytes().length);
        respuesta.put(cabecera.getBytes());

        respuesta.put(archivoToSend);
        respuesta.put(finLinea.getBytes());
        return respuesta.array();
    }// componerMensaje()

    private static BufferedReader getBufferedReader(Socket socket) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e);
        }
        return br;
    }// getPeticion()

    private static String recibirPeticion(BufferedReader br) {
        String mensaje = null;
        try {
            mensaje = br.readLine();
        } catch (IOException e) {
            System.out.println(e);
        }
        return mensaje;
    }// recibirMensaje()

    private static OutputStream getOutputStream(Socket socket) {
        OutputStream out = null;
        try {
            out = socket.getOutputStream();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return out;
    }// getObjetInputStream()

    private static byte[] archivoToBytes(File f) {
        byte[] r = null;
        try {
            r = Files.readAllBytes(f.toPath());
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return r;
    }// archivoToBytes()

    private static void enviarRespuesta(OutputStream os, byte[] r) {
        try {
            os.write(r);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }// enviarRespuesta()

    private static void cerrarCanales(BufferedReader br, OutputStream os) {
        try {
            br.close();
            os.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }// cerrarCanales()

}// ServerThread
