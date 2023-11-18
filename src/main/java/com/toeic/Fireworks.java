package com.toeic;

import java.util.*;
import java.util.stream.Collectors;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Fireworks extends ErrataPieChart {
    private List<Emitter> emitters = new ArrayList<>();
    List<Point2D> directions = new ArrayList<>(Arrays.asList(
            new Point2D(0, -1),
            new Point2D(0.75, -1),
            new Point2D(1, 0),
            new Point2D(0.75, 1),
            new Point2D(0, 1),
            new Point2D(-0.75, 1),
            new Point2D(-1, 0),
            new Point2D(-0.75, -1)
    ));
    Random random = new Random();

    @Override
    public void setup() {
        frames(50);

        // setOnMouseClicked
        graphicContext.getCanvas().setOnMouseMoved(e -> {
            emitters.add(new Emitter(e.getSceneX(), e.getSceneY()));
            startSound("/Firework.mp3");
        });

        int max = 800;
        int min = 0;

        int x;
        int y;

        for (int i = 0; i < 40; i++) {
            x = (int) (Math.random() * (max - min + 1) + min);
            y = (int) (Math.random() * (max - min + 1) + min);
            emitters.add(new Emitter(x, y));
        }
        startSound("/花火・連発.mp3");
    }

    @Override
    public void draw() {
        for (Emitter emitter : emitters) {
            emitter.emit(graphicContext);
        }
    }

    public class Emitter {
        List<Spark> sparks = new ArrayList<>();
        double x, y;

        public Emitter(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public void emit(GraphicsContext gc) {
            for (int i = 0; i < directions.size(); i++) {
                int duration = random.nextInt(100) + 2;
                double xDir = directions.get(i).getX();
                double yDir = directions.get(i).getY();
                Spark p = new Spark(x, y, duration,
                        xDir, yDir);
                sparks.add(p);
            }

            ColorSeeds colorSeeds = new ColorSeeds();
            int max = 5;
            int min = 0;
            int i = (int)(Math.random()*(max-min+1)+min);
            int j = (int)(Math.random()*(max-min+1)+min);
            int k = (int)(Math.random()*(max-min+1)+min);

            for (Spark particle : sparks) {
                particle.step();

                particle.show(gc, colorSeeds.getRed(i), colorSeeds.getGreen(j), colorSeeds.getBlue(k));
            }
            sparks = sparks.stream().filter(p ->
                    p.duration > 0).collect(Collectors.toList());
        }
    }

    public class Spark {
        int duration;
        double x, y, yDir, xDir;

        public Spark(double x, double y, int duration, double
                xDir, double yDir) {
            this.x = x;
            this.y = y;
            this.duration = duration;
            this.xDir = xDir;
            this.yDir = yDir;
        }

        public void step() {
            x += xDir;
            y += yDir;
            duration--;
        }

        public void show(GraphicsContext gc, int red, int green, int blue) {
            gc.setFill(Color.rgb(red, green, blue, 0.6));
            gc.fillOval(x, y, 3, 3);
        }
    }

    private class ColorSeeds {
        List<Integer> red;
        List<Integer> green;
        List<Integer> blue;

        public ColorSeeds() {
            red = new ArrayList<>();
            green = new ArrayList<>();
            blue = new ArrayList<>();

            Collections.addAll(red, 255,   0,   0, 255,   0, 255);
            Collections.addAll(green, 0, 255, 255, 255, 255,   0);
            Collections.addAll(blue,  0, 255,   0,   0, 255, 255);
        }

        public Integer getRed(int index) { return red.get(index);}

        public Integer getGreen(int index) { return green.get(index);}

        public Integer getBlue(int index) { return blue.get(index);}
    }
}