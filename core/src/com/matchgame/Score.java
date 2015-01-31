package com.matchgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Sized by its parent, Score will center its child label within itself.
 */
public class Score extends Group {
  private final Label label;
  private final BitmapFont font;
  private int score;

  public Score(BitmapFont font) {
    this.font = font;
    Label.LabelStyle labelStyle = new Label.LabelStyle();
    labelStyle.font = font;
    labelStyle.fontColor = Color.BLACK;
    label = new Label("", labelStyle);
    addActor(label);
    updateView();
  }

  private void updateView() {
    String scoreString = score + "";
    label.setText(scoreString);
    float left = (getWidth() - label.getTextBounds().width) / 2;
    label.setX(left);
  }

  public void incrementBy(int count) {
    score += count;
    updateView();
  }

  @Override
  protected void sizeChanged() {
    updateView();
  }
}
