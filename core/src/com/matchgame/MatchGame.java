package com.matchgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MatchGame extends ApplicationAdapter {
  enum State {
    INTRO,
    GAME,
  }

	SpriteBatch batch;
  private Stage stage;
  private final List<Texture> blockTextures = new ArrayList<Texture>();
  private IntroPhase introPhase;
  private GamePhase gamePhase;
  private DefaultBlockFactory blockFactory;
  State state = State.INTRO;

	@Override
	public void create () {
		batch = new SpriteBatch();
		for (int i = 0; i < 4; i++) {
		  blockTextures.add(new Texture("Block" + (i + 1) + ".png"));
		}
		blockFactory = new DefaultBlockFactory(new Random(), blockTextures);
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		introPhase = new IntroPhase(blockFactory, stage);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	private boolean act(float delta) {
	  switch (state) {
    case GAME:
      if (!gamePhase.act(delta)) {
        throw new NotImplementedException();
      }
      break;
    case INTRO:
      if (!introPhase.act(delta)) {
        state = State.GAME;
        gamePhase = new GamePhase(blockFactory, stage);
      }
      break;
	  }
    return true;
  }

  @Override
	public void dispose() {
	  stage.dispose();
	}
}
