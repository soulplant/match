package com.matchgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jameskozianski on 1/31/15.
 */
public class Resources {
  private final BitmapFont mediumFont;
  private final BitmapFont largeFont;
  private final BitmapFont smallFont;
  private final BitmapFont reallySmallFont;
  private Map<String, Sound> sounds = new HashMap<String, Sound>();

  public Resources() {
    for (String note : Constants.noteSoundNames) {
      loadSound(note);
    }
    loadSound(Constants.failSoundName);
    FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
        Gdx.files.internal("JosefinSlab-Regular.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
    param.size = 150;
    mediumFont = gen.generateFont(param);
    param.size = 250;
    largeFont = gen.generateFont(param);
    param.size = 100;
    smallFont = gen.generateFont(param);

    param.size = 60;
    reallySmallFont = gen.generateFont(param);
    gen.dispose();
  }

  private void loadSound(String note) {
    sounds.put(note, Gdx.audio.newSound(Gdx.files.internal(note + ".wav")));
  }

  public BitmapFont getScoreFont() {
    return mediumFont;
  }

  public BitmapFont getMultiplierFont() {
    return reallySmallFont;
  }

  public BitmapFont getLabelFont() {
    return smallFont;
  }

  public BitmapFont getTitleFont() {
    return largeFont;
  }

  public Sound getSoundByName(String name) {
    return sounds.get(name);
  }
}
