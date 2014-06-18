package com.matchgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class Timer extends Group {
  private float timeLeft = 60.0f;
  private final Label label;

  public Timer(BitmapFont font) {
    LabelStyle style = new LabelStyle();
    style.font = font;
    style.fontColor = new Color(0, 0, 0, 1);
    label = new Label("60.00", style);
    setSize(label.getWidth(), label.getHeight());
    label.setStyle(style);
    updateText();
    addActor(label);
  }

  @Override
  public void act(float delta) {
    timeLeft -= delta;
    updateText();
  }

  private void updateText() {
    float ds = (timeLeft * 10) % 10;
    int decimals = (int) Math.floor(ds);
    label.setText((int) Math.floor(timeLeft) + "." + decimals);
  }

  public boolean isDone() {
    return timeLeft <= 0;
  }
}
