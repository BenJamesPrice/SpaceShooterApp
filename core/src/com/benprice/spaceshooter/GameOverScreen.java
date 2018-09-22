package com.benprice.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class GameOverScreen implements Screen {

    private final SpaceShooter game;
    private OrthographicCamera camera;
    private Texture playAgainImage;
    private Rectangle button;
    private boolean newHighScore;
    private Texture backgroundImage;
    private GlyphLayout layout;

    public GameOverScreen(final SpaceShooter game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.screenWidth, game.screenHeight);

        playAgainImage = new Texture(Gdx.files.internal("playAgainButton.png"));
        backgroundImage = new Texture(Gdx.files.internal("background.png"));
        layout = new GlyphLayout();

        button = new Rectangle();
        button.x = 235;
        button.y = 300;
        button.width = 330;
        button.height = 65;

        if (game.getPoints() > game.getHighScore()) {
            newHighScore = true;
            game.setHighScore(game.getPoints());
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw images and display score and high score
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(backgroundImage, 0, 0);

        layout.setText(game.largeFont, "Game Over! Score is " + game.getPoints());
        game.largeFont.draw(game.batch, layout, 400 - layout.width/2, 200);
        if (newHighScore) {
            layout.setText(game.largeFont, "New High Score!");
            game.largeFont.draw(game.batch, layout, 400 - layout.width/2, 250);
        }
        layout.setText(game.largeFont, "High Score is " + game.getHighScore());
        game.largeFont.draw(game.batch, layout, 400 - layout.width/2, 150);
        game.batch.draw(playAgainImage, button.x, button.y);
        game.batch.end();

        // Handle user input
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if(button.contains(touchPos.x, touchPos.y)) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        playAgainImage.dispose();
        backgroundImage.dispose();
    }
}
