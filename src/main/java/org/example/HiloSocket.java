package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class HiloSocket extends Thread{
    private final Socket skCliente;

    public HiloSocket(Socket sCliente, String nombre) {
        super(nombre);
        this.skCliente = sCliente;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(skCliente.getInputStream());
             DataOutputStream out = new DataOutputStream(skCliente.getOutputStream())) {

            // Preparar palabra aleatoria por cliente
            String[] palabras = {"java", "socket", "concurrencia", "hilo", "servidor", "cliente", "ahorcado", "maven", "puerto", "protocolo"};
            String palabra = palabras[new Random().nextInt(palabras.length)].toLowerCase();
            char[] estado = new char[palabra.length()];
            Arrays.fill(estado, '_');
            Set<Character> usadas = new HashSet<>();
            int fallos = 0;
            final int maxFallos = 3;

            System.out.println("[" + getName() + "] Palabra generada: " + palabra);

            // Mensaje inicial al cliente
            out.writeUTF("START:" + new String(estado) + ":" + fallos);

            boolean terminado = false;
            while (!terminado) {
                String guess = in.readUTF();
                if (guess == null) break;
                String original = guess;
                guess = guess.trim().toLowerCase();

                char letra;
                boolean valido = (guess.length() == 1);
                if (valido) {
                    letra = guess.charAt(0);
                } else {
                    // cualquier entrada con más de 1 carácter cuenta como fallo
                    letra = '\0';
                }

                String mensajeTurno;
                boolean acierto = false;
                if (!valido) {
                    fallos++;
                    mensajeTurno = "Entrada inválida ('" + original + "'). Debe ser un solo carácter. Se cuenta como fallo.";
                    System.out.println("[" + getName() + "] " + mensajeTurno + " | Fallos=" + fallos);
                } else if (usadas.contains(letra)) {
                    mensajeTurno = "Letra ya usada: '" + letra + "'.";
                    System.out.println("[" + getName() + "] Repetida '" + letra + "' | Estado=" + String.valueOf(estado) + " | Fallos=" + fallos);
                } else {
                    usadas.add(letra);
                    int aciertosEnEsteTurno = 0;
                    for (int i = 0; i < palabra.length(); i++) {
                        if (palabra.charAt(i) == letra && estado[i] == '_') {
                            estado[i] = letra;
                            aciertosEnEsteTurno++;
                        }
                    }
                    if (aciertosEnEsteTurno > 0) {
                        acierto = true;
                        mensajeTurno = "¡Acierto! La letra '" + letra + "' aparece " + aciertosEnEsteTurno + " vez/veces.";
                    } else {
                        fallos++;
                        mensajeTurno = "Fallaste. La letra '" + letra + "' no está.";
                    }
                    System.out.println("[" + getName() + "] Intento='" + letra + "' => " + (acierto ? "ACIERTA" : "FALLA") + " | Estado=" + String.valueOf(estado) + " | Fallos=" + fallos);
                }

                // Comprobar fin de juego
                if (String.valueOf(estado).equals(palabra)) {
                    out.writeUTF("WIN:" + palabra + ":" + fallos);
                    System.out.println("[" + getName() + "] ¡Ha ganado! Palabra=" + palabra + ", Fallos=" + fallos);
                    terminado = true;
                } else if (fallos >= maxFallos) {
                    out.writeUTF("LOSE:" + palabra + ":" + fallos);
                    System.out.println("[" + getName() + "] ¡Ha perdido! Palabra=" + palabra + ", Fallos=" + fallos);
                    terminado = true;
                } else {
                    out.writeUTF("STATE:" + new String(estado) + ":" + fallos + ":" + mensajeTurno);
                }
            }
        } catch (Exception e) {
            System.err.println("[" + getName() + "] Error: " + e.getMessage());
        } finally {
            try { skCliente.close(); } catch (Exception ignored) {}
            System.out.println("[" + getName() + "] desconectado");
        }
    }
}