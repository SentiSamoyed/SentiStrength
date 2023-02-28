// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   WekaCrossValidateInfoGain.java

package uk.ac.wlv.wkaclass;

import java.io.*;
import java.util.Date;
import java.util.Random;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.*;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.instance.Randomize;

// Referenced classes of package uk.ac.wlv.wkaclass:
//            Utilities

public class WekaCrossValidateInfoGain
{

    private static String sgJarFolder = "C:/jars/";

    public WekaCrossValidateInfoGain()
    {
    }

    public static void main(String args[])
        throws Exception
    {
        boolean bArgumentRecognised[] = new boolean[args.length];
        boolean lessFeaturesRequestedThanData = true;
        String classifierName = ",,all,";
        String classifierExclude = "";
        String addToClasspath = "";
        String instructionFilename = "-";
        String inputArffFilename = "-";
        String resultsFileName = "-";
        String summaryResultsFileName = "-";
        int minFeatures = 100;
        int maxFeatures = 1000;
        int stepFeatures = 100;
        int iterations = 30;
        Random random = new Random();
        for(int i = 0; i < args.length; i++)
            bArgumentRecognised[i] = false;

        for(int i = 0; i < args.length; i++)
        {
            if(args[i].equals("infogain"))
                bArgumentRecognised[i] = true;
            if(i < args.length - 1)
            {
                if(args[i].equals("min"))
                {
                    minFeatures = Integer.parseInt(args[i + 1].trim());
                    bArgumentRecognised[i] = true;
                    bArgumentRecognised[i + 1] = true;
                }
                if(args[i].equals("max"))
                {
                    maxFeatures = Integer.parseInt(args[i + 1].trim());
                    bArgumentRecognised[i] = true;
                    bArgumentRecognised[i + 1] = true;
                }
                if(args[i].equals("step"))
                {
                    stepFeatures = Integer.parseInt(args[i + 1].trim());
                    bArgumentRecognised[i] = true;
                    bArgumentRecognised[i + 1] = true;
                }
                if(args[i].equals("iterations"))
                {
                    iterations = Integer.parseInt(args[i + 1].trim());
                    bArgumentRecognised[i] = true;
                    bArgumentRecognised[i + 1] = true;
                }
                if(args[i].equals("classifier"))
                {
                    classifierName = (new StringBuilder(",,")).append(args[i + 1].toLowerCase()).append(",").toString();
                    bArgumentRecognised[i] = true;
                    bArgumentRecognised[i + 1] = true;
                }
                if(args[i].equals("exclude"))
                {
                    classifierExclude = args[i + 1];
                    bArgumentRecognised[i] = true;
                    bArgumentRecognised[i + 1] = true;
                }
                if(args[i].equals("instructions"))
                {
                    instructionFilename = args[i + 1];
                    bArgumentRecognised[i] = true;
                    bArgumentRecognised[i + 1] = true;
                }
                if(args[i].equals("input"))
                {
                    inputArffFilename = args[i + 1];
                    bArgumentRecognised[i] = true;
                    bArgumentRecognised[i + 1] = true;
                }
                if(args[i].equals("results"))
                {
                    resultsFileName = args[i + 1];
                    bArgumentRecognised[i] = true;
                    bArgumentRecognised[i + 1] = true;
                }
                if(args[i].equals("summary"))
                {
                    summaryResultsFileName = args[i + 1];
                    bArgumentRecognised[i] = true;
                    bArgumentRecognised[i + 1] = true;
                }
                if(args[i].equals("addToClasspath"))
                {
                    addToClasspath = args[i + 1];
                    Utilities.addToClassPath(addToClasspath);
                    bArgumentRecognised[i] = true;
                    bArgumentRecognised[i + 1] = true;
                }
            }
        }

        for(int i = 0; i < args.length; i++)
            if(!bArgumentRecognised[i])
                System.out.println((new StringBuilder("Unrecognised command - wrong spelling or case?: ")).append(args[i]).toString());

        ReportParameters(args, minFeatures, maxFeatures, stepFeatures, iterations, classifierName, classifierExclude, addToClasspath, instructionFilename, inputArffFilename, resultsFileName, summaryResultsFileName);
        if(instructionFilename.equals("-"))
        {
            if(inputArffFilename.equals("-") || resultsFileName.equals("-") || summaryResultsFileName.equals("-"))
            {
                System.out.println("Must specify instructions file or input, results and summary files. Giving up.");
                return;
            }
            System.out.println((new StringBuilder("started processing ")).append(inputArffFilename).toString());
            for(int features = minFeatures; features <= maxFeatures; features += stepFeatures)
            {
                System.out.println((new StringBuilder("Starting ")).append(features).append(" features for ").append(inputArffFilename).toString());
                for(int i = 1; i <= iterations; i++)
                {
                    int randomSeed = random.nextInt();
                    lessFeaturesRequestedThanData = classifyArff(inputArffFilename, classifierName, classifierExclude, randomSeed, features, resultsFileName, summaryResultsFileName);
                    System.out.println((new StringBuilder(String.valueOf(i))).append("/").append(iterations).append(" iterations done").toString());
                }

                if(!lessFeaturesRequestedThanData)
                {
                    System.out.println("More features requested than in data last time!");
                    return;
                }
            }

            System.out.println("For correlations, use Windows SentiStrength, Data Mining|Summarise Average... menu item");
        } else
        {
            File f = new File(instructionFilename);
            if(f.exists())
            {
                BufferedReader reader;
                for(reader = new BufferedReader(new FileReader(instructionFilename)); reader.ready();)
                {
                    inputArffFilename = reader.readLine();
                    resultsFileName = reader.readLine();
                    summaryResultsFileName = reader.readLine();
                    System.out.println((new StringBuilder("started processing ")).append(inputArffFilename).toString());
                    for(int features = minFeatures; features <= maxFeatures; features += stepFeatures)
                    {
                        System.out.println((new StringBuilder("Starting ")).append(features).append(" features for ").append(inputArffFilename).toString());
                        for(int i = 1; i <= iterations + 1; i++)
                        {
                            int randomSeed = random.nextInt();
                            lessFeaturesRequestedThanData = classifyArff(inputArffFilename, classifierName, classifierExclude, randomSeed, features, resultsFileName, summaryResultsFileName);
                            System.out.println((new StringBuilder(String.valueOf(i))).append("/").append(iterations).append(" 10-fold iterations with ").append(features).append(" features done").toString());
                        }

                        if(!lessFeaturesRequestedThanData)
                        {
                            System.out.println("More features requested than in data last time!");
                            reader.close();
                            return;
                        }
                    }

                }

                reader.close();
            } else
            {
                System.out.println((new StringBuilder(String.valueOf(instructionFilename))).append(" not found - should contain data file, results file, summary file.").toString());
                System.out.println("Program terminating without running analysis.");
            }
        }
    }

    public static void ReportParameters(String args[], int minFeatures, int maxFeatures, int stepFeatures, int iterations, String classifierName, String classifierExclude, String addToClasspath, 
            String instructionFilename, String inputFilename, String resultsFileName, String summaryResultsFileName)
    {
        System.out.println("InfoGain method: default options or values set by command line:");
        System.out.print((new StringBuilder(" ")).append(minFeatures).append(" [min] features, ").toString());
        System.out.print((new StringBuilder(String.valueOf(maxFeatures))).append(" [max] features, ").toString());
        System.out.println((new StringBuilder(String.valueOf(stepFeatures))).append(" [step] features").toString());
        System.out.println((new StringBuilder(" ")).append(iterations).append(" [iterations]").toString());
        System.out.println((new StringBuilder(" ")).append(classifierName).append(" [classifier] LibSVM/LibLin/ALL = SMO/SLOG/BAYES/ADA/SMOreg/JRIP/DEC/J48").toString());
        System.out.println((new StringBuilder(" ")).append(classifierExclude).append(" [classifierExclude] SMO/SLOG/BAYES/ADA/SMOreg/JRIP/DEC/J48").toString());
        System.out.println((new StringBuilder(" ")).append(instructionFilename).append(" [instructions] file (train-eval-results triples)").toString());
        System.out.println((new StringBuilder(" ")).append(inputFilename).append(" [input] ARFF file").toString());
        System.out.println((new StringBuilder(" ")).append(resultsFileName).append(" [results] file").toString());
        System.out.println((new StringBuilder(" ")).append(summaryResultsFileName).append(" [summary] results file (just accuracy)").toString());
        System.out.println((new StringBuilder(" ")).append(addToClasspath).append(" [addToClasspath] file to add to classpath").toString());
        System.out.println("");
    }

    public static boolean classifyArff(String arffFileName, String classifierName, String classifierExclude, int randomSeed, int features, String allResultsFilename, String summaryResultsFilename)
        throws Exception
    {
        boolean lessFeaturesRequestedThanData = true;
        Date start = Utilities.getNow();
        System.out.print("Loading data ... ");
        BufferedReader reader = new BufferedReader(new FileReader(arffFileName));
        Instances data = new Instances(reader);
        reader.close();
        data.setClassIndex(data.numAttributes() - 1);
        Randomize randomize = new Randomize();
        randomize.setRandomSeed(randomSeed);
        randomize.setInputFormat(data);
        data = Filter.useFilter(data, randomize);
        Ranker ranker = new Ranker();
        if(features < data.numAttributes())
        {
            ranker.setNumToSelect(features);
        } else
        {
            ranker.setNumToSelect(data.numAttributes());
            lessFeaturesRequestedThanData = false;
        }
        ranker.setGenerateRanking(true);
        ranker.setThreshold(1.7976931348623157E+308D);
        InfoGainAttributeEval infoGainAttributeEval = new InfoGainAttributeEval();
        AttributeSelection attributeSelection = new AttributeSelection();
        attributeSelection.setEvaluator(infoGainAttributeEval);
        attributeSelection.setSearch(ranker);
        attributeSelection.setInputFormat(data);
        data = Filter.useFilter(data, attributeSelection);
        String options = null;
        System.out.println((new StringBuilder(String.valueOf(Utilities.timeGap(start, Utilities.getNow())))).append(" taken").toString());
        if(classifierName.indexOf(",liblin,") > 0)
            try
            {
                Utilities.printNameAndWarning("LibLINEAR");
                start = Utilities.getNow();
                Utilities.addToClassPath((new StringBuilder(String.valueOf(sgJarFolder))).append("liblinear-1.51.jar").toString());
                Evaluation eval = new Evaluation(data);
                LibLINEAR schemeLibLINEAR = new LibLINEAR();
                options = " -i -o \u2013t";
                schemeLibLINEAR.setOptions(Utils.splitOptions(options));
                eval.crossValidateModel(schemeLibLINEAR, data, 10, new Random(randomSeed), new Object[0]);
                PrintClassificationResults(eval, arffFileName, "LibLin", randomSeed, features, options, allResultsFilename, summaryResultsFilename);
                System.out.println((new StringBuilder(String.valueOf(Utilities.timeGap(start, Utilities.getNow())))).append(" taken").toString());
            }
            catch(Exception e)
            {
                System.out.println((new StringBuilder("Error with LibLINEAR on ")).append(arffFileName).append(" ").append(e.getMessage()).toString());
                System.out.println((new StringBuilder("Must have jar file in Jar folder ")).append(sgJarFolder).append(" or classpath. Here is the current Java classpath").toString());
                Utilities.printClasspath();
            }
        if(classifierName.indexOf(",libsvm,") > 0)
            try
            {
                Utilities.printNameAndWarning("LibSVM");
                start = Utilities.getNow();
                Utilities.addToClassPath((new StringBuilder(String.valueOf(sgJarFolder))).append("libsvm.jar").toString());
                Evaluation eval = new Evaluation(data);
                LibSVM schemeLibSVM = new LibSVM();
                options = "-s 0";
                schemeLibSVM.setOptions(Utils.splitOptions(options));
                eval.crossValidateModel(schemeLibSVM, data, 10, new Random(randomSeed), new Object[0]);
                PrintClassificationResults(eval, arffFileName, "LibSVM", randomSeed, features, options, allResultsFilename, summaryResultsFilename);
                System.out.println((new StringBuilder(String.valueOf(Utilities.timeGap(start, Utilities.getNow())))).append(" taken").toString());
            }
            catch(Exception e)
            {
                System.out.println((new StringBuilder("Error with LibSVM on ")).append(arffFileName).append(" ").append(e.getMessage()).toString());
                System.out.println((new StringBuilder("Must have jar file in Jar folder ")).append(sgJarFolder).append(" or classpath. Here is the current Java classpath").toString());
                Utilities.printClasspath();
            }
        if((classifierName.indexOf(",smo,") > 0 || classifierName.indexOf(",all,") > 0) && !classifierExclude.equals("SMO"))
            try
            {
                Utilities.printNameAndWarning("SMO   ");
                start = Utilities.getNow();
                Evaluation eval = new Evaluation(data);
                SMO schemeSMO = new SMO();
                options = "-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\"";
                schemeSMO.setOptions(Utils.splitOptions(options));
                eval.crossValidateModel(schemeSMO, data, 10, new Random(randomSeed), new Object[0]);
                PrintClassificationResults(eval, arffFileName, "SMO", randomSeed, features, options, allResultsFilename, summaryResultsFilename);
                System.out.println((new StringBuilder(String.valueOf(Utilities.timeGap(start, Utilities.getNow())))).append(" taken").toString());
            }
            catch(Exception e)
            {
                System.out.println((new StringBuilder("Error with SMO on ")).append(arffFileName).append(" ").append(e.getMessage()).toString());
                System.out.println("Must have jar file in classpath (not dir!). Here is the current Java classpath");
                Utilities.printClasspath();
            }
        if((classifierName.indexOf(",slog,") > 0 || classifierName.indexOf(",all,") > 0) && !classifierExclude.equals("SLOG"))
            try
            {
                Utilities.printNameAndWarning("SLOG  ");
                start = Utilities.getNow();
                Evaluation eval = new Evaluation(data);
                SimpleLogistic schemeSLOG = new SimpleLogistic();
                options = "-I 0 -M 500 -H 50 -W 0.0";
                schemeSLOG.setOptions(Utils.splitOptions(options));
                eval.crossValidateModel(schemeSLOG, data, 10, new Random(randomSeed), new Object[0]);
                PrintClassificationResults(eval, arffFileName, "SLOG", randomSeed, features, options, allResultsFilename, summaryResultsFilename);
                System.out.println((new StringBuilder(String.valueOf(Utilities.timeGap(start, Utilities.getNow())))).append(" taken").toString());
            }
            catch(Exception e)
            {
                System.out.println((new StringBuilder("Error with SLOG on ")).append(arffFileName).append(" ").append(e.getMessage()).toString());
            }
        if((classifierName.indexOf(",bayes,") > 0 || classifierName.indexOf(",all,") > 0) && !classifierExclude.equals("BAYES"))
            try
            {
                Utilities.printNameAndWarning("BAYES ");
                start = Utilities.getNow();
                Evaluation eval = new Evaluation(data);
                NaiveBayes schemeBayes = new NaiveBayes();
                eval.crossValidateModel(schemeBayes, data, 10, new Random(randomSeed), new Object[0]);
                PrintClassificationResults(eval, arffFileName, "BAYES", randomSeed, features, options, allResultsFilename, summaryResultsFilename);
                System.out.println((new StringBuilder(String.valueOf(Utilities.timeGap(start, Utilities.getNow())))).append(" taken").toString());
            }
            catch(Exception e)
            {
                System.out.println((new StringBuilder("Error with BAYES on ")).append(arffFileName).append(" ").append(e.getMessage()).toString());
            }
        if((classifierName.indexOf(",ada,") > 0 || classifierName.indexOf(",all,") > 0) && !classifierExclude.equals("ADA"))
            try
            {
                Utilities.printNameAndWarning("ADA   ");
                start = Utilities.getNow();
                Evaluation eval = new Evaluation(data);
                AdaBoostM1 schemeAda = new AdaBoostM1();
                options = "-P 100 -S 1 -I 10 -W weka.classifiers.trees.DecisionStump";
                schemeAda.setOptions(Utils.splitOptions(options));
                eval.crossValidateModel(schemeAda, data, 10, new Random(randomSeed), new Object[0]);
                PrintClassificationResults(eval, arffFileName, "ADA", randomSeed, features, options, allResultsFilename, summaryResultsFilename);
                System.out.println((new StringBuilder(String.valueOf(Utilities.timeGap(start, Utilities.getNow())))).append(" taken").toString());
            }
            catch(Exception e)
            {
                System.out.println((new StringBuilder("Error with ADA on ")).append(arffFileName).append(" ").append(e.getMessage()).toString());
            }
        if((classifierName.indexOf(",smoreg,") > 0 || classifierName.indexOf(",all,") > 0) && !classifierExclude.equals("SMOreg"))
            try
            {
                Utilities.printNameAndWarning("SMOreg");
                start = Utilities.getNow();
                Evaluation eval = new Evaluation(data);
                ClassificationViaRegression schemeSMOreg = new ClassificationViaRegression();
                options = "-W weka.classifiers.functions.SMOreg -- -C 1.0 -N 0 -I \"weka.classifiers.functions.supportVector.RegSMOImproved -L 0.0010 -W 1 -P 1.0E-12 -T 0.0010 -V\" -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\"";
                schemeSMOreg.setOptions(Utils.splitOptions(options));
                eval.crossValidateModel(schemeSMOreg, data, 10, new Random(randomSeed), new Object[0]);
                PrintClassificationResults(eval, arffFileName, "SMOreg", randomSeed, features, options, allResultsFilename, summaryResultsFilename);
                System.out.println((new StringBuilder(String.valueOf(Utilities.timeGap(start, Utilities.getNow())))).append(" taken").toString());
            }
            catch(Exception e)
            {
                System.out.println((new StringBuilder("Error with SMOreg on ")).append(arffFileName).append(" ").append(e.getMessage()).toString());
            }
        if((classifierName.indexOf(",jrip,") > 0 || classifierName.indexOf(",all,") > 0) && !classifierExclude.equals("JRIP"))
            try
            {
                Utilities.printNameAndWarning("JRIP  ");
                start = Utilities.getNow();
                Evaluation eval = new Evaluation(data);
                JRip schemeJrip = new JRip();
                options = "-F 3 -N 2.0 -O 2 -S 1";
                schemeJrip.setOptions(Utils.splitOptions(options));
                eval.crossValidateModel(schemeJrip, data, 10, new Random(randomSeed), new Object[0]);
                PrintClassificationResults(eval, arffFileName, "JRIP", randomSeed, features, options, allResultsFilename, summaryResultsFilename);
                System.out.println((new StringBuilder(String.valueOf(Utilities.timeGap(start, Utilities.getNow())))).append(" taken").toString());
            }
            catch(Exception e)
            {
                System.out.println((new StringBuilder("Error with JRIP on ")).append(arffFileName).append(" ").append(e.getMessage()).toString());
            }
        if((classifierName.indexOf(",dec,") > 0 || classifierName.indexOf(",all,") > 0) && !classifierExclude.equals("DEC"))
            try
            {
                Utilities.printNameAndWarning("DEC   ");
                start = Utilities.getNow();
                Evaluation eval = new Evaluation(data);
                DecisionTable schemeDec = new DecisionTable();
                options = "-X 1 -S \"weka.attributeSelection.BestFirst -D 1 -N 5\"";
                schemeDec.setOptions(Utils.splitOptions(options));
                eval.crossValidateModel(schemeDec, data, 10, new Random(randomSeed), new Object[0]);
                PrintClassificationResults(eval, arffFileName, "DEC", randomSeed, features, options, allResultsFilename, summaryResultsFilename);
                System.out.println((new StringBuilder(String.valueOf(Utilities.timeGap(start, Utilities.getNow())))).append(" taken").toString());
            }
            catch(Exception e)
            {
                System.out.println((new StringBuilder("Error with DEC on ")).append(arffFileName).append(" ").append(e.getMessage()).toString());
            }
        if((classifierName.indexOf(",j48,") > 0 || classifierName.indexOf(",all,") > 0) && !classifierExclude.equals("J48"))
            try
            {
                Utilities.printNameAndWarning("J48   ");
                start = Utilities.getNow();
                Evaluation eval = new Evaluation(data);
                J48 schemeJ48 = new J48();
                options = "-C 0.25 -M 2";
                schemeJ48.setOptions(Utils.splitOptions(options));
                eval.crossValidateModel(schemeJ48, data, 10, new Random(randomSeed), new Object[0]);
                PrintClassificationResults(eval, arffFileName, "J48", randomSeed, features, options, allResultsFilename, summaryResultsFilename);
                System.out.println((new StringBuilder(String.valueOf(Utilities.timeGap(start, Utilities.getNow())))).append(" taken").toString());
            }
            catch(Exception e)
            {
                System.out.println((new StringBuilder("Error with J48 on ")).append(arffFileName).append(" ").append(e.getMessage()).toString());
            }
        return lessFeaturesRequestedThanData;
    }

    public static void PrintClassificationResults(Evaluation eval, String arffFilename, String classifierName, int randomSeed, int features, String options, String allResultsFilename, String summaryResultsFilename)
        throws Exception
    {
        FileOutputStream fout = new FileOutputStream(allResultsFilename, true);
        PrintStream allResultsPrintStream = new PrintStream(fout);
        allResultsPrintStream.println();
        allResultsPrintStream.println(arffFilename);
        allResultsPrintStream.println("=== Evaluation result ===");
        allResultsPrintStream.println((new StringBuilder("Scheme: ")).append(classifierName).toString());
        allResultsPrintStream.println((new StringBuilder("Options: ")).append(options).toString());
        allResultsPrintStream.println((new StringBuilder("Relation: .Randomize-S")).append(randomSeed).append("Features-N").append(features).toString());
        allResultsPrintStream.println(eval.toSummaryString());
        allResultsPrintStream.println(eval.toClassDetailsString());
        allResultsPrintStream.println(eval.toMatrixString());
        fout.close();
        fout = new FileOutputStream(summaryResultsFilename, true);
        PrintStream summaryResultsPrintStream = new PrintStream(fout);
        summaryResultsPrintStream.println((new StringBuilder(String.valueOf(classifierName))).append("\t").append(randomSeed).append("\t").append(features).append("\t").append(eval.pctCorrect()).append("%\t").append(options).append("\t").append(arffFilename).toString());
        fout.close();
    }

}
