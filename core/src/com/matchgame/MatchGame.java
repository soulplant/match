package com.matchgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MatchGame extends ApplicationAdapter {
	SpriteBatch batch;
  private Stage stage;
  private final List<Texture> blockTextures = new ArrayList<Texture>();

	@Override
	public void create () {
		batch = new SpriteBatch();
		for (int i = 0; i < 4; i++) {
		  blockTextures.add(new Texture("Block" + (i + 1) + ".png"));
		}
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		new BlockGrid(blockTextures, stage);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void dispose() {
	  stage.dispose();
	}
}
