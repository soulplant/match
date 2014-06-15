package com.matchgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockSelection {
  private final List<Block> blocks = new ArrayList<Block>();

  public BlockSelection() {

  }

  public void addBlock(Block block) {
    blocks.add(block);
  }

  public boolean canAdd(Block block) {
    if (blocks.isEmpty()) {
      return true;
    }
    if (containsBlock(block)) {
      return false;
    }
    Block lastAdded = blocks.get(blocks.size() - 1);
    if (lastAdded.getLogicalColor() != block.getLogicalColor()) {
      return false;
    }
    return blocks.get(blocks.size() - 1).isAdjacentTo(block);
  }

  private boolean containsBlock(Block block) {
    for (Block b : blocks) {
      if (b.isAtSamePosition(block)) {
        return true;
      }
    }
    return false;
  }

  public List<Block> getBlocks() {
    return Collections.unmodifiableList(blocks);
  }
}
