package com.benprice.spaceshooter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SpaceShooter extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
	public BitmapFont largeFont;
	public final int screenHeight = 480;
	public final int screenWidth = 800;
	private int points = 0;
	private Preferences prefs;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("space.fnt"));
		largeFont = new BitmapFont(Gdx.files.internal("large.fnt"));

		if (prefs == null) {
			prefs = Gdx.app.getPreferences("SpaceShootPrefs");
		}
		if (!prefs.contains("highScore")) {
			prefs.putInteger("highScore", 0);
		}

		this.setScreen(new MenuScreen(this));
	}

	public int getPoints() {
		return this.points;
	}

	public void setPoints(int value) {
		this.points = value;
	}

	public int getHighScore() {
		return prefs.getInteger("highscore", 0);
	}

	public void setHighScore(int value) {
		prefs.putInteger("highscore", value);
		prefs.flush();
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		largeFont.dispose();
	}
}
