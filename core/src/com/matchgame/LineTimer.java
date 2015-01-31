package com.matchgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Group;


public class LineTimer extends Group {
  private float duration;
  private float elapsed;
  private ShapeRenderer renderer = new ShapeRenderer();

  public LineTimer(float duration) {
    this.duration = duration;
    this.elapsed = 0f;
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);
    renderer.begin(ShapeType.Filled);
    float completed = Math.min(1f, elapsed / duration);
    if (completed < 0.5) {
      renderer.setColor(Color.GREEN);
    } else if (completed < 0.75) {
      renderer.setColor(Color.valueOf("F2D86D"));
    } else {
      renderer.setColor(Color.RED);
    }
    renderer.rect(getX(), getY(), getWidth() * (1 - completed), getHeight());
    renderer.end();
  }

  @Override
  public void act(float delta) {
    elapsed += delta;
  }

  public boolean isDone() {
    return elapsed >= duration;
  }
}
