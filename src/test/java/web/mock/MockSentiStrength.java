package web.mock;

import uk.ac.wlv.sentistrength.SentiStrength;

import java.util.Random;

public class MockSentiStrength extends SentiStrength {
  @Override
  public SentimentScoreResult computeSentimentScoresSeparate(String sentence) {
    Random random = new Random();
    int v1 = random.nextInt(1, 6);
    int v2 = random.nextInt(1, 6);
    int v3 = random.nextInt(1, 6);
    return new SentiStrength.SentimentScoreResult(v1, v2, v3, v3, "This is a mocked result. Don't trust me.");
  }
}
