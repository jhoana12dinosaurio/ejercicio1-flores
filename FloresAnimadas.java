import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FloresAnimadas extends JPanel {

    // ── Clase Girasol ────────────────────────────────────────────────────────
    static class Flor {
        double x, y;
        double velocidadX, velocidadY;
        int tamanio;
        double angulo;
        double velAngulo;
        // Cada flor tiene su propia imagen pre-renderizada para mayor rendimiento
        BufferedImage imagen;

        Flor(int x, int y, double vx, double vy, int tamanio, Random rand) {
            this.x = x;
            this.y = y;
            this.velocidadX = vx;
            this.velocidadY = vy;
            this.tamanio = tamanio;
            this.angulo = rand.nextDouble() * Math.PI * 2;
            this.velAngulo = (rand.nextDouble() * 0.018 + 0.003)
                    * (rand.nextBoolean() ? 1 : -1);
            this.imagen = renderizarGirasol(tamanio);
        }

        /** Dibuja un girasol realista en un BufferedImage reutilizable. */
        static BufferedImage renderizarGirasol(int tam) {
            int sz = tam * 2;
            BufferedImage img = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int cx = sz / 2, cy = sz / 2;
            int rFlor = tam / 2; // radio total de la flor
            int rCentro = (int) (rFlor * 0.38); // radio del disco central
            int numPetalos = 20; // girasoles tienen ~20 pétalos

            // ── Sombra proyectada ──────────────────────────────────────────
            g.setColor(new Color(0, 0, 0, 35));
            g.fillOval(cx - rFlor + 5, cy - rFlor + 8, rFlor * 2, rFlor * 2);

            // ── Pétalos capa trasera (alternados para dar volumen) ─────────
            dibujarCapaPetalos(g, cx, cy, rCentro, rFlor, numPetalos,
                    new Color(200, 130, 0), new Color(170, 100, 0), 0);

            // ── Pétalos capa delantera ─────────────────────────────────────
            dibujarCapaPetalos(g, cx, cy, rCentro, rFlor, numPetalos,
                    new Color(255, 200, 0), new Color(230, 150, 0),
                    Math.PI / numPetalos);

            // ── Disco central marrón oscuro con gradiente ──────────────────
            RadialGradientPaint gradDisco = new RadialGradientPaint(
                    new Point2D.Float(cx - rCentro * 0.25f, cy - rCentro * 0.25f),
                    rCentro * 1.3f,
                    new float[] { 0f, 0.5f, 1f },
                    new Color[] {
                            new Color(100, 60, 10),
                            new Color(60, 35, 5),
                            new Color(30, 15, 0)
                    });
            g.setPaint(gradDisco);
            g.fillOval(cx - rCentro, cy - rCentro, rCentro * 2, rCentro * 2);

            // ── Patrón espiral de semillas (Fibonacci) ─────────────────────
            double phi = Math.PI * (3 - Math.sqrt(5)); // ángulo dorado
            int numSemillas = (int) (Math.PI * rCentro * rCentro / 18.0);
            numSemillas = Math.min(numSemillas, 200);
            for (int i = 0; i < numSemillas; i++) {
                double r = Math.sqrt((double) i / numSemillas) * (rCentro - 3);
                double ang = i * phi;
                int sx = (int) (cx + r * Math.cos(ang));
                int sy = (int) (cy + r * Math.sin(ang));
                int sr = Math.max(1, rCentro / 10);
                // semilla con gradiente pequeño
                float progreso = (float) i / numSemillas;
                Color cSemilla = new Color(
                        (int) (80 + 40 * progreso),
                        (int) (40 + 20 * progreso),
                        (int) (5 + 5 * progreso));
                g.setColor(cSemilla);
                g.fillOval(sx - sr, sy - sr, sr * 2, sr * 2);
                g.setColor(new Color(20, 10, 0, 120));
                g.drawOval(sx - sr, sy - sr, sr * 2, sr * 2);
            }

            // ── Brillo en el disco ─────────────────────────────────────────
            g.setColor(new Color(255, 220, 100, 35));
            g.fillOval(cx - rCentro / 2, cy - rCentro / 2, rCentro, rCentro / 2);

            // ── Borde del disco ────────────────────────────────────────────
            g.setColor(new Color(50, 25, 0));
            g.setStroke(new BasicStroke(1.5f));
            g.drawOval(cx - rCentro, cy - rCentro, rCentro * 2, rCentro * 2);

            g.dispose();
            return img;
        }

        /** Dibuja una capa de pétalos de girasol como elipses rotadas. */
        static void dibujarCapaPetalos(Graphics2D g, int cx, int cy,
                int rCentro, int rFlor, int num, Color c1, Color c2, double offset) {
            int longPetalo = rFlor - rCentro + 5;
            int anchoPetalo = Math.max(5, longPetalo / 3);
            for (int i = 0; i < num; i++) {
                double a = Math.toRadians(360.0 / num * i) + offset;
                int px = (int) (cx + Math.cos(a) * (rCentro + longPetalo / 2));
                int py = (int) (cy + Math.sin(a) * (rCentro + longPetalo / 2));

                GradientPaint gp = new GradientPaint(
                        cx, cy, c1,
                        px, py, c2);
                g.setPaint(gp);

                AffineTransform t = g.getTransform();
                g.translate(px, py);
                g.rotate(a + Math.PI / 2);
                // Pétalo con punta ligeramente puntiaguda usando Path2D
                Path2D petalo = new Path2D.Double();
                petalo.moveTo(0, -longPetalo / 2);
                petalo.curveTo(anchoPetalo, -longPetalo / 4,
                        anchoPetalo, longPetalo / 4,
                        0, longPetalo / 2);
                petalo.curveTo(-anchoPetalo, longPetalo / 4,
                        -anchoPetalo, -longPetalo / 4,
                        0, -longPetalo / 2);
                petalo.closePath();
                g.fill(petalo);
                g.setColor(c2.darker());
                g.setStroke(new BasicStroke(0.8f));
                g.draw(petalo);
                // Nervio central del pétalo
                g.setColor(new Color(255, 240, 100, 150));
                g.setStroke(new BasicStroke(0.7f));
                g.drawLine(0, -longPetalo / 2 + 2, 0, longPetalo / 2 - 2);
                g.setTransform(t);
            }
        }

        void mover(int ancho, int alto) {
            x += velocidadX;
            y += velocidadY;
            angulo += velAngulo;
            if (x < 0 || x > ancho - tamanio)
                velocidadX *= -1;
            if (y < 0 || y > alto - tamanio)
                velocidadY *= -1;
        }

        void dibujar(Graphics2D g2d) {
            int cx = (int) x + tamanio / 2;
            int cy = (int) y + tamanio / 2;
            AffineTransform original = g2d.getTransform();
            g2d.translate(cx, cy);
            g2d.rotate(angulo);
            // Dibujar la imagen pre-renderizada centrada
            int half = imagen.getWidth() / 2;
            g2d.drawImage(imagen, -half, -half, null);
            g2d.setTransform(original);
        }
    }

    private final List<Flor> flores = new ArrayList<>();
    private final int CANTIDAD_FLORES = 15; // Resolucion: mas flores (A) + mayor velocidad (B)
    private final int VELOCIDAD_BASE = 4;
    private int tick = 0;

    public FloresAnimadas() {
        Random rand = new Random();
        for (int i = 0; i < CANTIDAD_FLORES; i++) {
            int x = rand.nextInt(700);
            int y = rand.nextInt(500);
            double vx = (rand.nextBoolean() ? 1 : -1) * (0.8 + rand.nextDouble() * VELOCIDAD_BASE);
            double vy = (rand.nextBoolean() ? 1 : -1) * (0.8 + rand.nextDouble() * VELOCIDAD_BASE);
            int tam = 55 + rand.nextInt(45);
            flores.add(new Flor(x, y, vx, vy, tam, rand));
        }

        Timer timer = new Timer(16, e -> {
            tick++;
            for (Flor f : flores)
                f.mover(getWidth(), getHeight());
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Fondo degradado cielo → prado
        GradientPaint fondoGrad = new GradientPaint(
                0, 0, new Color(135, 206, 250), // azul cielo
                0, getHeight(), new Color(80, 200, 80) // verde prado
        );
        g2d.setPaint(fondoGrad);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Sol animado en esquina superior derecha
        double pulso = 1.0 + 0.05 * Math.sin(tick * 0.05);
        int solR = (int) (55 * pulso);
        int solX = getWidth() - 100, solY = 60;
        // rayos del sol
        g2d.setColor(new Color(255, 240, 100, 160));
        g2d.setStroke(new BasicStroke(3f));
        for (int i = 0; i < 12; i++) {
            double a = Math.toRadians(i * 30 + tick * 0.3);
            g2d.drawLine(
                    (int) (solX + Math.cos(a) * (solR + 5)), (int) (solY + Math.sin(a) * (solR + 5)),
                    (int) (solX + Math.cos(a) * (solR + 18)), (int) (solY + Math.sin(a) * (solR + 18)));
        }
        RadialGradientPaint gradSol = new RadialGradientPaint(
                new Point2D.Float(solX - solR / 4f, solY - solR / 4f), solR * 1.4f,
                new float[] { 0f, 1f },
                new Color[] { new Color(255, 255, 150), new Color(255, 180, 0) });
        g2d.setPaint(gradSol);
        g2d.fillOval(solX - solR, solY - solR, solR * 2, solR * 2);

        // Nubes decorativas
        dibujarNube(g2d, (int) (150 + 60 * Math.sin(tick * 0.003)), 50, 90, 35);
        dibujarNube(g2d, (int) (400 + 40 * Math.sin(tick * 0.004 + 1)), 30, 70, 28);

        // Césped inferior
        g2d.setColor(new Color(34, 160, 34));
        g2d.fillRect(0, getHeight() - 60, getWidth(), 60);
        g2d.setColor(new Color(50, 200, 50));
        for (int i = 0; i < getWidth(); i += 12) {
            int h = 10 + (int) (8 * Math.sin(i * 0.3 + tick * 0.04));
            g2d.fillRect(i, getHeight() - 60 - h, 5, h + 2);
        }

        // Flores
        for (Flor f : flores)
            f.dibujar(g2d);
    }

    private void dibujarNube(Graphics2D g2d, int cx, int cy, int w, int h) {
        g2d.setColor(new Color(255, 255, 255, 210));
        g2d.fillOval(cx - w / 2, cy - h / 2, w, h);
        g2d.fillOval(cx - w / 2 - 20, cy - h / 2 + 10, (int) (w * 0.7), (int) (h * 0.8));
        g2d.fillOval(cx + w / 4, cy - h / 2 + 8, (int) (w * 0.6), (int) (h * 0.75));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flores Animadas");
        FloresAnimadas panel = new FloresAnimadas();
        frame.add(panel);
        frame.setSize(900, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
