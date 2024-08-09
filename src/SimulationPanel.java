import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

public class SimulationPanel extends JPanel implements KeyListener {
    private World world;
    private Segway segway;
    private Body person;
    private List<Body> obstacles;
    private boolean isGameOver = false;

    private static final int FRAME_WIDTH = 60;
    private static final int FRAME_HEIGHT = 10;
    private static final int WHEEL_DIAMETER = 30;
    private static final int GROUND_HEIGHT = 15;
    private static final int OBSTACLE_SIZE = 30;
    private static final int GAME_OVER_FONT_SIZE = 36;
    private static final double SCALE = 30.0;

    private double wheelRotation = 0;

    public SimulationPanel(World world, Segway segway, Body person, List<Body> obstacles) {
        this.world = world;
        this.segway = segway;
        this.person = person;
        this.obstacles = obstacles;
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        drawGround(g);
        drawObstacles(g);
        drawSegway(g);
        drawPerson(g);
        if (isGameOver) {
            drawGameOver(g);
        }
    }

    private void drawBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(135, 206, 235)); // Sky blue
        g2d.fillRect(0, 0, getWidth(), getHeight() - GROUND_HEIGHT);
    }

    private void drawGround(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(139, 69, 19)); // Brown ground
        g2d.fillRect(0, getHeight() - GROUND_HEIGHT, getWidth(), GROUND_HEIGHT);

        g2d.setColor(new Color(34, 139, 34)); // Green grass
        g2d.fillRect(0, getHeight() - GROUND_HEIGHT, getWidth(), GROUND_HEIGHT / 4);
    }

    private static final int OBSTACLE_WIDTH = 20; // width of the obstacles
    private static final int OBSTACLE_HEIGHT = 70;

    private void drawObstacles(Graphics g) {
        g.setColor(Color.RED);
        for (Body obstacle : obstacles) {
            Vec2 pos = obstacle.getPosition();
            int x = (int) (pos.x * SCALE);
            int y = (int) (getHeight() - GROUND_HEIGHT - pos.y * SCALE);
            g.fillRect(x, y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        }
    }

    private void drawSegway(Graphics g) {
        Vec2 framePos = segway.getFrame().getPosition();
        double x = framePos.x * SCALE;
        double y = getHeight() - GROUND_HEIGHT - framePos.y * SCALE;
        drawWheels(g, x, y);
        drawPlatform(g, x, y);
        drawHandle(g, x, y);
    }

    private void drawWheels(Graphics g, double x, double y) {
        double wheelRadius = WHEEL_DIAMETER / 2.0;
        double platformWidth = SCALE * 2.5;
        g.setColor(Color.BLACK);

        drawSpinningWheel(g, x - platformWidth / 2, y, wheelRadius);
        drawSpinningWheel(g, x + platformWidth / 2, y, wheelRadius);
    }

    private void drawSpinningWheel(Graphics g, double x, double y, double radius) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform old = g2d.getTransform();

        // Translate to the center of the wheel
        g2d.translate(x, y + radius);

        // Rotate the wheel
        wheelRotation += 0.1; // Increment rotation for animation
        g2d.rotate(wheelRotation);

        // Draw the wheel
        g2d.setColor(Color.BLACK);
        g2d.fillOval((int) -radius, (int) -radius, (int) (2 * radius), (int) (2 * radius));

        // Draw the spokes
        g2d.setColor(Color.GRAY);
        for (int i = 0; i < 6; i++) {
            g2d.drawLine(0, 0, (int) radius, 0);
            g2d.rotate(Math.PI / 3); // Rotate by 60 degrees for the next spoke
        }

        g2d.setTransform(old);
    }

    private void drawPlatform(Graphics g, double x, double y) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform old = g2d.getTransform();
        double platformWidth = SCALE * 2.5;
        double platformHeight = SCALE / 4.0;

        g2d.translate(x, y - WHEEL_DIAMETER / 2.0);
        g2d.rotate(Math.toRadians(5)); // Example angle, adjust as needed

        g2d.setColor(new Color(169, 169, 169)); // Light gray platform
        g2d.fillRect((int) (-platformWidth / 2), (int) (-platformHeight), (int) platformWidth, (int) platformHeight);
        g2d.setTransform(old);
    }

    private void drawHandle(Graphics g, double x, double y) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform old = g2d.getTransform();
        double handleWidth = SCALE / 6.0;
        double handleHeight = FRAME_HEIGHT * 10.0;

        g2d.translate(x, y - WHEEL_DIAMETER / 2.0 - SCALE / 4.0);
        g2d.rotate(Math.toRadians(15)); // Example angle, adjust as needed

        g2d.setColor(new Color(169, 169, 169)); // Light gray handle
        g2d.fillRect((int) (-handleWidth / 2 + 35), (int) (-handleHeight), (int) handleWidth, (int) handleHeight);
        g2d.setTransform(old);
    }

    private void drawPerson(Graphics g) {
        Vec2 segwayPos = segway.getFrame().getPosition();
        double x = segwayPos.x * SCALE;
        double y = getHeight() - GROUND_HEIGHT - segwayPos.y * SCALE - WHEEL_DIAMETER / 2.0 - SCALE / 4.0 - SCALE * 1.5;

        int personHeight = (int) (SCALE * 4);
        int personWidth = (int) (SCALE * 1.5);
        int bodyHeight = personHeight - personWidth;
        int legWidth = personWidth / 2;
        int legHeight = bodyHeight / 2;
        int shoeWidth = legWidth + 5;
        int shoeHeight = 10;
        int armLength = personWidth;
        int armY = (int) (y - personHeight + personWidth + bodyHeight / 4);

        int headX = (int) (x - personWidth / 2);
        int headY = (int) (y - personHeight);
        int bodyX = headX;
        int bodyY = headY + personWidth;
        int legX = (int) (x - legWidth / 2);
        int legY = bodyY + bodyHeight;
        int shoeX = legX - 2;
        int shoeY = legY + legHeight - shoeHeight;

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform old = g2d.getTransform();

        g2d.setStroke(new BasicStroke(5));

        // Draw the head
        g.setColor(new Color(255, 224, 189)); // Skin color
        g.fillOval(headX, headY, personWidth, personWidth);

        // Draw the helmet
        g.setColor(Color.ORANGE);
        g.fillOval(headX, headY, personWidth, personWidth / 2);

        // Draw the body
        g.setColor(new Color(255, 165, 0)); // Uniform color
        g.fillRect(bodyX, bodyY, personWidth, bodyHeight);

        // Draw the right arm
        g.setColor(new Color(128, 128, 128)); // Arm color
        int rightArmStartX = (int) (x + armLength / 2);
        int rightArmStartY = armY;
        int rightArmEndX = rightArmStartX + 10;
        int rightArmEndY = rightArmStartY + 20;
        int shortHorizontalEndX = rightArmEndX + 15;

        g2d.drawLine(rightArmStartX, rightArmStartY, rightArmEndX, rightArmEndY);
        g2d.drawLine(rightArmEndX, rightArmEndY, shortHorizontalEndX, rightArmEndY);
        g.setColor(Color.black); // Arm color
        g.fillOval(shortHorizontalEndX, rightArmEndY-5, 20, 15);


        // Draw the legs
        g.setColor(new Color(255, 165, 0)); // Pant color
        g.fillRect(legX, legY, legWidth, legHeight);

        // Draw the shoes
        g.setColor(Color.BLACK);
        g.fillRect(shoeX, shoeY, shoeWidth, shoeHeight);

        g2d.setTransform(old);
    }



    private void drawGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, GAME_OVER_FONT_SIZE));
        g.drawString("Game Over", getWidth() / 2 - 100, getHeight() / 2);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isGameOver) {
            int key = e.getKeyCode();
            switch (key) {
                case KeyEvent.VK_RIGHT:
                    segway.moveRight();
                    break;
                case KeyEvent.VK_LEFT:
                    segway.moveLeft();
                    break;
                case KeyEvent.VK_SPACE:
                    segway.jump();
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!isGameOver) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_LEFT) {
                segway.stop();
            }
        }
    }

    public void updateObstacles() {
        for (Body obstacle : obstacles) {
            Vec2 position = obstacle.getPosition();
            position.x -= 0.1f;
            obstacle.setTransform(position, obstacle.getAngle());

            if (position.x < -5.0f) {
                position.x = 40.0f;
                obstacle.setTransform(position, obstacle.getAngle());
            }

            if (isCollision(obstacle)) {
                isGameOver = true;
            }
        }

        // Ensure the Segway stays within the frame
        Vec2 segwayPos = segway.getFrame().getPosition();
        if (segwayPos.x * SCALE < 0) {
            segway.getFrame().setTransform(new Vec2(0, segwayPos.y), segway.getFrame().getAngle());
        } else if (segwayPos.x * SCALE > getWidth()) {
            segway.getFrame().setTransform(new Vec2((float) (getWidth() / SCALE), segwayPos.y), segway.getFrame().getAngle());
        }
    }

    private boolean isCollision(Body obstacle) {
        Vec2 obstaclePos = obstacle.getPosition();
        Vec2 framePos = segway.getFrame().getPosition();
        Vec2 frontWheelPos = segway.getFrontWheel().getPosition();
        Vec2 rearWheelPos = segway.getRearWheel().getPosition();

        float obstacleWidth = 1.0f;
        float obstacleHeight = 1.0f;
        float frameWidth = 2.0f;
        float frameHeight = 0.2f;
        float wheelRadius = 0.5f;

        return checkCollision(obstaclePos, framePos, frameWidth, frameHeight, obstacleWidth, obstacleHeight) ||
                checkCollision(obstaclePos, frontWheelPos, wheelRadius, wheelRadius, obstacleWidth, obstacleHeight) ||
                checkCollision(obstaclePos, rearWheelPos, wheelRadius, wheelRadius, obstacleWidth, obstacleHeight);
    }

    private boolean checkCollision(Vec2 obstaclePos, Vec2 objPos, float objWidth, float objHeight, float obstacleWidth, float obstacleHeight) {
        return obstaclePos.x < objPos.x + objWidth && obstaclePos.x + obstacleWidth > objPos.x &&
                obstaclePos.y < objPos.y + objHeight && obstaclePos.y + obstacleHeight > objPos.y;
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}
