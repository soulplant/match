package com.matchgame;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class GamePhase implements Block.Delegate, Phase {
  private final Group blockGroup;
  private BlockSelection selection = null;
  private int childrenLeft = 0;
  private final BlockFactory blockFactory;
  private final Stage stage;
  private List<Block> longerSelection = null;
  private Label scoreLabel;
  private int score = 0;
  private Timer timer;
  private final BitmapFont font;
  private boolean isDone = false;
  private Map<String, Sound> sounds;

  public GamePhase(BlockFactory blockFactory, Stage stage, BitmapFont font,
      Map<String, Sound> sounds) {
    this.blockFactory = blockFactory;
    this.stage = stage;
    this.font = font;
    this.sounds = sounds;
    blockGroup = new Group();
  }

  @Override
  public void enter() {
    score = 0;
    selection = null;
    longerSelection = null;
    isDone = false;
    createBlocks();
    float groupWidth = blockGroup.getChildren().get(0).getWidth() * 4f;
    float groupHeight = blockGroup.getChildren().get(0).getHeight() * 4f;
    blockGroup.setSize(groupWidth, groupHeight);
    Util.centerActorInStage(blockGroup, stage);
    stage.addActor(blockGroup);

    LabelStyle labelStyle = new LabelStyle();
    labelStyle.font = font;
    labelStyle.fontColor = Color.BLACK;
    scoreLabel = new Label("60.0", labelStyle);
    stage.addActor(scoreLabel);
    scoreLabel.setPosition(blockGroup.getRight() - scoreLabel.getWidth(),
        stage.getHeight() - scoreLabel.getHeight() - 200f);

    timer = new Timer(font);
    stage.addActor(timer);
    timer.setPosition(blockGroup.getX(), stage.getHeight() - scoreLabel.getHeight() - 200f);
    updateScoreText();

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
          sounds.get("fail").play();
          return true;
        }
        if (timer.isDone()) {
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

  private void createBlocks() {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        blockGroup.addActor(blockFactory.createBlock(i, j, this));
      }
    }
    childrenLeft = 16;
  }

  // DragController.
  @Override
  public void onDragStart(Block block) {
    selection = new BlockSelection();
    addBlockToSelection(block);
  }

  private void addBlockToSelection(Block block) {
    selection.addBlock(block);
    block.setSelected(true);
    String note = Constants.noteSoundNames[Math.min(Constants.noteSoundNames.length - 1,
        selection.getBlocks().size() - 1)];
    sounds.get(note).play();
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
      score++;
    }

    updateScoreText();
    selection = null;
  }

  private void updateScoreText() {
    scoreLabel.setText(score + "");
  }

  private List<Block> getLongerSelection(BlockSelection selection) {
    List<Block> longest = null;
    List<Block> allTouching = getAllTouching(selection.getBlocks().get(0));
    for (Block b : allTouching) {
      List<Block> candidate = getLongestPathFrom(b);
      if (longest == null || candidate.size() > longest.size()) {
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
      if (longest == null || candidate.size() > longest.size()) {
        longest = new ArrayList<Block>(candidate);
      }
      path.remove(path.size() - 1);
    }
    return longest;
  }

  private List<Block> getAllTouching(Block block) {
    List<Block> result = new ArrayList<Block>();
    Queue<Block> candidates = new ArrayDeque<Block>();
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

  @Override
  public void onDead() {
    // We use a variable to keep track of this because we get notified before
    // the child is removed.
    childrenLeft--;
  }
}
