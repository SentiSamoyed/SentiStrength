package web;

import common.SentiData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;
import uk.ac.wlv.sentistrength.SentiStrength;

import java.io.File;


@SpringBootApplication(scanBasePackages = {"web"})
public class SentiStrengthApplication {
  public static void main(String[] args) {
    if (args.length != 0 && "--web".equals(args[0])) {
      Assert.isTrue(args.length >= 2, "Usage: ~ --web <Path to SentiStrength_Data>");
      File f = new File(args[1]);
      Assert.isTrue(f.exists() && f.isDirectory(), "Invalid path of SentiStrength_Data: " + args[1]);
      SentiData.SENTI_DATA_DIR_PATH = args[1];

      SpringApplication.run(SentiStrengthApplication.class, args);
    } else {
      SentiStrength.main(args);
    }
  }
}
