package com.matchgame;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class BlockSelectionTest {

  private BlockSelection blockSelection;

  private Block block(int x, int y) {
    return block(x, y, 0);
  }

  private Block block(int x, int y, int color) {
    return new Block(x, y, color, null, null, null);
  }

  @Before
  public void setup() {
    blockSelection = new BlockSelection();
  }

  @Test
  public void canAddAnyBlockToInitialSelection() {
    Block block = block(10, 10);
    assertTrue(blockSelection.canAdd(block));
  }

  @Test
  public void cantAddNonAdjacentBlock() {
    Block block = block(10, 10);
    Block block2 = block(0, 0);
    blockSelection.addBlock(block);
    assertFalse(blockSelection.canAdd(block2));
  }

  @Test
  public void canAddAdjacentBlock() {
    blockSelection.addBlock(block(0, 0));
    assertTrue(blockSelection.canAdd(block(0, 1)));
  }

  @Test
  public void cantAddBlockOnTopOfCurrentBlock() {
    blockSelection.addBlock(block(0, 0));
    assertFalse(blockSelection.canAdd(block(0, 0)));
  }

  @Test
  public void cantAddBlockOnTopOfExistingBlock() {
    blockSelection.addBlock(block(0, 0));
    blockSelection.addBlock(block(0, 1));
    assertFalse(blockSelection.canAdd(block(0, 0)));
  }

  @Test
  public void cantAddBlockOfDifferingColor() {
    blockSelection.addBlock(block(0, 0, 0));
    assertFalse(blockSelection.canAdd(block(0, 1, 1)));
  }

  @Test
  public void canAddBlockOfSameColor() {
    blockSelection.addBlock(block(0, 0, 0));
    assertTrue(blockSelection.canAdd(block(0, 1, 0)));
  }
}
