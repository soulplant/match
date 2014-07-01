package com.matchgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class IntroPhase implements Block.Delegate, Phase {
  private final BlockFactory blockFactory;
  private final Stage stage;
  private boolean done = false;
  private final List<Block> blocks = new ArrayList<Block>();
  private int currentVisibleBlock;
  private final BitmapFont smallFont;
  private final BitmapFont largeFont;

  public IntroPhase(BlockFactory blockFactory, Stage stage,
      BitmapFont largeFont, BitmapFont smallFont) {
    this.blockFactory = blockFactory;
    this.stage = stage;
    this.largeFont = largeFont;
    this.smallFont = smallFont;
  }

  @Override
  public void enter() {
    currentVisibleBlock = 0;
    done = false;
    // TODO(koz): Don't use a magic number here.
    for (int i = 0; i < 4; i++) {
      Block block = blockFactory.createColoredBlock(0, 0, i, this);
      blocks.add(block);
      stage.addActor(block);
      Util.centerActorInStage(block, stage);
      block.setVisible(currentVisibleBlock == i);
    }

    // Button label.
    Label buttonLabel = new Label("Play", new LabelStyle(smallFont, Color.BLACK));
    float bottom = blocks.get(0).getY();
    float padding = 40;
    Util.centerActorInStage(buttonLabel, stage);
    buttonLabel.setY(bottom - buttonLabel.getHeight() - padding);
    stage.addActor(buttonLabel);

    // Title label.
    Label titleLabel = new Label("Match", new LabelStyle(largeFont, Color.BLACK));
    Util.centerActorInStage(titleLabel, stage);
    titleLabel.setY(stage.getHeight() - titleLabel.getHeight() - 4 * padding);
    stage.addActor(titleLabel);

    stage.addAction(Actions.forever(Actions.delay(0.075f, new Action() {
      @Override
      public boolean act(float delta) {
        showNextColorBlock();
        return true;
      }
    })));
  }

  @Override
  public void exit() {
    stage.clear();
    blocks.clear();
  }

  private void showNextColorBlock() {
    Block block = blocks.get(currentVisibleBlock);
    block.setVisible(false);
    currentVisibleBlock = (currentVisibleBlock + 1) % blocks.size();
    blocks.get(currentVisibleBlock).setVisible(true);
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
