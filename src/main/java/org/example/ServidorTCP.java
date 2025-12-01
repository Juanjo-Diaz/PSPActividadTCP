package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ServidorTCP {
    static final int Puerto = 2000;

    public static void main(String[] args) {
        AtomicInteger contadorClientes = new AtomicInteger(1);
        try (ServerSocket socketServidor = new ServerSocket(Puerto)) {
            System.out.println("[SERVIDOR] Servidor TCP del Ahorcado escuchando en puerto " + Puerto + " (Ctrl+C para detener) ...");
            while (true) {
                try {
                    Socket skCliente = socketServidor.accept();
                    int id = contadorClientes.getAndIncrement();
                    String nombreHilo = "Cliente-" + id;
                    System.out.println("[SERVIDOR] Conexión aceptada de " + skCliente.getInetAddress().getHostAddress() + ":" + skCliente.getPort() + " -> " + nombreHilo);
                    // Lanzamos un hilo por cliente
                    HiloSocket hilo = new HiloSocket(skCliente, nombreHilo);
                    hilo.start();
                } catch (IOException e) {
                    System.err.println("[SERVIDOR] Error aceptando cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error en el servidor: " + e.getMessage());
        }
    }
}