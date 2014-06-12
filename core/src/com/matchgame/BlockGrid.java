package com.matchgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.matchgame.Block.DragController;

public class BlockGrid implements DragController {
  private final Group group;
  private List<Block> currentSelection = null;

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
  public void onDragStart(Block block) {
    currentSelection = new ArrayList<Block>();
    currentSelection.add(block);
    block.setSelected(true);
    System.out.println("onDragStart(" + block.getLogicX() + ", " + block.getLogicY() + ")");
  }

  @Override
  public void onDragEnter(Block block) {
    currentSelection.add(block);
    block.setSelected(true);
    System.out.println("onDragEnter(" + block.getLogicX() + ", " + block.getLogicY() + ")");
  }

  @Override
  public void onDragEnd(Block block) {
    for (Block b : currentSelection) {
      b.setSelected(false);
    }
    System.out.println("onDragEnd(" + block.getLogicX() + ", " + block.getLogicY() + ")");
  }
}
