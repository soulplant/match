package com.matchgame;

import java.util.List;

public class CyclicPhaseRunner {
  private final List<Phase> phases;
  private int currentPhase;

  public CyclicPhaseRunner(List<Phase> phases) {
    this.phases = phases;
    currentPhase = 0;
    phases.get(currentPhase).enter();
  }

  public void act(float delta) {
    Phase phase = phases.get(currentPhase);
    if (!phase.act(delta)) {
      currentPhase = (currentPhase + 1) % phases.size();
      phase.exit();
      phase = phases.get(currentPhase);
      phase.enter();
    }
  }
}
