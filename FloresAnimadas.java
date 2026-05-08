import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FloresAnimadas extends JPanel {

    // Clase interna que representa una flor
    static class Flor {
        int x, y;
        int velocidadX, velocidadY;
        int tamanio;

        Flor(int x, int y, int velocidadX, int velocidadY, int tamanio) {
            this.x = x;
            this.y = y;
            this.velocidadX = velocidadX;
            this.velocidadY = velocidadY;
            this.tamanio = tamanio;
        }

        void mover(int anchoPanel, int altoPanel) {
            x += velocidadX;
            y += velocidadY;
            // Rebotar en los bordes
            if (x < 0 || x > anchoPanel - tamanio) velocidadX *= -1;
            if (y < 0 || y > altoPanel - tamanio) velocidadY *= -1;
        }

        void dibujar(Graphics g) {
            int cx = x + tamanio / 2;
            int cy = y + tamanio / 2;
            int radio = tamanio / 2;

            // Pétalos amarillos (8 pétalos alrededor del centro)
            g.setColor(Color.YELLOW);
            for (int i = 0; i < 8; i++) {
                double angulo = Math.toRadians(i * 45);
                int px = (int) (cx + Math.cos(angulo) * radio - radio / 2);
                int py = (int) (cy + Math.sin(angulo) * radio - radio / 2);
                g.fillOval(px, py, radio, radio);
            }

            // Centro naranja de la flor
            g.setColor(new Color(200, 100, 0));
            g.fillOval(cx - radio / 3, cy - radio / 3, radio * 2 / 3 * 2, radio * 2 / 3 * 2);
        }
    }

    private final List<Flor> flores = new ArrayList<>();
    private final int CANTIDAD_FLORES = 15; // Integrante A: mas flores
    private final int VELOCIDAD_BASE = 2;

    public FloresAnimadas() {
        setBackground(new Color(34, 139, 34)); // Fondo verde (prado)
        Random rand = new Random();

        for (int i = 0; i < CANTIDAD_FLORES; i++) {
            int x = rand.nextInt(700);
            int y = rand.nextInt(500);
            int vx = (rand.nextBoolean() ? 1 : -1) * (1 + rand.nextInt(VELOCIDAD_BASE));
            int vy = (rand.nextBoolean() ? 1 : -1) * (1 + rand.nextInt(VELOCIDAD_BASE));
            int tam = 40 + rand.nextInt(30);
            flores.add(new Flor(x, y, vx, vy, tam));
        }

        // Timer para la animación (~60 FPS)
        Timer timer = new Timer(16, e -> {
            for (Flor f : flores) {
                f.mover(getWidth(), getHeight());
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (Flor f : flores) {
            f.dibujar(g);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flores Animadas");
        FloresAnimadas panel = new FloresAnimadas();
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
