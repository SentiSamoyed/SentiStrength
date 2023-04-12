package uk.ac.wlv.sentistrength.wordlist;

import lombok.extern.log4j.Log4j2;
import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.utilities.FileOps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

@Log4j2
public abstract class WordList {

  protected record WordAndStrength(String word, int strength) {

  }

  public boolean initialise(String filename, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    int nrLines;
    try {
      if (Objects.isNull(filename) || filename.isBlank()) {
        throw new IllegalArgumentException("Invalid filename: " + filename);
      } else if (!(new File(filename)).exists()) {
        throw new IllegalArgumentException("Could not find file: " + filename);
      } else if ((nrLines = FileOps.i_CountLinesInTextFile(filename)) < 2) {
        throw new IllegalArgumentException("Less than 2 lines in file: " + filename);
      }
    } catch (IllegalArgumentException e) {
      log.fatal(e.getMessage());
      return false;
    }

    try (Stream<String> lines = FileOps.getFileStream(filename, options.bgForceUTF8)) {
      return initialise(lines, nrLines, options, extraBlankArrayEntriesToInclude);

    } catch (FileNotFoundException e) {
      log.fatal("Could not find file: " + filename);
      return false;
    } catch (IOException e) {
      log.fatal(e.getLocalizedMessage());
      return false;
    }
  }

  public boolean initialise(String sSourceFile, ClassificationOptions options) {
    return this.initialise(sSourceFile, options, 0);
  }

  protected String[] parseColumnsToStrings(String line) {
    int iFirstTabLocation = line.indexOf("\t");
    if (iFirstTabLocation < 0) {
      return null;
    }

    int iSecondTabLocation = line.indexOf("\t", iFirstTabLocation + 1);
    String second;
    if (iSecondTabLocation > 0) {
      second = line.substring(iFirstTabLocation + 1, iSecondTabLocation).trim();
    } else {
      second = line.substring(iFirstTabLocation + 1).trim();
    }

    line = line.substring(0, iFirstTabLocation);
    if (line.contains(" ")) {
      line = line.trim();
    }

    return new String[]{line, second};
  }

  protected WordAndStrength parseColumns(String line) {
    String[] ss = parseColumnsToStrings(line);
    line = ss[0];
    if (line.isBlank()) {
      return null;
    }

    int strength;
    try {
      strength = Integer.parseInt(ss[1]);
    } catch (NumberFormatException e) {
      log.fatal("Failed to identify integer weight! Ignoring word\nLine: " + line);
      strength = 0;
    }

    return new WordAndStrength(line, strength);
  }

  protected abstract boolean initialise(Stream<String> lines, int nrLines, ClassificationOptions options, int extraBlankArrayEntriesToInclude);
}
