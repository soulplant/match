package com.matchgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.matchgame.Block.DragController;

public class BlockGrid implements DragController {
  private final Group group;

  public BlockGrid(Texture blockTexture, Stage stage) {
    group = new Group();
    Block block = null;
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        block = new Block(i, j, blockTexture, this);
        group.addActor(block);
      }
    }
    stage.addActor(group);
    float groupWidth = group.getChildren().get(0).getWidth() * 4f;
    float groupHeight = group.getChildren().get(0).getHeight() * 4f;

    float stageWidth = stage.getWidth();
    float stageHeight = stage.getHeight();

    float padding = (stageWidth - groupWidth) / 2;
    group.setPosition(padding, stageHeight - groupHeight - padding);
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
