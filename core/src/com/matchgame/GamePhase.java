package com.matchgame;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class GamePhase implements Block.Delegate, Phase {
  private final Group blockGroup;
  private BlockSelection selection = null;
  private int childrenLeft = 0;
  private final BlockFactory blockFactory;
  private final Stage stage;
  private Resources resources;
  private List<Block> longerSelection = null;
  private Score score;
  private LineTimer timer;
  private boolean isDone = false;

  public GamePhase(BlockFactory blockFactory, Stage stage, Resources resources) {
    this.blockFactory = blockFactory;
    this.stage = stage;
    this.resources = resources;
    blockGroup = new Group();
  }

  @Override
  public void enter() {
    selection = null;
    longerSelection = null;
    isDone = false;
    createBlocks();
    float groupWidthPx = blockGroup.getChildren().get(0).getWidth() * 4f;
    float groupHeightPx = blockGroup.getChildren().get(0).getHeight() * 4f;
    blockGroup.setSize(groupWidthPx, groupHeightPx);
    Util.centerActorInStage(blockGroup, stage);
    stage.addActor(blockGroup);

    score = new Score(resources);
    score.setBounds(0, stage.getHeight() - 200f, stage.getWidth(), 50f);
    stage.addActor(score);

    timer = new LineTimer(60f);
    stage.addActor(timer);
    timer.setPosition(blockGroup.getX(), stage.getHeight() - score.getHeight() - 220f);
    timer.setSize(blockGroup.getWidth(), 4f);

    final Action done = new Action() {
      @Override
      public boolean act(float delta) {
        isDone = true;
        return true;
      }
    };
    final Action pause = Actions.delay(3f);

    Action play = new Action() {
      @Override
      public boolean act(float delta) {
        if (longerSelection != null) {
          for (Block block : longerSelection) {
            block.setIndicating(true);
            stage.addAction(Actions.sequence(pause, done));
          }
          resources.getSoundByName("fail").play();
          score.addRemainingPoints();
          return true;
        }
        if (timer.isDone()) {
          score.addRemainingPoints();
          stage.addAction(Actions.sequence(pause, done));
          return true;
        }
        if (childrenLeft == 0) {
          createBlocks();
        }
        return false;
      }
    };
    stage.addAction(play);
  }

  @Override
  public void exit() {
    stage.clear();
    blockGroup.clear();
  }

  @Override
  public boolean act(float delta) {
    stage.act(delta);
    return !isDone;
  }

  // DragController.
  @Override
  public void onDragStart(Block block) {
    selection = new BlockSelection();
    addBlockToSelection(block);
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
      addBlockToSelection(block);
    }
  }

  @Override
  public void onDragEnd(Block block) {
    longerSelection = getLongerSelection(selection);
    if (longerSelection != null) {
      return;
    }
    for (Block b : selection.getBlocks()) {
      b.die();
    }
    score.incrementBy(selection.getBlocks().size());

    selection = null;
  }

  @Override
  public void onDead() {
    // We use a variable to keep track of this because we get notified before
    // the child is removed.
    childrenLeft--;
  }

  private void createBlocks() {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        blockGroup.addActor(blockFactory.createBlock(i, j, this));
      }
    }
    childrenLeft = 16;
  }

  private void addBlockToSelection(Block block) {
    selection.addBlock(block);
    block.setSelected(true);
    String note = Constants.noteSoundNames[Math.min(Constants.noteSoundNames.length - 1,
        selection.getBlocks().size() - 1)];
    resources.getSoundByName(note).play();
  }

  private List<Block> getLongerSelection(BlockSelection selection) {
    List<Block> longest = new ArrayList<Block>(selection.getBlocks());
    List<Block> allTouching = getAllTouching(selection.getBlocks().get(0));
    for (Block b : allTouching) {
      List<Block> candidate = getLongestPathFrom(b);
      if (candidate.size() > longest.size()) {
        longest = candidate;
      }
    }
    if (selection.size() == longest.size()) {
      return null;
    }
    return longest;
  }

  private List<Block> getLongestPathFrom(Block startingBlock) {
    List<Block> result = new ArrayList<Block>();
    result.add(startingBlock);
    return getLongestPathFromInner(result);
  }

  private List<Block> getLongestPathFromInner(List<Block> path) {
    Block block = path.get(path.size() - 1);
    List<Block> longest = new ArrayList<Block>(path);
    List<Block> adjacent = getAdjacent(block);
    for (Block a : adjacent) {
      if (path.contains(a)) {
        continue;
      }
      path.add(a);
      List<Block> candidate = getLongestPathFromInner(path);
      if (candidate.size() > longest.size()) {
        longest = new ArrayList<Block>(candidate);
      }
      path.remove(path.size() - 1);
    }
    return longest;
  }

  private List<Block> getAllTouching(Block block) {
    List<Block> result = new ArrayList<Block>();
    Queue<Block> candidates = new LinkedList<Block>();
    candidates.add(block);
    while (!candidates.isEmpty()) {
      Block candidate = candidates.remove();
      List<Block> adjacent = getAdjacent(candidate);
      for (Block b : adjacent) {
        if (!candidates.contains(b) && !result.contains(b)) {
          candidates.add(b);
        }
      }
      result.add(candidate);
    }
    return result;
  }

  private List<Block> getAdjacent(Block block) {
    int x = block.getLogicalX();
    int y = block.getLogicalY();
    List<Block> result = new ArrayList<Block>();
    addIfHasColour(block.getLogicalColor(), getBlockAt(x - 1, y), result);
    addIfHasColour(block.getLogicalColor(), getBlockAt(x + 1, y), result);
    addIfHasColour(block.getLogicalColor(), getBlockAt(x, y - 1), result);
    addIfHasColour(block.getLogicalColor(), getBlockAt(x, y + 1), result);
    return result;
  }

  private void addIfHasColour(int logicalColor, Block block, List<Block> result) {
    if (block != null && block.getLogicalColor() == logicalColor) {
      result.add(block);
    }
  }

  private Block getBlockAt(int x, int y) {
    for (Actor actor : blockGroup.getChildren()) {
      Block block = (Block) actor;
      if (block.getLogicalX() == x && block.getLogicalY() == y && !block.isDying()) {
        return block;
      }
    }
    return null;
  }
}
