package com.matchgame;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class BlockGrid implements Block.Delegate {
  private final Group group;
  private BlockSelection selection = null;
  private final Random random;
  private final List<Texture> blockTextures;
  private int childrenLeft = 0;

  public BlockGrid(List<Texture> blockTextures, Stage stage) {
    this.blockTextures = blockTextures;
    random = new Random();

    group = new Group();
    createBlocks();
    stage.addActor(group);
    float groupWidth = group.getChildren().get(0).getWidth() * 4f;
    float groupHeight = group.getChildren().get(0).getHeight() * 4f;

    float stageWidth = stage.getWidth();
    float stageHeight = stage.getHeight();

    float padding = (stageWidth - groupWidth) / 2;
    group.setPosition(padding, stageHeight - groupHeight - padding);
  }

  private void createBlocks() {
    Block block;
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        int colorIndex = random.nextInt(blockTextures.size());
        block = new Block(i, j, colorIndex, blockTextures.get(colorIndex),
            new ShapeRenderer(), this);
        group.addActor(block);
      }
    }
    childrenLeft = 16;
  }

  // DragController.
  @Override
  public void onDragStart(Block block) {
    selection = new BlockSelection();
    selection.addBlock(block);
    block.setSelected(true);
  }

  @Override
  public void onDragEnter(Block block) {
    if (selection == null) {
      // Must be dragging from nowhere onto a block. We ignore this because that
      // means we won't catch the onDragEnd() event either. This is intended
      // behavior anyway, as it's weird for people to drag from somewhere else
      // onto their first block. Note this is also how the android lock screen
      // works.
      return;
    }
    if (selection.canAdd(block)) {
      selection.addBlock(block);
      block.setSelected(true);
    }
  }

  @Override
  public void onDragEnd(Block block) {
    for (Block b : selection.getBlocks()) {
      b.setSelected(false);
      b.die();
    }
    selection = null;
  }

  @Override
  public void onDead() {
    // We use a variable to keep track of this because we get notified before
    // the child is removed.
    childrenLeft--;
    if (childrenLeft == 0) {
      createBlocks();
    }
  }
}
