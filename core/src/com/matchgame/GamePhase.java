package com.matchgame;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class GamePhase implements Block.Delegate, Phase {
  private final Group blockGroup;
  private BlockSelection selection = null;
  private int childrenLeft = 0;
  private final BlockFactory blockFactory;
  private final Stage stage;
  private Resources resources;
  private Random random;
  private List<Block> longerSelection = null;
  private Score score;
  private LineTimer timer;
  private boolean isDone = false;

  public GamePhase(BlockFactory blockFactory, Stage stage, Resources resources, Random random) {
    this.blockFactory = blockFactory;
    this.stage = stage;
    this.resources = resources;
    this.random = random;
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
    final Action finished = Actions.sequence(new Action() {
      @Override
      public boolean act(float delta) {
        score.addRemainingPoints();
        for (Block block : getBlocks()) {
          block.lock();
        }
        return true;
      }
    }, pause, done);

    Action play = new Action() {
      @Override
      public boolean act(float delta) {
        if (longerSelection != null) {
          for (Block block : longerSelection) {
            block.setIndicating(true);
            stage.addAction(Actions.sequence(pause, done));
          }
          resources.getSoundByName("fail").play();
          stage.addAction(finished);
          return true;
        }
        if (timer.isDone()) {
          stage.addAction(finished);
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

  private List<Block> getBlocks() {
    ArrayList<Block> blocks = new ArrayList<Block>();
    for (Actor child : blockGroup.getChildren()) {
      blocks.add((Block) child);
    }
    return blocks;
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
    score.incrementBy(selection.size());
    if (selection.size() >= 4) {
      // Consider:
      // replaceAllSinglesWithColor(selection.getLogicalColor());
      switch (selection.getLogicalColor()) {
        case Constants.COLOR_RED:
          replaceAllSinglesWithColor(Constants.COLOR_RED);
          break;
        case Constants.COLOR_GRAY:
          fillAdjacentBlocksWith(Constants.COLOR_GRAY);
          break;
        case Constants.COLOR_GREEN:
          fillEmptySpacesWith(getRandomColor());
          break;
        case Constants.COLOR_CYAN:
          replaceSelectionWith(getRandomColor());
          break;
      }
    }

    selection = null;
  }

  private void replaceSelectionWith(int color) {
    for (Block block : selection.getBlocks()) {
      addBlock(blockFactory.createColoredBlock(block.getLogicalX(), block.getLogicalY(), color, this));
    }
  }

  private int getRandomColor() {
    return random.nextInt(Constants.COLOR_COUNT);
  }

  private void fillEmptySpacesWith(int color) {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        if (getBlockAt(i, j) == null) {
          addBlock(blockFactory.createColoredBlock(i, j, color, this));
        }
      }
    }
  }

  private void addBlock(Block block) {
    blockGroup.addActor(block);
    this.childrenLeft++;
  }

  private void fillAdjacentBlocksWith(int color) {
    Set<Block> adjacentBlocks = new HashSet<Block>();
    for (Block block : selection.getBlocks()) {
      adjacentBlocks.addAll(getAdjacent(block));
    }

    Set<Block> fillAdjacentBlocks = new HashSet<Block>();
    for (Block block : adjacentBlocks) {
      fillAdjacentBlocks.addAll(getAllTouching(block));
    }
    changeColor(fillAdjacentBlocks, color);
  }

  private void changeColor(Collection<Block> blocks, int color) {
    for (Block block : blocks) {
      if (block.getLogicalColor() != color) {
        Block newBlock = blockFactory.createColoredBlock(
            block.getLogicalX(), block.getLogicalY(), color, this);
        replaceBlock(block, newBlock);
      }
    }
  }

  private void replaceAllSinglesWithColor(int colorIndex) {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        Block block = getBlockAt(i, j);
        if (block == null) {
          continue;
        }
        if (isSingle(block)) {
          Block newBlock = blockFactory.createColoredBlock(i, j, colorIndex, this);
          replaceBlock(block, newBlock);
        }
      }
    }
  }

  private void replaceBlock(Block oldBlock, Block newBlock) {
    blockGroup.addActorAfter(oldBlock, newBlock);
    blockGroup.removeActor(oldBlock);
  }

  private boolean isSingle(Block block) {
    for (Block neighbor : getAdjacentWithSameColor(block)) {
      if (neighbor.getLogicalColor() == block.getLogicalColor()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void onDead() {
    // We use a variable to keep track of this because we get notified before
    // the child is removed.
    childrenLeft--;
  }

  private void createBlocks() {
    childrenLeft = 0;
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        addBlock(blockFactory.createBlock(i, j, this));
      }
    }
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
    List<Block> allTouching = removeDying(getAllTouching(selection.getBlocks().get(0)));
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

  private List<Block> removeDying(List<Block> blocks) {
    List<Block> result = new ArrayList<Block>();
    for (Block b : blocks) {
      if (!b.isDying()) {
        result.add(b);
      }
    }
    return result;
  }

  private List<Block> getLongestPathFrom(Block startingBlock) {
    List<Block> result = new ArrayList<Block>();
    result.add(startingBlock);
    return getLongestPathFromInner(result);
  }

  private List<Block> getLongestPathFromInner(List<Block> path) {
    Block block = path.get(path.size() - 1);
    List<Block> longest = new ArrayList<Block>(path);
    List<Block> adjacent = removeDying(getAdjacentWithSameColor(block));
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
      List<Block> adjacent = getAdjacentWithSameColor(candidate);
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
    addIfNotNull(getBlockAt(x - 1, y), result);
    addIfNotNull(getBlockAt(x + 1, y), result);
    addIfNotNull(getBlockAt(x, y - 1), result);
    addIfNotNull(getBlockAt(x, y + 1), result);
    return result;
  }

  private List<Block> getAdjacentWithSameColor(Block block) {
    List<Block> result = new ArrayList<Block>();
    for (Block adjacentBlock : getAdjacent(block)) {
      if (adjacentBlock.getLogicalColor() == block.getLogicalColor()) {
        result.add(adjacentBlock);
      }
    }
    return result;
  }

  private void addIfNotNull(Block block, List<Block> result) {
    if (block != null) {
      result.add(block);
    }
  }

  private Block getBlockAt(int x, int y) {
    return getBlockAtInner(x, y, true);
  }

  private Block getBlockAtInner(int x, int y, boolean allowDead) {
    for (Actor actor : blockGroup.getChildren()) {
      Block block = (Block) actor;
      if (block.getLogicalX() == x && block.getLogicalY() == y && (allowDead || !block.isDying())) {
        return block;
      }
    }
    return null;
  }
}
