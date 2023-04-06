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
      if (args.length < 2) {
        usageAndExit();
      }
      
      File f = new File(args[1]);
      if (!f.exists() || !f.isDirectory()) {
        System.err.println("Invalid path of SentiStrength_Data: " + args[1]);
        usageAndExit();
      }

      SentiData.SENTI_DATA_DIR_PATH = args[1];
      SpringApplication.run(SentiStrengthApplication.class, args);
    } else {
      SentiStrength.main(args);
    }
  }

  private static void usageAndExit() {
    System.err.println("Usage: ~ --web <Path to SentiStrength_Data>");
    System.exit(1);
  }
}
