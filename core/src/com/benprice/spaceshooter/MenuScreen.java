package com.benprice.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MenuScreen implements Screen {

    private final SpaceShooter game;
    private Rectangle startButton;
    private Rectangle quitButton;
    private OrthographicCamera camera;
    private Texture startImage;
    private Texture titleImage;
    private Texture quitImage;
    private Texture backgroundImage;

    public MenuScreen(final SpaceShooter game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.screenWidth, game.screenHeight);

        titleImage = new Texture(Gdx.files.internal("title.png"));
        startImage = new Texture(Gdx.files.internal("startButton.png"));
        quitImage = new Texture(Gdx.files.internal("quitButton.png"));
        backgroundImage = new Texture(Gdx.files.internal("background.png"));

        startButton = new Rectangle();
        startButton.x = 289;
        startButton.y = 200;
        startButton.width = 222;
        startButton.height = 39;

        quitButton = new Rectangle();
        quitButton.x = 289;
        quitButton.y = 130;
        quitButton.width = 222;
        quitButton.height = 39;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(backgroundImage, 0, 0);
        game.batch.draw(titleImage, 185, 290);
        game.batch.draw(startImage, startButton.x, startButton.y);
        game.batch.draw(quitImage, quitButton.x, quitButton.y);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if(startButton.contains(touchPos.x, touchPos.y)) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
            if(quitButton.contains(touchPos.x, touchPos.y)) {
                Gdx.app.exit();
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
        startImage.dispose();
        titleImage.dispose();
        quitImage.dispose();
        backgroundImage.dispose();
    }
}
