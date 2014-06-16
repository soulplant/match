package com.matchgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;

public class IntroPhase implements Block.Delegate, Phase {
  private final BlockFactory blockFactory;
  private final Stage stage;
  private boolean done = false;
  private final List<Block> blocks = new ArrayList<Block>();
  private int currentVisibleBlock;

  public IntroPhase(BlockFactory blockFactory, Stage stage) {
    this.blockFactory = blockFactory;
    this.stage = stage;
  }

  @Override
  public void enter() {
    currentVisibleBlock = 0;
    // TODO(koz): Don't use a magic number here.
    for (int i = 0; i < 4; i++) {
      Block block = blockFactory.createColoredBlock(0, 0, i, this);
      blocks.add(block);
      stage.addActor(block);
      Util.centerActorInStage(block, stage);
      block.setVisible(currentVisibleBlock == i);
    }
    stage.addAction(Actions.forever(Actions.delay(0.075f, new Action() {
      @Override
      public boolean act(float delta) {
        showNextColorBlock();
        return true;
      }
    })));
  }

  private void showNextColorBlock() {
    Block block = blocks.get(currentVisibleBlock);
    block.setVisible(false);
    currentVisibleBlock = (currentVisibleBlock + 1) % blocks.size();
    blocks.get(currentVisibleBlock).setVisible(true);
  }

  @Override
  public void exit() {
    stage.clear();
    blocks.clear();
  }

  @Override
  public boolean act(float delta) {
    stage.act(delta);
    return !done;
  }

  @Override
  public void onDragStart(Block block) {
    stage.getRoot().clearActions();
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
