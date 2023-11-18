package com.toeic;

import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class FireEimtter extends Emitter {
    @Override
    public List<Particle> emit(double x, double y) {
        List<Particle> particles = new ArrayList<>();

        int numParticles = 5;
        for (int i = 0; i < numParticles; i++){
            Particle p = new Particle(x, y, new Point2D((Math.random() - 0.5) * 0.65, Math.random() * -2),
                    10, 1.0, Color.rgb(230, 40, 45), BlendMode.ADD);
            particles.add(p);
        }

        return particles;
    }
}
