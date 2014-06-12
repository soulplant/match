package com.matchgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.matchgame.Block.DragController;

class Block extends Actor {
  private final int x;
  private final int y;
  private final Texture blockTexture;
  private float w;
  private float h;
  private final DragController dragController;

  public interface DragController {
    void onDragStart(float x, float y);
    void onDragEnter(float x, float y);
    void onDragEnd(float x, float y);
  }

  public Block(int x, int y, Texture blockTexture, final DragController dragController) {
    // Logical coordinates.
    this.x = x;
    this.y = y;
    this.blockTexture = blockTexture;
    this.dragController = dragController;

    w = blockTexture.getWidth() / 2.5f;
    h = blockTexture.getHeight() / 2.5f;

    System.out.println("Hi");

    setBounds(x * w, y * h, w, h);

    addListener(new InputListener() {
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer,
          int button) {
        log("touchDown");
        dragController.onDragStart(Block.this.x, Block.this.y);
        return true;
      }

      @Override
      public void touchUp(InputEvent event, float x, float y, int pointer,
          int button) {
        dragController.onDragEnd(Block.this.x, Block.this.y);
        log("touchUp");
      }

      @Override
      public void enter(InputEvent event, float x, float y, int pointer,
          Actor fromActor) {
        dragController.onDragEnter(Block.this.x, Block.this.y);
        log("enter");
      }
    });
  }

  private void log(String msg) {
//    System.out.println("Block[" + x + ", " + y + "]: " + msg);
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    batch.draw(blockTexture, x * w, y * h, w, h);
  }
}

public class BlockGrid implements DragController {
  private final Group group;

  public BlockGrid(Texture blockTexture, Stage stage) {
    group = new Group();
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        group.addActor(new Block(i, j, blockTexture, this));
      }
    }
    stage.addActor(group);
  }

  // DragController.
  @Override
  public void onDragStart(float x, float y) {
    System.out.println("onDragStart(" + x + ", " + y + ")");
  }

  @Override
  public void onDragEnter(float x, float y) {
    System.out.println("onDragEnter(" + x + ", " + y + ")");
  }

  @Override
  public void onDragEnd(float x, float y) {
    System.out.println("onDragEnd(" + x + ", " + y + ")");
  }
}
