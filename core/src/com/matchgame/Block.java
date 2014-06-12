package com.matchgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

class Block extends Actor {
  private final int x;
  private final int y;
  private final Texture blockTexture;
  private float w;
  private float h;
  private final DragController dragController;
  private boolean isSelected;

  public interface DragController {
    void onDragStart(Block block);
    void onDragEnter(Block block);
    void onDragEnd(Block block);
  }

  public Block(int x, int y, Texture blockTexture, final DragController dragController) {
    // Logical coordinates.
    this.x = x;
    this.y = y;
    this.blockTexture = blockTexture;
    this.dragController = dragController;

    w = 200f;
    h = 200f;

    setBounds(x * w, y * h, w, h);

    addListener(new InputListener() {
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer,
          int button) {
        log("touchDown");
        dragController.onDragStart(Block.this);
        return true;
      }

      @Override
      public void touchUp(InputEvent event, float x, float y, int pointer,
          int button) {
        dragController.onDragEnd(Block.this);
        log("touchUp");
      }

      @Override
      public void enter(InputEvent event, float x, float y, int pointer,
          Actor fromActor) {
        dragController.onDragEnter(Block.this);
        log("enter");
      }
    });
  }

  public int getLogicX() {
    return x;
  }

  public int getLogicY() {
    return y;
  }

  private void log(String msg) {
//    System.out.println("Block[" + x + ", " + y + "]: " + msg);
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    batch.draw(blockTexture, x * w, y * h, w, h);
    if (isSelected()) {
      batch.end();
      ShapeRenderer renderer = new ShapeRenderer();
      renderer.setTransformMatrix(batch.getTransformMatrix());
      renderer.setColor(Color.GREEN);
      renderer.begin(ShapeType.Filled);
      renderer.ellipse(x * w + w / 4f, y * h + h / 4f, w / 2f, h / 2f);
      renderer.end();
      renderer.dispose();
      batch.begin();
    }
  }

  private boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }
}