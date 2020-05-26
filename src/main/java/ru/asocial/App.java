package ru.asocial;

import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.CRC32;

public class App {
    static int GLOBAL_WIDTH = 1000;
    static int GLOBAL_HEIGHT = 1000;
    static byte[][] currentGeneration;
    static byte[][] nextGeneration;
    static long currentGenerationIndex = 0;
    static List<Long> crcList = new LinkedList<>();
    static long alive = 0;
    static final BlockingQueue queue = new LinkedBlockingQueue(1);
    static List<Point> lastPoints = new LinkedList<>();
    static JPanel panel;
    static JFrame frame;
    static boolean isPentamino;
    static boolean stopCalculations = false;
    static Point p1, p2;
    static boolean dragging;
    static AtomicLong delay = new AtomicLong(1000);
    static Map<String, FigureTemplate> templates = new HashMap<>();
    static boolean mustStopOnRepeat = true;
    static boolean useOldTemplates = false;
    static int maxWidth = 0;
    static int maxHeight = 0;

    static void addObject(FigureTemplate f, int x, int y){
        alive += f.render(currentGeneration, x, y);
    }

    static void addObject(FigureTemplate f, int x, int y, boolean flipX, boolean flipY){
        alive += f.render(currentGeneration, x, y, flipX, flipY);
    }

    static void custom_preset(){
       /* Figure f = templates.get("houndstoothagar");
        addObject(f, 500, 500);*/
       int x = 10, y = 10;
       int dx = 170, dy = 170;
       List<Map.Entry<String, FigureTemplate>> entries = new LinkedList<>(templates.entrySet());
       Collections.shuffle(entries);
       for (Map.Entry<String, FigureTemplate> entry : entries) {
           FigureTemplate t = entry.getValue();
           if (x + t.getWidth() >= GLOBAL_WIDTH - 50) {
               x = 50;
               y += t.getHeight() + dy;
           }
           if (y + t.getHeight() >= GLOBAL_HEIGHT - 50) {
               break;
           }
           addObject(t, x, y);
           x += t.getWidth() + dx;
           //y += t.getHeight() + dy;
       }
    }

    static void random_figure(){
        List<Map.Entry<String, FigureTemplate>> entries = new LinkedList<>(templates.entrySet());
        Collections.shuffle(entries);

        Map.Entry<String, FigureTemplate> e = entries.get(0);
        frame.setTitle(e.getKey());

        addObject(e.getValue(), 500, 500);
    }

    static List<String> oldTemplates(){
        List<String> paths = new LinkedList<>();
        paths.add("oktagon4");
        paths.add("vulkano_p5");
        paths.add("glyder");
        paths.add("pentamino_R");
        paths.add("fever");
        paths.add("laundry");
        paths.add("rifle");
        paths.add("qwazar");
        paths.add("hertz_osc");
        paths.add("something_flying");
        paths.add("galaxy_cock");
        return paths;
    }

    static void loadTemplates() throws Exception {
        List<String> paths;
        if (useOldTemplates) {
            paths = oldTemplates();
        }
        else {
            try (InputStream is = App.class.getResourceAsStream("/templates.txt")) {
                paths = IOUtils.readLines(is);
            }
        }

        for (String path : paths) {
            FigureTemplate f = Util.parseTemplate(path);
            templates.put(path, f);
            maxWidth = maxWidth > f.getWidth() ? maxWidth : f.getWidth();
            maxHeight = maxHeight > f.getHeight() ? maxHeight : f.getHeight();
        }
    }

    static void print(){
        System.out.println("Generation: " + currentGenerationIndex + "; alive: " + alive);
    }

    public static void main(String[] argc) throws Exception{
        frame = new JFrame();
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyChar() == '1'){
                    delay.set(100);
                }
                else if (e.getKeyChar() == '2') {
                    delay.set(1000);
                }
                else if (e.getKeyChar() == '0') {
                    delay.set(0);
                }
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        panel = new JPanel(){
            @Override
            protected void paintChildren(Graphics g) {
                super.paintChildren(g);
                g.setColor(Color.GRAY);
                List<Point> points = (List<Point>) queue.poll();
                if (points != null) {
                    lastPoints = points;
                }

                BufferedImage buf = new BufferedImage(GLOBAL_WIDTH, GLOBAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = (Graphics2D) buf.getGraphics();
                for (Point p : lastPoints) {
                    g2d.drawOval(p.getX(), p.getY(), 1,1);
                }
                Image imageToDraw = buf;
                if (p1 != null && p2 != null && !dragging) {
                    int left = Math.min(p1.getX(), p2.getX());
                    int top = Math.min(p1.getY(), p2.getY());
                    int right = Math.max(p1.getX(), p2.getX());
                    int bottom = Math.max(p1.getY(), p2.getY());
                    int halfize = Math.max(right-left, bottom-top) / 2;
                    int cx = (right + left) / 2;
                    int cy = (bottom + top) / 2;
                    imageToDraw = buf.getSubimage(cx - halfize, cy-halfize, 2 * halfize, 2 * halfize);
                }
                g.drawImage(imageToDraw, 0, 0, GLOBAL_WIDTH, GLOBAL_HEIGHT, null);
            }
        };
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                if (e.getButton() == MouseEvent.BUTTON1) {
                    p1 = new Point(e.getX(), e.getY());
                    dragging = true;
                }
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    p1 = null;
                    p2 = null;
                    panel.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getButton() == MouseEvent.BUTTON1) {
                    p2 = new Point(e.getX(), e.getY());
                    dragging = false;
                    panel.repaint();
                }
            }
        });

        frame.setContentPane(panel);
        frame.setSize(GLOBAL_WIDTH, GLOBAL_HEIGHT);
        frame.setVisible(true);

        currentGeneration = Util.initArray(GLOBAL_WIDTH, GLOBAL_HEIGHT);
        nextGeneration = Util.initArray(GLOBAL_WIDTH, GLOBAL_HEIGHT);
        loadTemplates();
        System.out.println("Templates: maxWidth=" + maxWidth + "; maxHeight=" + maxHeight);

        //custom_preset();
        random_figure();
        crc32Generation();
        print();

        while (!stopCalculations) {
            calculateNextGeneration();
        }
    }

    static boolean isDead(byte cell){
        return cell == 0;
    }

    static void crc32Generation() {
        CRC32 crc = new CRC32();
        for (int i = 0; i < GLOBAL_WIDTH; i++) {
            crc.update(currentGeneration[i]);
        }

        long value = crc.getValue();
        int index = crcList.indexOf(value);
        if (index >= 0 && mustStopOnRepeat) {
            System.out.println((currentGenerationIndex - index) + " generations ago was the same configuration");
            stopCalculations = true;
        }
        crcList.add(value);
    }

    static void calculateNextGeneration() throws  Exception{
        List<Point> drawPoints = new LinkedList<>();
        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i < GLOBAL_WIDTH; i++) {
            for (int j = 0; j < GLOBAL_HEIGHT; j++) {
                byte self = currentGeneration[i][j];
                int left = (i == 0) ? GLOBAL_WIDTH - 1 : i - 1;
                int right = (i == GLOBAL_WIDTH - 1) ? 0 : i + 1;
                int up = (j == 0) ? GLOBAL_HEIGHT - 1  : j - 1;
                int down = (j == GLOBAL_HEIGHT - 1) ? 0 : j + 1;

                byte upCell = currentGeneration[i][up];
                byte upLeftCell = currentGeneration[left][up];
                byte upRightCell = currentGeneration[right][up];
                byte leftCell = currentGeneration[left][j];
                byte rightCell = currentGeneration[right][j];
                byte downCell = currentGeneration[i][down];
                byte downLeftCell = currentGeneration[left][down];
                byte downRightCell = currentGeneration[right][down];
                int neighboursCount = upCell + upLeftCell + upRightCell + rightCell + downCell + downLeftCell + downRightCell + leftCell;
                byte next;
                if (isDead(self)) {
                    if (neighboursCount == 3 ) {
                        next = 1;
                        alive++;
                    }
                    else {
                        next = 0;
                    }
                }
                else {
                    if (neighboursCount == 2 || neighboursCount == 3) {
                        next = 1;
                    }
                    else {
                        next = 0;
                        alive--;
                    }
                }
                nextGeneration[i][j] = next;
                if (next == 1) {
                    drawPoints.add(new Point(i, j));
                }
            }
        }

        queue.offer(drawPoints);
        panel.repaint();

        if (alive == 0) {
            System.out.println("Nobody left alive");
            //System.exit(0);
            stopCalculations = true;
        }

        byte[][] temporal = currentGeneration;
        currentGeneration = nextGeneration;
        nextGeneration = temporal;
        currentGenerationIndex++;
        print();

        crc32Generation();

        if (isPentamino && currentGenerationIndex == 1103) {
            Thread.sleep(5000);
        }

        long d = delay.get();
        if (d > 0)
            Thread.sleep(d);
    }
}
