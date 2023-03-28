package uk.ac.wlv.wkaclass;
import java.io.BufferedReader;





import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import uk.ac.wlv.utilities.FileOps;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibLINEAR;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Utils;

public class PredictClass {
   private static String sgJarFolder = "C:/jars/";

   public static void main(String[] args) {
      boolean[] bArgumentRecognised = new boolean[args.length];
      String sSourceArff = "";
      String sUnclassifiedArff = "";
      String sClassifier = "smo";
      int iClassFor0 = 0;

      int i;
      for(i = 0; i < args.length; ++i) {
         bArgumentRecognised[i] = false;
      }

      for(i = 0; i < args.length; ++i) {
         if (args[i].equals("predict")) {
            bArgumentRecognised[i] = true;
         }

         if (i < args.length - 1) {
            if (args[i].equals("classified")) {
               sSourceArff = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equals("unclassified")) {
               sUnclassifiedArff = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equals("classifier")) {
               sClassifier = args[i + 1].toLowerCase();
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equals("zeros")) {
               iClassFor0 = Integer.parseInt(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }
         }
      }

      for(i = 0; i < args.length; ++i) {
         if (!bArgumentRecognised[i]) {
            System.out.println("Unrecognised command - wrong spelling or case?: " + args[i]);
            return;
         }
      }

      if (sSourceArff.length() > 1 && sUnclassifiedArff.length() > 1) {
         predictArffClass(sSourceArff, sClassifier, sUnclassifiedArff, iClassFor0);
      }

      System.out.println("Finished! Agressive feature reduction 100 features per 1k texts works best");
   }

   public static String predictArffClass(String arffTrainFile, String sClassifier, String sUnclassifiedArff, int iClassFor0) {
      Date start = Utilities.getNow();
      sClassifier = sClassifier.toLowerCase();
      int i = -1;
      String options = null;
      String sClassDefinition = "";
      String sResultsFile = "";
      Instances labeledData = null;
      Instances unlabeledData = null;

      Instances trainData;
      try {
         BufferedReader reader = new BufferedReader(new FileReader(arffTrainFile));
         trainData = new Instances(reader);
         reader.close();
         trainData.setClassIndex(trainData.numAttributes() - 1);
         sClassDefinition = trainData.attribute(trainData.numAttributes() - 1).toString();
      } catch (Exception var18) {
         System.out.println("Fatal error reading training data" + arffTrainFile + " " + var18.getMessage());
         return "";
      }

      try {
         unlabeledData = new Instances(new BufferedReader(new FileReader(sUnclassifiedArff)));
         unlabeledData.setClassIndex(unlabeledData.numAttributes() - 1);
         labeledData = new Instances(unlabeledData);
         labeledData.setClassIndex(labeledData.numAttributes() - 1);
      } catch (Exception var17) {
         System.out.println("Fatal error reading unlabelled instances " + sUnclassifiedArff + "\n" + var17.getMessage());
         return "";
      }

      double clsLabel;
//      int i;
      if (sClassifier.indexOf("smoreg") >= 0) {
         try {
            Utilities.printNameAndWarning("SMOreg");
            start = Utilities.getNow();
            System.out.println();
            ClassificationViaRegression schemeSMOreg = new ClassificationViaRegression();
            options = "-W weka.classifiers.functions.SMOreg -- -C 1.0 -N 0 -I \"weka.classifiers.functions.supportVector.RegSMOImproved -L 0.0010 -W 1 -P 1.0E-12 -T 0.0010 -V\" -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\"";
            schemeSMOreg.setOptions(Utils.splitOptions(options));
            schemeSMOreg.buildClassifier(trainData);

            for(i = 0; i < unlabeledData.numInstances(); ++i) {
               clsLabel = schemeSMOreg.classifyInstance(unlabeledData.instance(i));
               labeledData.instance(i).setClassValue(clsLabel);
            }

            System.out.println(Utilities.timeGap(start, Utilities.getNow()) + " taken");
         } catch (Exception var30) {
            System.out.println("Fatal error with SMOreg on " + arffTrainFile + " " + var30.getMessage());
         }
      } else if (sClassifier.indexOf("smo") >= 0) {
         Utilities.printNameAndWarning("SMO   ");
         start = Utilities.getNow();
         System.out.println();
         SMO schemeSMO = new SMO();
         options = "-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\"";

         try {
            schemeSMO.setOptions(Utils.splitOptions(options));
            schemeSMO.buildClassifier(trainData);
         } catch (Exception var16) {
            System.out.println("!!Fatal error loading SMO train " + arffTrainFile + " " + var16.getMessage());
         }

         try {
            for(i = 0; i < unlabeledData.numInstances(); ++i) {
               clsLabel = schemeSMO.classifyInstance(unlabeledData.instance(i));
               labeledData.instance(i).setClassValue(clsLabel);
            }

            System.out.println(Utilities.timeGap(start, Utilities.getNow()) + " taken");
         } catch (Exception var29) {
            System.out.println("!!Fatal error with loaded SMO train " + arffTrainFile + " applied to unclassified " + sUnclassifiedArff + " trying to label line " + i + ". " + var29.getMessage());
         }
      } else if (sClassifier.indexOf("slog") >= 0) {
         try {
            Utilities.printNameAndWarning("SLOG  ");
            start = Utilities.getNow();
            SimpleLogistic schemeSLOG = new SimpleLogistic();
            options = "-I 0 -M 500 -H 50 -W 0.0";
            schemeSLOG.setOptions(Utils.splitOptions(options));
            schemeSLOG.buildClassifier(trainData);

            for(i = 0; i < unlabeledData.numInstances(); ++i) {
               clsLabel = schemeSLOG.classifyInstance(unlabeledData.instance(i));
               labeledData.instance(i).setClassValue(clsLabel);
            }

            System.out.println(Utilities.timeGap(start, Utilities.getNow()) + " taken");
         } catch (Exception var28) {
            System.out.println("Fatal error with SLOG train " + arffTrainFile + " applied to unclassified " + sUnclassifiedArff + " " + var28.getMessage());
         }
      } else if (sClassifier.indexOf("liblin") >= 0) {
         try {
            Utilities.printNameAndWarning("LibLINEAR");
            start = Utilities.getNow();
            LibLINEAR schemeLibLINEAR = new LibLINEAR();
            options = " -i -o –t";
            schemeLibLINEAR.setOptions(Utils.splitOptions(options));
            schemeLibLINEAR.buildClassifier(trainData);

            for(i = 0; i < unlabeledData.numInstances(); ++i) {
               clsLabel = schemeLibLINEAR.classifyInstance(unlabeledData.instance(i));
               labeledData.instance(i).setClassValue(clsLabel);
            }

            System.out.println(Utilities.timeGap(start, Utilities.getNow()) + " taken");
         } catch (Exception var27) {
            System.out.println("Fatal error with LibLINEAR train " + arffTrainFile + " applied to unclassified " + sUnclassifiedArff + " " + var27.getMessage());
         }
      } else if (sClassifier.indexOf("bayes") >= 0) {
         try {
            Utilities.printNameAndWarning("BAYES ");
            start = Utilities.getNow();
            NaiveBayes schemeBayes = new NaiveBayes();
            schemeBayes.buildClassifier(trainData);

            for(i = 0; i < unlabeledData.numInstances(); ++i) {
               clsLabel = schemeBayes.classifyInstance(unlabeledData.instance(i));
               labeledData.instance(i).setClassValue(clsLabel);
            }

            System.out.println(Utilities.timeGap(start, Utilities.getNow()) + " taken");
         } catch (Exception var26) {
            System.out.println("Fatal error with BAYES train " + arffTrainFile + " applied to unclassified " + sUnclassifiedArff + " " + var26.getMessage());
         }
      } else if (sClassifier.indexOf("ada") >= 0) {
         try {
            Utilities.printNameAndWarning("ADA   ");
            start = Utilities.getNow();
            AdaBoostM1 schemeAda = new AdaBoostM1();
            options = "-P 100 -S 1 -I 10 -W weka.classifiers.trees.DecisionStump";
            schemeAda.setOptions(Utils.splitOptions(options));
            schemeAda.buildClassifier(trainData);

            for(i = 0; i < unlabeledData.numInstances(); ++i) {
               clsLabel = schemeAda.classifyInstance(unlabeledData.instance(i));
               labeledData.instance(i).setClassValue(clsLabel);
            }

            System.out.println(Utilities.timeGap(start, Utilities.getNow()) + " taken");
         } catch (Exception var25) {
            System.out.println("Fatal error with ADA train " + arffTrainFile + " applied to unclassified " + sUnclassifiedArff + " " + var25.getMessage());
         }
      } else if (sClassifier.indexOf("jrip") >= 0) {
         try {
            Utilities.printNameAndWarning("JRIP  ");
            start = Utilities.getNow();
            JRip schemeJrip = new JRip();
            options = "-F 3 -N 2.0 -O 2 -S 1";
            schemeJrip.setOptions(Utils.splitOptions(options));
            schemeJrip.buildClassifier(trainData);

            for(i = 0; i < unlabeledData.numInstances(); ++i) {
               clsLabel = schemeJrip.classifyInstance(unlabeledData.instance(i));
               labeledData.instance(i).setClassValue(clsLabel);
            }

            System.out.println(Utilities.timeGap(start, Utilities.getNow()) + " taken");
         } catch (Exception var24) {
            System.out.println("Fatal error with JRIP on " + arffTrainFile + " applied to unclassified " + sUnclassifiedArff + " " + var24.getMessage());
         }
      } else if (sClassifier.indexOf("dec") >= 0) {
         try {
            Utilities.printlnNameAndWarning("DEC   ");
            start = Utilities.getNow();
            DecisionTable schemeDec = new DecisionTable();
            options = "-X 1 -S \"weka.attributeSelection.BestFirst -D 1 -N 5\"";
            schemeDec.setOptions(Utils.splitOptions(options));
            schemeDec.buildClassifier(trainData);

            for(i = 0; i < unlabeledData.numInstances(); ++i) {
               clsLabel = schemeDec.classifyInstance(unlabeledData.instance(i));
               labeledData.instance(i).setClassValue(clsLabel);
            }

            System.out.println(Utilities.timeGap(start, Utilities.getNow()) + " taken");
         } catch (Exception var23) {
            System.out.println("Fatal error with DEC on " + arffTrainFile + " applied to unclassified " + sUnclassifiedArff + " " + var23.getMessage());
         }
      } else if (sClassifier.indexOf("j48") >= 0) {
         try {
            Utilities.printNameAndWarning("J48   ");
            start = Utilities.getNow();
            J48 schemeJ48 = new J48();
            options = "-C 0.25 -M 2";
            schemeJ48.setOptions(Utils.splitOptions(options));
            schemeJ48.buildClassifier(trainData);

            for(i = 0; i < unlabeledData.numInstances(); ++i) {
               clsLabel = schemeJ48.classifyInstance(unlabeledData.instance(i));
               labeledData.instance(i).setClassValue(clsLabel);
            }

            System.out.println(Utilities.timeGap(start, Utilities.getNow()) + " taken");
         } catch (Exception var22) {
            System.out.println("Fatal error with J48 train " + arffTrainFile + " applied to unclassified " + sUnclassifiedArff + " " + var22.getMessage());
         }
      } else if (sClassifier.indexOf("j48") >= 0) {
         try {
            Utilities.printlnNameAndWarning("MLP   ");
            start = Utilities.getNow();
            MultilayerPerceptron schemeMLP = new MultilayerPerceptron();
            options = "-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a";
            schemeMLP.setOptions(Utils.splitOptions(options));
            schemeMLP.buildClassifier(trainData);

            for(i = 0; i < unlabeledData.numInstances(); ++i) {
               clsLabel = schemeMLP.classifyInstance(unlabeledData.instance(i));
               labeledData.instance(i).setClassValue(clsLabel);
            }

            System.out.println(Utilities.timeGap(start, Utilities.getNow()) + " taken");
         } catch (Exception var21) {
            System.out.println("Fatal error with MLP on " + arffTrainFile + " applied to unclassified " + sUnclassifiedArff + " " + var21.getMessage());
         }
      } else if (sClassifier.indexOf("libsvm") >= 0) {
         try {
            Utilities.printNameAndWarning("LibSVM");
            start = Utilities.getNow();
            System.out.println();
            Utilities.addToClassPath(sgJarFolder + "libsvm.jar");
            LibSVM schemeLibSVM = new LibSVM();
            options = "-s 0";
            schemeLibSVM.setOptions(Utils.splitOptions(options));
            schemeLibSVM.buildClassifier(trainData);

            for(i = 0; i < unlabeledData.numInstances(); ++i) {
               clsLabel = schemeLibSVM.classifyInstance(unlabeledData.instance(i));
               labeledData.instance(i).setClassValue(clsLabel);
            }

            System.out.println(Utilities.timeGap(start, Utilities.getNow()) + " taken");
         } catch (Exception var20) {
            System.out.println("Fatal error with LibSVM on " + arffTrainFile);
            System.out.println("Must have jar file in Jar folder " + sgJarFolder + " or classpath. Here is the current Java classpath");
            Utilities.printClasspath();
         }
      } else {
         System.out.println("Unknown classifier: " + sClassifier + ". Name not case sensitive");
      }

      if (sClassDefinition.indexOf("{1,2,3,4,5}") > 0 && iClassFor0 != 0) {
         System.out.println("Warning! Unclassified texts changed from 0 to " + iClassFor0 + " for pos or neg data.");

         for(i = 0; i < unlabeledData.numInstances(); ++i) {
            if (labeledData.instance(i).classValue() == 0.0D) {
               labeledData.instance(i).setClassValue((double)iClassFor0);
            }
         }
      } else {
         System.out.println("Warning! Unclassified texts may remain as 0 in data.");
      }

      try {
         sResultsFile = FileOps.s_ChopFileNameExtension(sUnclassifiedArff) + "_classified.arff";
         BufferedWriter writer = new BufferedWriter(new FileWriter(sResultsFile));
         writer.write("@relation AllTerms\n");

         for(i = 0; i < labeledData.numAttributes(); ++i) {
            writer.write(labeledData.attribute(i).toString());
            writer.write("\n");
         }

         writer.write("@data\n");

         for(i = 0; i < labeledData.numInstances(); ++i) {
            writer.write(labeledData.instance(i).toString());
            writer.write("\n");
         }

         writer.close();
         return sResultsFile;
      } catch (Exception var19) {
         System.out.println("Error saving results" + arffTrainFile + " " + var19.getMessage());
         return "";
      }
   }
}
