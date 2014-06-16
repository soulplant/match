package com.matchgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

class Block extends Actor {
  private final int x;
  private final int y;
  private final Texture blockTexture;
  private float w;
  private float h;
  private final Delegate delegate;
  private boolean isSelected;
  private ShapeRenderer renderer;
  private final int colorIndex;

  public interface Delegate {
    void onDragStart(Block block);
    void onDragEnter(Block block);
    void onDragEnd(Block block);
    void onDead();
  }

  public Block(int x, int y, int colorIndex, Texture blockTexture, ShapeRenderer renderer,
      final Delegate dragController) {
    // Logical coordinates.
    this.x = x;
    this.y = y;
    this.colorIndex = colorIndex;
    this.blockTexture = blockTexture;
    this.delegate = dragController;
    this.renderer = renderer;

    w = 200f;
    h = 200f;

    setBounds(x * w, y * h, w, h);

    addListener(new InputListener() {
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer,
          int button) {
        dragController.onDragStart(Block.this);
        return true;
      }

      @Override
      public void touchUp(InputEvent event, float x, float y, int pointer,
          int button) {
        dragController.onDragEnd(Block.this);
      }

      @Override
      public void enter(InputEvent event, float x, float y, int pointer,
          Actor fromActor) {
        dragController.onDragEnter(Block.this);
      }
    });
  }

  public int getLogicalX() {
    return x;
  }

  public int getLogicalY() {
    return y;
  }

  public int getLogicalColor() {
    return colorIndex;
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    batch.setColor(getColor());
    batch.draw(blockTexture, getX(), getY(), getWidth(), getHeight());
    if (isSelected()) {
      batch.end();
      renderer.setTransformMatrix(batch.getTransformMatrix());
      renderer.setColor(getColor());
      renderer.begin(ShapeType.Filled);
      renderer.ellipse(x * w + w / 4f, y * h + h / 4f, w / 2f, h / 2f);
      renderer.end();
      batch.begin();
    }
  }

  private boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }

  public void die() {
    setSelected(false);
    setZIndex(17);
    setTouchable(Touchable.disabled);
    addAction(Actions.sequence(
        Actions.moveBy(0f, 100f, 0.2f, Interpolation.exp10Out), Actions.parallel(Actions.fadeOut(0.2f, Interpolation.fade)),
        new Action() {
          @Override
          public boolean act(float delta) {
            delegate.onDead();
            return true;
          }
        }, Actions.removeActor()));
  }

  public boolean isAdjacentTo(Block block) {
    int dx = Math.abs(getLogicalX() - block.getLogicalX());
    int dy = Math.abs(getLogicalY() - block.getLogicalY());
    return (dx + dy) == 1;
  }

  @Override
  public String toString() {
    return "Block[" + getLogicalX() + ", " + getLogicalY() + "]";
  }

  public boolean isAtSamePosition(Block block) {
    return getLogicalX() == block.getLogicalX() && getLogicalY() == block.getLogicalY();
  }
}