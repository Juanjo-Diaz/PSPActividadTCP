package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClienteTCP {

    static final String HOST = "localhost";
    static final int Puerto = 2000;

    public static void main(String[] args) {
        try (Socket sCliente = new Socket(HOST, Puerto);
             DataInputStream in = new DataInputStream(sCliente.getInputStream());
             DataOutputStream out = new DataOutputStream(sCliente.getOutputStream());
             Scanner sc = new Scanner(System.in)) {

            String estadoActual = "";
            int fallos = 0;
            boolean jugando = true;
            while (jugando) {

                String msg = in.readUTF();
                if (msg == null) break;
                String[] partes = msg.split(":", 4);
                String tipo = partes[0];
                switch (tipo) {
                    case "START": {
                        estadoActual = partes.length > 1 ? partes[1] : "";
                        fallos = partes.length > 2 ? parseIntSafe(partes[2]) : 0;
                        System.out.println("Palabra: " + estadoActual + " | Fallos: " + fallos + "/3");
                        break;
                    }
                    case "STATE": {
                        estadoActual = partes.length > 1 ? partes[1] : estadoActual;
                        fallos = partes.length > 2 ? parseIntSafe(partes[2]) : fallos;
                        String info = partes.length > 3 ? partes[3] : "";
                        if (!info.isEmpty()) System.out.println(info);
                        System.out.println("Palabra: " + estadoActual + " | Fallos: " + fallos + "/3");
                        break;
                    }
                    case "WIN": {
                        String palabra = partes.length > 1 ? partes[1] : "";
                        fallos = partes.length > 2 ? parseIntSafe(partes[2]) : fallos;
                        System.out.println("¡Enhorabuena! Has acertado la palabra: " + palabra + " | Fallos: " + fallos);
                        jugando = false;
                        break;
                    }
                    case "LOSE": {
                        String palabra = partes.length > 1 ? partes[1] : "";
                        fallos = partes.length > 2 ? parseIntSafe(partes[2]) : fallos;
                        System.out.println("Has perdido. La palabra era: " + palabra + " | Fallos: " + fallos);
                        jugando = false;
                        break;
                    }
                }
                if(jugando){System.out.print("Introduce una letra (solo 1 carácter): ");}
                String entrada = sc.nextLine();
                // Enviar tal cual; el servidor validará si es 1 carácter
                out.writeUTF(entrada);
            }
        } catch (Exception e) {
            System.err.println("[CLIENTE] Error: " + e.getMessage());
        }
    }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
}