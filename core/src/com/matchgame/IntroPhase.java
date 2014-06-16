package com.matchgame;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class IntroPhase implements Block.Delegate {
  private final BlockFactory blockFactory;
  private final Stage stage;
  private boolean done = false;

  public IntroPhase(BlockFactory blockFactory, Stage stage) {
    this.blockFactory = blockFactory;
    this.stage = stage;

    Block block = blockFactory.createBlock(1, 1, this);

    stage.addActor(block);
    Util.centerActorInStage(block, stage);
  }

  public boolean act(float delta) {
    stage.act(delta);
    return !done;
  }

  @Override
  public void onDragStart(Block block) {
    block.setSelected(true);
  }

  @Override
  public void onDragEnter(Block block) {
    // Do nothing.
  }

  @Override
  public void onDragEnd(Block block) {
    block.die();
  }

  @Override
  public void onDead() {
    done = true;
  }
}
