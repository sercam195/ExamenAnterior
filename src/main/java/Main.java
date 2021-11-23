
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    static ArrayList<Hilos> l = new ArrayList<>();
    static int mayor = 0;
    static String gandor = "";

    public static void main(String[] args) {
        for (int i = 1; i <= 15; i++) {
            Hilos hilo = new Hilos(false, 0);
            hilo.setName("Jugador " + i);
            hilo.start();
            l.add(hilo);
        }
        for (Hilos h : l) {
            try {
                h.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Hilos h : l) {
            if (mayor < h.puntuacion) {
                mayor = h.puntuacion;
                gandor = "El " + h.getName() + " gana con una puntuación de " + mayor;
            }
        }
        System.out.println(gandor);

    }
}

class Hilos extends Thread {
    boolean bonus;
    int puntuacion;

    Hilos(boolean bonus, int puntuacion) {
        this.bonus = bonus;
        this.puntuacion = puntuacion;
    }

    @Override
    public void run() {
        try {
            Batalla.addParticipante(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Batalla {

    private static final int NUM_ACCESO_SIMULTANEOS = 10;
    static AtomicInteger cont = new AtomicInteger(0);
    static AtomicBoolean primero = new AtomicBoolean(false);
    static Semaphore semaphore = new Semaphore(NUM_ACCESO_SIMULTANEOS, true);

    public static void addParticipante(Hilos hilo) throws InterruptedException {
        Random r = new Random();
        try {
            semaphore.acquire();
            Thread.sleep(r.nextInt(5000) + 1000);
            if (!primero.compareAndExchange(false, true)) {
                hilo.bonus = true;
            }

            if (cont.get() < 5) {
                Puntuacion.calcularPuntuacion(hilo);
                System.out.println(hilo.getName() + " - Puntuacion: " + hilo.puntuacion + "\n");
                cont.getAndIncrement();
            }
            if (cont.get() < 10 && cont.get() >= 5) {
                System.out.println("El " + hilo.getName() + " ha sido eliminado" + "\n");
                cont.getAndIncrement();
            }
            if (cont.get() == 10) {
                semaphore.release(5);
            }
            if (cont.get() < 15 && cont.get() >= 10) {
                Puntuacion.calcularPuntuacion(hilo);
                System.out.println(hilo.getName() + " - Puntuacion: " + hilo.puntuacion + "\n");
                cont.getAndIncrement();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Puntuacion {

    public static void calcularPuntuacion(Hilos hilos) {
        Random r = new Random();
        if (hilos.bonus) {
            hilos.puntuacion = (r.nextInt(10) + 1) * 2;
        } else {
            hilos.puntuacion = r.nextInt(10) + 1;
        }
    }
}