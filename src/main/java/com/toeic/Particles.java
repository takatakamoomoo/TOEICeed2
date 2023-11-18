package com.toeic;

import java.util.concurrent.ThreadLocalRandom;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * An animated circle that moves in a straight line at constant velocity within a
 * rectangular container whose sides are parallel to the coordinate axes and whose
 * top-left corner is at the origin (0, 0). The particle bounces off the sides of
 * the container in accordance with the Law of Reflection.
 *
 * @author Drue Coles
 */
public class Particles extends Circle {

    // distance (in pixels) traveled per unit of time
    private static final int DISTANCE = 10;

    private final int containerWidth;
    private final int containerHeight;

    // the particle's current direction of motion
    private int dx; // change in x-coordinate of particle's center
    private int dy; // change in y-coordinate of particle's center

    // updates position ot discrete intervals
    private final Timeline timeline;

    /**
     * Creates a new particle with initial direction selected a random.
     *
     * @param x the x-coordinate of this particle's center
     * @param y the y-coordinate of this particle's center
     * @param r the radius of this particle
     * @param c the color of this particle
     * @param w width of the bounding box (container of particle)
     * @param h height of the bounding box
     * @param t milliseconds between position updates
     */
    public Particles(int x, int y, int r, Color c, int w, int h, int t) {
        super(x, y, r, c);
        this.containerWidth = w;
        this.containerHeight = h;
        meander(); // randomize direction

        KeyFrame kf = new KeyFrame(Duration.millis(t), e -> move());
        timeline = new Timeline(kf);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Moves this particle in the current direction of movement.
     */
    private void move() {
        double x = getCenterX();
        double y = getCenterY();
        double r = getRadius();

        // On impact with vertical side of container, reflect horizontally.
        if (x <= r || x >= containerWidth - r) {
            dx = -dx;
        }

        // On impact with horizontal side of container, reflect vertically.
        if (y <= r || y >= containerHeight - r) {
            dy = -dy;
        }

        double nextX = x + dx;
        double nextY = y + dy;

        // If particle has moved out of container's boundaries, bring it back.
        if (nextX < r) {
            nextX = r;
        }
        if (nextY < r) {
            nextY = r;
        }
        if (nextX > containerWidth - r) {
            nextX = containerWidth - r;
        }
        if (nextY > containerHeight - r) {
            nextY = containerHeight - r;
        }

        setCenterX(nextX);
        setCenterY(nextY);
    }

    /**
     * Toggles the pause/play state of this particle's animation.
     */
    public void toggleRunState() {
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.pause();
        } else {
            timeline.play();
        }
    }

    /**
     * Randomizes the direction of this particle.
     */
    public void meander() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        // After initializing dx to a random integer between 1 and DISTANCE,
        // dy is calculated using the Pythagorean Theorem so that the total
        // distance traveled with each update will be exactly DISTANCE.
        this.dx = rand.nextInt(1, DISTANCE + 1);
        this.dy = (int) Math.sqrt(DISTANCE * DISTANCE - dx * dx);

        // At this point, dx and dy are both positive, so the particle moves
        // only down and to the right. To allow for up and/or left movement,
        // flip the sign of dx and dy with 50% probability.
        if (rand.nextBoolean()) {
            dx = -dx;
        }
        if (rand.nextBoolean()) {
            dy = -dy;
        }
    }
}
