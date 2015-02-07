package com.matchgame;

import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by jameskozianski on 2/7/15.
 */
public class HighscorePhase implements Phase {
  private final Stage stage;

  private LineTimer timer;

  public HighscorePhase(Stage stage) {
    this.stage = stage;
  }

  @Override
  public boolean act(float delta) {
    stage.act(delta);
    return !timer.isDone();
  }

  @Override
  public void enter() {
    timer = new LineTimer(2f);
    timer.setPosition(0, stage.getHeight() / 2);
    timer.setSize(stage.getWidth(), 8f);
    stage.addActor(timer);
  }

  @Override
  public void exit() {
    stage.clear();
  }
}
