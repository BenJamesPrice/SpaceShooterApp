package com.benprice.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    private final SpaceShooter game;
    private Array<Rectangle> enemies;
    private OrthographicCamera camera;
    private Texture shipImage;
    private Texture enemyImage;
    private Texture laserImage;
    private Texture backgroundImage1;
    private Texture backgroundImage2;
    private float xMax, xCoordBg1, xCoordBg2;
    private Rectangle ship;
    private long lastEnemySpawnTime; //Can be ship or asteroid
    private long lastLaserSpawnTime;
    private Array<Rectangle> lasers;
    private Texture powerUpImage;
    private Rectangle powerUp;
    private long lastPowerUpTime; //Time since last powerup spawned
    private boolean powerUpVisible;
    private boolean powerUpActive; //Ship has used powerup
    private long powerUpActiveTime; //Time powerup has been active for
    private Sound laserSound;
    private Array<Rectangle> asteroids; //Add asteroids after core features work
    private Texture asteroidImage;
    private long lastAsteroidSpawnTime;

    public GameScreen(final SpaceShooter game) {
        this.game = game;
        game.setPoints(0);
        // Load textures
        shipImage = new Texture(Gdx.files.internal("ship2.png"));
        enemyImage = new Texture(Gdx.files.internal("enemy.png"));
        laserImage = new Texture(Gdx.files.internal("laser.png"));
        powerUpImage = new Texture(Gdx.files.internal("powerup.png"));
        asteroidImage = new Texture(Gdx.files.internal("asteroid.png"));

        // Background
        backgroundImage1 = new Texture(Gdx.files.internal("background.png"));
        backgroundImage2 = new Texture(Gdx.files.internal("background.png"));
        xMax = 800;
        xCoordBg1 = xMax*(-1);
        xCoordBg2 = 0;

        // load the laser sound effect and the background music
        laserSound = Gdx.audio.newSound(Gdx.files.internal("laserSound.ogg"));

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.screenWidth, game.screenHeight);

        // create a Rectangle to logically represent the ship
        ship = new Rectangle();
        ship.x = 20;
        ship.y = 208;
        ship.width = 75;
        ship.height = 99;

        // create powerup
        powerUp = new Rectangle();
        powerUp.x = game.screenWidth;
        powerUp.y = 200;
        powerUp.width = 34;
        powerUp.height = 33;
        lastPowerUpTime = TimeUtils.millis() - 15000;

        // create the enemies array and spawn the first enemy
        enemies = new Array<Rectangle>();
        asteroids = new Array<Rectangle>();
        lasers = new Array<Rectangle>();
        spawnEnemyShip();
        spawnAsteroid();
        spawnLaser();
    }

    private void spawnEnemyShip() {
        Rectangle enemy = new Rectangle();
        enemy.y = MathUtils.random(0, game.screenHeight - 82);
        enemy.x = game.screenWidth;
        enemy.width = 84;
        enemy.height = 82;
        enemies.add(enemy);
        lastEnemySpawnTime = TimeUtils.nanoTime();
    }

    private void spawnAsteroid() {
        Rectangle rock = new Rectangle();
        rock.x = game.screenWidth;
        rock.y = MathUtils.random(0, game.screenHeight - 96);
        rock.width = 98;
        rock.height = 96;
        asteroids.add(rock);
        lastAsteroidSpawnTime = TimeUtils.millis();
    }

    private void spawnLaser() {
        Rectangle laser1 = new Rectangle();
        Rectangle laser2 = new Rectangle();
        laser1.x = laser2.x = ship.x + 42;
        laser1.y = ship.y + 18;
        laser2.y = ship.y + 68;
        laser1.width = laser2.width = 54;
        laser1.height = laser2.height = 13;
        lasers.add(laser1);
        lasers.add(laser2);
        lastLaserSpawnTime = TimeUtils.nanoTime();
        laserSound.play();
    }

    private void spawnPowerUp() {
        powerUp.y = MathUtils.random(0, game.screenHeight - 33);
        powerUpVisible = true; // start moving it
        lastPowerUpTime = TimeUtils.millis();
    }

    @Override
    public void show() {

    }

    private void gameOver(){
        game.setScreen(new GameOverScreen(game));
        dispose();
    }

    @Override
    public void render(float delta) {
        // Clear screen and set background colour
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Move scrolling background
        xCoordBg1 -= 100 * Gdx.graphics.getDeltaTime();
        xCoordBg2 = xCoordBg1 + xMax;
        if (xCoordBg1 <= -xMax) {
            xCoordBg1 = 0;
            xCoordBg2 = xMax;
        }

        // Draw ship, all enemies and lasers
        game.batch.begin();
        game.batch.draw(backgroundImage1, xCoordBg1, 0);
        game.batch.draw(backgroundImage2, xCoordBg2, 0);
        game.font.draw(game.batch, "Points: " + game.getPoints(), 20, 450);
        game.batch.draw(shipImage, ship.x, ship.y);
        for (Rectangle enemy : enemies)
            game.batch.draw(enemyImage, enemy.x, enemy.y);
        for (Rectangle laser : lasers)
            game.batch.draw(laserImage, laser.x, laser.y);
        for (Rectangle rock:asteroids)
            game.batch.draw(asteroidImage, rock.x, rock.y);
        game.batch.draw(powerUpImage, powerUp.x, powerUp.y);
        game.batch.end();

        // Handle user input
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (touchPos.y<ship.y+32) ship.y -=5;
            if (touchPos.y>ship.y+32) ship.y +=5;
        }

        // Keep ship on screen
        if (ship.y < 0) ship.y = 0;
        if (ship.y > game.screenHeight - 64) ship.y = game.screenHeight - 64;

        // Create new enemy and asteroid
        if (TimeUtils.nanoTime() - lastEnemySpawnTime > 800000000)
            spawnEnemyShip();
        if (TimeUtils.millis() - lastAsteroidSpawnTime > 3000)
            spawnAsteroid();

        // Increase enemy speed
        int enemySpeed = 250 + (game.getPoints() / 1000) * 30;

        // Move enemy ships
        Iterator<Rectangle> i = enemies.iterator();
        while (i.hasNext()) {
            Rectangle enemy = i.next();
            enemy.x -= enemySpeed * Gdx.graphics.getDeltaTime();
            if (enemy.x + 64 < 0)
                i.remove();
            if (enemy.overlaps(ship)) {
                gameOver();
            }
        }

        // Move asteroids
        Iterator<Rectangle> a = asteroids.iterator();
        while (a.hasNext()) {
            Rectangle rock = a.next();
            rock.x -= 200 * Gdx.graphics.getDeltaTime();
            if (rock.x + 98 < 0)
                a.remove();
            if (rock.overlaps(ship)) {
                gameOver();
            }
        }

        // Create powerup
        if (TimeUtils.millis() - lastPowerUpTime > 20000)
            spawnPowerUp();

        // Move powerup and handle powerup detection
        if (powerUpVisible) {
            powerUp.x -= 500 * Gdx.graphics.getDeltaTime();
            if (ship.overlaps(powerUp)) {
                powerUp.x = game.screenWidth;
                powerUpVisible = false;
                powerUpActive = true;
                powerUpActiveTime = TimeUtils.millis();
            }
            if (powerUp.x + 34 < 0) {
                powerUp.x = game.screenWidth;
                powerUpVisible = false;
            }
        }

        // Create new laser
        if (powerUpActive) {
            if (TimeUtils.nanoTime() - lastLaserSpawnTime > 200000000)
                spawnLaser();
        } else {
            if (TimeUtils.nanoTime() - lastLaserSpawnTime > 700000000)
                spawnLaser();
        }

        // Remove powerup after 5 seconds of it being active
        if (powerUpActive && TimeUtils.millis() - powerUpActiveTime > 5000) {
            powerUpActive = false;
        }

        // Move lasers
        i = lasers.iterator();
        while (i.hasNext()) i.next().x += 900 * Gdx.graphics.getDeltaTime();
        for (Rectangle laser:lasers) {
            if (laser.x > game.screenWidth) {
                lasers.removeValue(laser, true);
                continue;
            }
            // Detect hit on enemy
            for (Rectangle enemy : enemies) {
                if (laser.overlaps(enemy)) {
                    enemies.removeValue(enemy, true);
                    game.setPoints(game.getPoints() + 100);
                    lasers.removeValue(laser, true);
                    break;
                }
            }
            // Detect hit on asteroid
            for (Rectangle rock : asteroids) {
                if (laser.overlaps(rock)) {
                    asteroids.removeValue(rock, true);
                    game.setPoints(game.getPoints() + 50);
                    lasers.removeValue(laser, true);
                    break;
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        shipImage.dispose();
        enemyImage.dispose();
        laserImage.dispose();
        backgroundImage1.dispose();
        backgroundImage2.dispose();
        powerUpImage.dispose();
        laserSound.dispose();
        asteroidImage.dispose();
    }
}
