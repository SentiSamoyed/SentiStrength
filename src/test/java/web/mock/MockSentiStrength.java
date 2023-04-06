package web.mock;

import uk.ac.wlv.sentistrength.SentiStrength;

import java.util.Random;

public class MockSentiStrength extends SentiStrength {
  @Override
  public String computeSentimentScores(String sentence) {
    Random random = new Random();
    int v1 = random.nextInt(1, 6);
    int v2 = random.nextInt(1, 6);
    int v3 = random.nextInt(1, 6);
    return "%d %d %d\nThis is a mocked result. Don't trust me."
        .formatted(v1, -v2, v3);
  }
}
