package org.example;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor TCP que escucha en el puerto 2000. Se quedará esperando las peticiones de 3 clientes y le enviará un mensaje con el número de cliente
 * @author alumno
 */
public class ServidorTCP {

    static final int Puerto = 2000;

    /**
     * Método principal de la clase
     * @param args sin argumentos
     */
    public static void main(String[] args) {
        try {
            ServerSocket skServidor = new ServerSocket(Puerto);
            System.out.println("Escucho el puerto " + Puerto);
            for (int nCli = 0; nCli < 3; nCli++) {
                Socket sCliente = skServidor.accept();
                System.out.println("Sirvo al cliente " + nCli);
                OutputStream aux = sCliente.getOutputStream();
                DataOutputStream flujo_salida = new DataOutputStream(aux);
                flujo_salida.writeUTF("Hola cliente " + nCli);
                sCliente.close();
            }
            System.out.println("Ya se han atendido los 3 clientes");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
