package com.matchgame;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class GamePhase implements Block.Delegate, Phase {
  private final Group group;
  private BlockSelection selection = null;
  private int childrenLeft = 0;
  private final BlockFactory blockFactory;
  private final Stage stage;
  private boolean invalidSelectionMade;
  private Label scoreLabel;
  private int score = 0;
  private Timer timer;
  private final BitmapFont font;

  public GamePhase(BlockFactory blockFactory, Stage stage, BitmapFont font) {
    this.blockFactory = blockFactory;
    this.stage = stage;
    this.font = font;
    group = new Group();
  }

  @Override
  public void enter() {
    score = 0;
    selection = null;
    invalidSelectionMade = false;
    createBlocks();
    stage.addActor(group);

    LabelStyle labelStyle = new LabelStyle();
    labelStyle.font = font;
    labelStyle.fontColor = new Color(0, 0, 0, 1);
    scoreLabel = new Label("0", labelStyle);
    stage.addActor(scoreLabel);

    System.out.println("label width = " + scoreLabel.getWidth());

    TextBounds bounds = font.getBounds("60.00");
    scoreLabel.setSize(bounds.width, bounds.height);
    scoreLabel.setPosition(stage.getWidth() - bounds.width - 200f, stage.getHeight() - bounds.height - 200f);

    timer = new Timer(font);
    System.out.println("timer width = " + timer.getWidth());
    stage.addActor(timer);
    timer.setPosition(200, stage.getHeight() - bounds.height - 200f);

    float groupWidth = group.getChildren().get(0).getWidth() * 4f;
    float groupHeight = group.getChildren().get(0).getHeight() * 4f;
    group.setSize(groupWidth, groupHeight);
    Util.centerActorInStage(group, stage);
    updateScoreText();
  }

  @Override
  public void exit() {
    stage.clear();
    group.clear();
  }

  @Override
  public boolean act(float delta) {
    stage.act(delta);
    if (invalidSelectionMade) {
      return false;
    }
    if (timer.isDone()) {
      return false;
    }
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
    if (!isValidSelection(selection)) {
      invalidSelectionMade = true;
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

  private boolean isValidSelection(BlockSelection selection) {
    List<Block> longest = null;
    List<Block> allTouching = getAllTouching(selection.getBlocks().get(0));
    for (Block b : allTouching) {
      List<Block> candidate = getLongestPathFrom(b);
      if (longest == null || candidate.size() > longest.size()) {
        longest = candidate;
      }
    }
    return selection.size() == longest.size();
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
    for (Actor actor : group.getChildren()) {
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
