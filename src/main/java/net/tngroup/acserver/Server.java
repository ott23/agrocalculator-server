package net.tngroup.acserver;

public class Server implements Runnable {

    @Override
    public void run() {
        try {
            for (int i = 5; i > 0; i--) {
                System.out.println("\tдополнительный поток: " + i);
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            System.out.println("\tдополнительный поток прерван");
        }
        System.out.println("\tдополнительный поток завершён");
    }

}
