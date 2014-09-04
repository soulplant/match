package com.matchgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MatchGame extends ApplicationAdapter {
  SpriteBatch batch;
  private Stage stage;
  private final List<Texture> blockTextures = new ArrayList<Texture>();
  private DefaultBlockFactory blockFactory;
  private CyclicPhaseRunner runner;
  private Map<String, Sound> sounds = new HashMap<String, Sound>();

  @Override
  public void create() {
    for (String note : Constants.noteSoundNames) {
      loadSound(note);
    }
    loadSound(Constants.failSoundName);
    FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
        Gdx.files.internal("JosefinSlab-Regular.ttf"));
    FreeTypeFontParameter param = new FreeTypeFontParameter();
    param.size = 150;
    BitmapFont mediumFont = gen.generateFont(param);
    param.size = 250;
    BitmapFont largeFont = gen.generateFont(param);
    param.size = 100;
    BitmapFont smallFont = gen.generateFont(param);
    gen.dispose();
    batch = new SpriteBatch();
    for (int i = 0; i < 4; i++) {
      blockTextures.add(new Texture("Block" + (i + 1) + ".png"));
    }
    blockFactory = new DefaultBlockFactory(new Random(), blockTextures);
    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage);
    List<Phase> phases = new ArrayList<Phase>();
    phases.add(new IntroPhase(blockFactory, stage, largeFont, smallFont, sounds));
    phases.add(new GamePhase(blockFactory, stage, mediumFont, sounds));
    runner = new CyclicPhaseRunner(phases);
  }

  private void loadSound(String note) {
    sounds.put(note, Gdx.audio.newSound(Gdx.files.internal(note + ".wav")));
  }

  private void scheduleSound(final Sound sound, float delay) {
    new Timer().scheduleTask(new Task() {
      @Override
      public void run() {
        System.out.println("about to play");
        long play = sound.play();
        System.out.println("play() result = " + play);
      }
    }, delay);
  }

  @Override
  public void render() {
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    runner.act(Gdx.graphics.getDeltaTime());
    stage.draw();
  }

  @Override
  public void dispose() {
    stage.dispose();
  }
}
