package com.matchgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

/**
 * Sized by its parent, Score will center its child scoreLabel within itself.
 */
public class Score extends Group {
  private final Label scoreLabel;
  private final Label multiplierLabel;
  private int score;
  private int lastIncrement;
  private Resources resources;

  public Score(Resources resources) {
    this.resources = resources;
    scoreLabel = makeLabel(resources.getScoreFont(), Color.BLACK);
    multiplierLabel = makeLabel(resources.getMultiplierFont(), Color.BLACK);
    addActor(scoreLabel);
    addActor(multiplierLabel);
    updateView();
  }

  private static Label makeLabel(BitmapFont font, Color color) {
    LabelStyle labelStyle = new LabelStyle();
    labelStyle.font = font;
    labelStyle.fontColor = color;
    return new Label("", labelStyle);
  }

  private void updateView() {
    String scoreString = score + "";
    scoreLabel.setText(scoreString);
    float left = (getWidth() - scoreLabel.getTextBounds().width) / 2;
    scoreLabel.setX(left);

    if (lastIncrement > 0) {
      multiplierLabel.setText(lastIncrement + "");
      multiplierLabel.setPosition(scoreLabel.getX() + scoreLabel.getTextBounds().width + 20f,
          scoreLabel.getY() + 50f);
    } else {
      multiplierLabel.setText("");
    }
  }

  public void incrementBy(int count) {
    if (count == 1) {
      score += lastIncrement + 1;
      lastIncrement = 0;
    } else if (lastIncrement == 0) {
      lastIncrement = count;
    } else {
      int points = lastIncrement * count;
      lastIncrement = 0;
      score += points;
    }
    updateView();
  }

  public void addRemainingPoints() {
    score += lastIncrement;
    lastIncrement = 0;
    updateView();
  }

  @Override
  protected void sizeChanged() {
    updateView();
  }
}
