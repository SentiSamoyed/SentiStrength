package mock;

import uk.ac.wlv.sentistrength.SentiStrength;
import web.data.vo.AnalysisOptionsVO;
import web.enums.AnalysisModeEnum;
import web.factory.SentiStrengthFactory;

public class MockSentiStrengthFactory implements SentiStrengthFactory {
  @Override
  public SentiStrength build(AnalysisModeEnum mode, Boolean explain, AnalysisOptionsVO options) {
    return new MockSentiStrength();
  }
}
