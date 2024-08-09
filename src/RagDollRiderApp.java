import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RagDollRiderApp {
    private World world;
    private Segway segway;
    private Body person;
    private Body rightArm;
    private RevoluteJoint rightArmJoint;
    private SimulationPanel simulationPanel;
    private List<Body> obstacles;

    public RagDollRiderApp() {
        world = new World(new Vec2(0, -10));
        initializeGround();
        initializeSegwayMan();
        initializeObstacles();
        initializePerson();
        initializeUI();
    }

    private void initializeSegwayMan() {
        segway = new Segway(world, 0.0f, 1.5f);
    }

    private void initializeGround() {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(0.0f, 0.0f);
        Body groundBody = world.createBody(groundBodyDef);

        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(50.0f, 0.5f);

        FixtureDef groundFixture = new FixtureDef();
        groundFixture.shape = groundBox;
        groundFixture.density = 0.0f;
        groundFixture.friction = 0.6f;
        groundBody.createFixture(groundFixture);
    }

    private void initializeObstacles() {
        obstacles = new ArrayList<>();
        createRandomObstacles();
    }

    private void createRandomObstacles() {
        Random random = new Random();
        int maxObstacles = 2;
        for (int i = 0; i < maxObstacles; i++) {
            float posX = random.nextFloat() * 10 + 10;
            float posY = 1.0f;
            float size = random.nextFloat() * 1.5f + 0.5f;
            float speed = random.nextFloat() * 0.2f + 0.1f;
            createObstacle(posX, posY, size, speed);
        }
    }

    private void createObstacle(float posX, float posY, float size, float speed) {
        BodyDef obstacleDef = new BodyDef();
        obstacleDef.position.set(posX, posY);
        Body obstacle = world.createBody(obstacleDef);

        PolygonShape obstacleShape = new PolygonShape();
        obstacleShape.setAsBox(size, size);

        FixtureDef obstacleFixture = new FixtureDef();
        obstacleFixture.shape = obstacleShape;
        obstacleFixture.density = 0.0f;
        obstacleFixture.friction = 0.6f;
        obstacle.createFixture(obstacleFixture);

        Vec2 velocity = new Vec2(-speed, 0.0f);
        obstacle.setLinearVelocity(velocity);

        obstacles.add(obstacle);
    }

    private void initializePerson() {
        BodyDef personDef = new BodyDef();
        personDef.type = BodyType.DYNAMIC;
        personDef.position.set(5.0f, 3.0f);
        person = world.createBody(personDef);

        PolygonShape personShape = new PolygonShape();
        personShape.setAsBox(0.5f, 1.5f);
        FixtureDef personFixture = new FixtureDef();
        personFixture.shape = personShape;
        personFixture.density = 1.0f;
        personFixture.friction = 0.3f;
        person.createFixture(personFixture);

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = segway.getFrame();
        jointDef.bodyB = person;
        jointDef.localAnchorA.set(0, 1.5f);
        jointDef.localAnchorB.set(0, -1.5f);
        world.createJoint(jointDef);

        initializeRightArm();
    }

    private void initializeRightArm() {
        BodyDef armDef = new BodyDef();
        armDef.type = BodyType.DYNAMIC;

        PolygonShape armShape = new PolygonShape();
        armShape.setAsBox(0.1f, 0.5f);
        FixtureDef armFixture = new FixtureDef();
        armFixture.shape = armShape;
        armFixture.density = 0.5f;

        armDef.position.set(5.5f, 3.5f);
        rightArm = world.createBody(armDef);
        rightArm.createFixture(armFixture);

        RevoluteJointDef armJointDef = new RevoluteJointDef();
        armJointDef.bodyA = person;
        armJointDef.localAnchorA.set(0.5f, 1.0f);
        armJointDef.bodyB = rightArm;
        armJointDef.localAnchorB.set(0, -0.5f);
        armJointDef.enableLimit = true;
        armJointDef.lowerAngle = (float) -Math.PI / 4;
        armJointDef.upperAngle = (float) Math.PI / 4;
        rightArmJoint = (RevoluteJoint) world.createJoint(armJointDef);
    }

    private void initializeUI() {
        simulationPanel = new SimulationPanel(world, segway, person, obstacles);
        JFrame frame = new JFrame("RagDoll Rider Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(simulationPanel);
        frame.setVisible(true);
    }

    public void start() {
        while (true) {
            world.step(1.0f / 60.0f, 6, 2);
            simulationPanel.updateObstacles();
            simulationPanel.repaint();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (simulationPanel.isGameOver()) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        RagDollRiderApp simulator = new RagDollRiderApp();
        simulator.start();
    }
}
