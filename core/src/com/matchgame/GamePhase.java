package com.matchgame;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GamePhase implements Block.Delegate, Phase {
  private final Group group;
  private BlockSelection selection = null;
  private int childrenLeft = 0;
  private final BlockFactory blockFactory;
  private final Stage stage;

  public GamePhase(BlockFactory blockFactory, Stage stage) {
    this.blockFactory = blockFactory;
    this.stage = stage;
    group = new Group();
  }

  @Override
  public void enter() {
    createBlocks();
    stage.addActor(group);

    float groupWidth = group.getChildren().get(0).getWidth() * 4f;
    float groupHeight = group.getChildren().get(0).getHeight() * 4f;
    group.setSize(groupWidth, groupHeight);
    Util.centerActorInStage(group, stage);
  }

  @Override
  public void exit() {
    stage.clear();
  }

  @Override
  public boolean act(float delta) {
    stage.act(delta);
    if (childrenLeft == 0) {
      createBlocks();
    }
    return true;
  }

  private void createBlocks() {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        group.addActor(blockFactory.createBlock(i, j, this));
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
      b.die();
    }
    selection = null;
  }

  @Override
  public void onDead() {
    // We use a variable to keep track of this because we get notified before
    // the child is removed.
    childrenLeft--;
  }
}
