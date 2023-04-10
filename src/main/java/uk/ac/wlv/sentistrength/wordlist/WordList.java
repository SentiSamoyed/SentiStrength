package uk.ac.wlv.sentistrength.wordlist;

import lombok.extern.log4j.Log4j2;
import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.utilities.FileOps;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    try (BufferedReader br = new BufferedReader(
        options.bgForceUTF8 ?
            new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8)
            : new FileReader(filename)
    )) {

      return initialise(br.lines(), nrLines, options, extraBlankArrayEntriesToInclude);

    } catch (FileNotFoundException e) {
      log.fatal("Could not find file: " + filename);
      return false;
    } catch (IOException e) {
      log.fatal(e.getLocalizedMessage());
      return false;
    }
  }

  protected WordAndStrength parseColumns(String line) {
    int iFirstTabLocation = line.indexOf("\t");
    if (iFirstTabLocation < 0) {
      return null;
    }

    int iSecondTabLocation = line.indexOf("\t", iFirstTabLocation + 1);
    int strength;
    try {
      if (iSecondTabLocation > 0) {
        strength = Integer.parseInt(line.substring(iFirstTabLocation + 1, iSecondTabLocation).trim());
      } else {
        strength = Integer.parseInt(line.substring(iFirstTabLocation + 1).trim());
      }
    } catch (NumberFormatException e) {
      log.fatal("Failed to identify integer weight! Ignoring word\nLine: " + line);
      strength = 0;
    }

    line = line.substring(0, iFirstTabLocation);
    if (line.contains(" ")) {
      line = line.trim();
    }

    if (line.isBlank()) {
      return null;
    }

    return new WordAndStrength(line, strength);
  }

  protected abstract boolean initialise(Stream<String> lines, int nrLines, ClassificationOptions options, int extraBlankArrayEntriesToInclude);
}
