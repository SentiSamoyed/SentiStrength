// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   ClassificationStatistics.java

package uk.ac.wlv.sentistrength;

import java.io.PrintStream;

/**
 * 计算分类测试所需相关数据<br/>
 * 可调用的计算包括：<br/>
 * <ol>
 * <li>Pearson相关系数的绝对值</li>
 * <li>Pearson相关系数</li>
 * <li>Trinary混淆表或Binary混淆表</li>
 * <li>预测数组中的正确数</li>
 * <li>通过无除法计算出的平均误差率的绝对值</li>
 * <li>平均误差率的绝对值</li>
 * <li>相对于baseline的预测准确度</li>
 * <li>基于baseline准确度，对预测数组做最大分类预测</li>
 * </ol>
 */
public class ClassificationStatistics
{

    /**
     * ClassificationStatistics构造函数
     */
    public ClassificationStatistics()
    {
    }

    /**
     * 计算Pearson相关系数，取绝对值
     * @param iCorrect 正确数组
     * @param iPredicted 预测数组
     * @param iCount 数组长度
     * @return 返回Pearson相关系数的绝对值
     */
    public static double correlationAbs(int iCorrect[], int iPredicted[], int iCount)
    {
        double fMeanC = 0.0D;
        double fMeanP = 0.0D;
        double fProdCP = 0.0D;
        double fSumCSq = 0.0D;
        double fSumPSq = 0.0D;
        for(int iRow = 1; iRow <= iCount; iRow++)
        {
            fMeanC += Math.abs(iCorrect[iRow]);
            fMeanP += Math.abs(iPredicted[iRow]);
        }

        fMeanC /= iCount;
        fMeanP /= iCount;
        for(int iRow = 1; iRow <= iCount; iRow++)
        {
            fProdCP += ((double)Math.abs(iCorrect[iRow]) - fMeanC) * ((double)Math.abs(iPredicted[iRow]) - fMeanP);
            fSumPSq += Math.pow((double)Math.abs(iPredicted[iRow]) - fMeanP, 2D);
            fSumCSq += Math.pow((double)Math.abs(iCorrect[iRow]) - fMeanC, 2D);
        }

        return fProdCP / (Math.sqrt(fSumPSq) * Math.sqrt(fSumCSq));
    }

    /**
     * 计算Pearson相关系数
     * @param iCorrect 正确数组
     * @param iPredicted 预测数组
     * @param iCount 数组长度
     * @return 返回相关性值
     */
    public static double correlation(int iCorrect[], int iPredicted[], int iCount)
    {
        double fMeanC = 0.0D;
        double fMeanP = 0.0D;
        double fProdCP = 0.0D;
        double fSumCSq = 0.0D;
        double fSumPSq = 0.0D;
        for(int iRow = 1; iRow <= iCount; iRow++)
        {
            fMeanC += iCorrect[iRow];
            fMeanP += iPredicted[iRow];
        }

        fMeanC /= iCount;
        fMeanP /= iCount;
        for(int iRow = 1; iRow <= iCount; iRow++)
        {
            fProdCP += ((double)iCorrect[iRow] - fMeanC) * ((double)iPredicted[iRow] - fMeanP);
            fSumPSq += Math.pow((double)iPredicted[iRow] - fMeanP, 2D);
            fSumCSq += Math.pow((double)iCorrect[iRow] - fMeanC, 2D);
        }

        return fProdCP / (Math.sqrt(fSumPSq) * Math.sqrt(fSumCSq));
    }

    /**
     * 打印Trinary混淆表或Binary混淆表
     * @param iTrinaryEstimate Trinary预测数组
     * @param iTrinaryCorrect Trinary正确数组
     * @param iDataCount Trinary数组长度
     * @param estCorr 混淆矩阵
     */
    public static void TrinaryOrBinaryConfusionTable(int iTrinaryEstimate[], int iTrinaryCorrect[], int iDataCount, int estCorr[][])
    {
        for(int i = 0; i <= 2; i++)
        {
            for(int j = 0; j <= 2; j++)
                estCorr[i][j] = 0;

        }

        for(int i = 1; i <= iDataCount; i++)
            if(iTrinaryEstimate[i] > -2 && iTrinaryEstimate[i] < 2 && iTrinaryCorrect[i] > -2 && iTrinaryCorrect[i] < 2)
                estCorr[iTrinaryEstimate[i] + 1][iTrinaryCorrect[i] + 1]++;
            else
                System.out.println((new StringBuilder("Estimate or correct value ")).append(i).append(" out of range -1 to +1 (data count may be wrong): ").append(iTrinaryEstimate[i]).append(" ").append(iTrinaryCorrect[i]).toString());

    }

    /**
     * 计算Pearson相关系数，取绝对值
     * @param iCorrect 正确数组
     * @param iPredicted 预测数组
     * @param bSelected 选中数组
     * @param bInvert 是否倒转极性
     * @param iCount 数组长度
     * @return 返回Pearson相关系数的绝对值
     */
    public static double correlationAbs(int iCorrect[], int iPredicted[], boolean bSelected[], boolean bInvert, int iCount)
    {
        double fMeanC = 0.0D;
        double fMeanP = 0.0D;
        double fProdCP = 0.0D;
        double fSumCSq = 0.0D;
        double fSumPSq = 0.0D;
        int iDataCount = 0;
        for(int iRow = 1; iRow <= iCount; iRow++)
            if(bSelected[iRow] && !bInvert || !bSelected[iRow] && bInvert)
            {
                fMeanC += Math.abs(iCorrect[iRow]);
                fMeanP += Math.abs(iPredicted[iRow]);
                iDataCount++;
            }

        fMeanC /= iDataCount;
        fMeanP /= iDataCount;
        for(int iRow = 1; iRow <= iCount; iRow++)
            if(bSelected[iRow] && !bInvert || !bSelected[iRow] && bInvert)
            {
                fProdCP += ((double)Math.abs(iCorrect[iRow]) - fMeanC) * ((double)Math.abs(iPredicted[iRow]) - fMeanP);
                fSumPSq += Math.pow((double)Math.abs(iPredicted[iRow]) - fMeanP, 2D);
                fSumCSq += Math.pow((double)Math.abs(iCorrect[iRow]) - fMeanC, 2D);
            }

        return fProdCP / (Math.sqrt(fSumPSq) * Math.sqrt(fSumCSq));
    }

    /**
     * 计算预测数组中的正确数
     * @param iCorrect 正确数组
     * @param iPredicted 预测数组
     * @param iCount 数组长度
     * @param bChangeSignOfOneArray 是否改变数组的情感极性
     * @return 预测数组中的正确数
     */
    public static int accuracy(int iCorrect[], int iPredicted[], int iCount, boolean bChangeSignOfOneArray)
    {
        int iCorrectCount = 0;
        if(bChangeSignOfOneArray)
        {
            for(int iRow = 1; iRow <= iCount; iRow++)
                if(iCorrect[iRow] == -iPredicted[iRow])
                    iCorrectCount++;

        } else
        {
            for(int iRow = 1; iRow <= iCount; iRow++)
                if(iCorrect[iRow] == iPredicted[iRow])
                    iCorrectCount++;

        }
        return iCorrectCount;
    }

    /**
     * 计算预测数组中的正确数
     * @param iCorrect 正确数组
     * @param iPredicted 预测数组
     * @param bSelected 选中数组
     * @param bInvert 是否倒转极性
     * @param iCount 数组长度
     * @return 预测数组中的正确数
     */
    public static int accuracy(int iCorrect[], int iPredicted[], boolean bSelected[], boolean bInvert, int iCount)
    {
        int iCorrectCount = 0;
        for(int iRow = 1; iRow <= iCount; iRow++)
            if((bSelected[iRow] && !bInvert || !bSelected[iRow] && bInvert) && iCorrect[iRow] == iPredicted[iRow])
                iCorrectCount++;

        return iCorrectCount;
    }

    /**
     * 计算预测数组中的正确数，允许误差值在正负1之间
     * @param iCorrect 正确数组
     * @param iPredicted 预测数组
     * @param bSelected 选中数组
     * @param bInvert 是否倒转极性
     * @param iCount 数组长度
     * @return 预测数组中的正确数
     */
    public static int accuracyWithin1(int iCorrect[], int iPredicted[], boolean bSelected[], boolean bInvert, int iCount)
    {
        int iCorrectCount = 0;
        for(int iRow = 1; iRow <= iCount; iRow++)
            if((bSelected[iRow] && !bInvert || !bSelected[iRow] && bInvert) && Math.abs(iCorrect[iRow] - iPredicted[iRow]) <= 1)
                iCorrectCount++;

        return iCorrectCount;
    }

    /**
     * 计算预测数组中的正确数，允许误差值在正负1之间
     * @param iCorrect 正确数组
     * @param iPredicted 预测数组
     * @param iCount 数组长度
     * @param bChangeSignOfOneArray 是否改变数组的情感极性
     * @return 预测数组中的正确数
     */
    public static int accuracyWithin1(int iCorrect[], int iPredicted[], int iCount, boolean bChangeSignOfOneArray)
    {
        int iCorrectCount = 0;
        if(bChangeSignOfOneArray)
        {
            for(int iRow = 1; iRow <= iCount; iRow++)
                if(Math.abs(iCorrect[iRow] + iPredicted[iRow]) <= 1)
                    iCorrectCount++;

        } else
        {
            for(int iRow = 1; iRow <= iCount; iRow++)
                if(Math.abs(iCorrect[iRow] - iPredicted[iRow]) <= 1)
                    iCorrectCount++;

        }
        return iCorrectCount;
    }

    /**
     * 无除法计算预测数组的平均绝对值误差率
     * @param iCorrect 正确数组
     * @param iPredicted 预测数组
     * @param bSelected 选中数组
     * @param bInvert 是否倒转极性
     * @param iCount 数组长度
     * @return 通过无除法计算出的预测数组的平均绝对值误差率
     */
    public static double absoluteMeanPercentageErrorNoDivision(int iCorrect[], int iPredicted[], boolean bSelected[], boolean bInvert, int iCount)
    {
        int iDataCount = 0;
        double fAMeanPE = 0.0D;
        for(int iRow = 1; iRow <= iCount; iRow++)
            if(bSelected[iRow] && !bInvert || !bSelected[iRow] && bInvert)
            {
                fAMeanPE += Math.abs(iPredicted[iRow] - iCorrect[iRow]);
                iDataCount++;
            }

        return fAMeanPE / (double)iDataCount;
    }

    /**
     * 计算预测数组的平均绝对值误差率
     * @param iCorrect 正确数组
     * @param iPredicted 预测数组
     * @param bSelected 选中数组
     * @param bInvert 是否倒转极性
     * @param iCount 数组长度
     * @return 预测数组的平均绝对值误差率
     */
    public static double absoluteMeanPercentageError(int iCorrect[], int iPredicted[], boolean bSelected[], boolean bInvert, int iCount)
    {
        int iDataCount = 0;
        double fAMeanPE = 0.0D;
        for(int iRow = 1; iRow <= iCount; iRow++)
            if(bSelected[iRow] && !bInvert || !bSelected[iRow] && bInvert)
            {
                fAMeanPE += Math.abs((double)(iPredicted[iRow] - iCorrect[iRow]) / (double)iCorrect[iRow]);
                iDataCount++;
            }

        return fAMeanPE / (double)iDataCount;
    }

    /**
     * 无除法计算预测数组的平均绝对值误差率
     * @param iCorrect 正确数组
     * @param iPredicted 预测数组
     * @param iCount 数组长度
     * @param bChangeSignOfOneArray 是否改变数组的情感极性
     * @return 通过无除法计算出的预测数组的平均绝对值误差率
     */
    public static double absoluteMeanPercentageErrorNoDivision(int iCorrect[], int iPredicted[], int iCount, boolean bChangeSignOfOneArray)
    {
        double fAMeanPE = 0.0D;
        if(bChangeSignOfOneArray)
        {
            for(int iRow = 1; iRow <= iCount; iRow++)
                fAMeanPE += Math.abs(iPredicted[iRow] + iCorrect[iRow]);

        } else
        {
            for(int iRow = 1; iRow <= iCount; iRow++)
                fAMeanPE += Math.abs(iPredicted[iRow] - iCorrect[iRow]);

        }
        return fAMeanPE / (double)iCount;
    }

    /**
     * 计算相对于baseline的预测准确度
     * @param iCorrect 正确数组
     * @param iCount 数组长度
     * @return 相对于baseline的预测准确度
     */
    public static double baselineAccuracyMajorityClassProportion(int iCorrect[], int iCount)
    {
        if(iCount == 0)
            return 0.0D;
        int iClassCount[] = new int[100];
        int iMinClass = iCorrect[1];
        int iMaxClass = iCorrect[1];
        for(int i = 2; i <= iCount; i++)
        {
            if(iCorrect[i] < iMinClass)
                iMinClass = iCorrect[i];
            if(iCorrect[i] > iMaxClass)
                iMaxClass = iCorrect[i];
        }

        if(iMaxClass - iMinClass >= 100)
            return 0.0D;
        for(int i = 0; i <= iMaxClass - iMinClass; i++)
            iClassCount[i] = 0;

        for(int i = 1; i <= iCount; i++)
            iClassCount[iCorrect[i] - iMinClass]++;

        int iMaxClassCount = 0;
        for(int i = 0; i <= iMaxClass - iMinClass; i++)
            if(iClassCount[i] > iMaxClassCount)
                iMaxClassCount = iClassCount[i];

        return (double)iMaxClassCount / (double)iCount;
    }

    /**
     * 基于baseline准确度，对预测数组做最大分类预测
     * @param iCorrect 正确数组
     * @param iPredict 预测数组
     * @param iCount 数组长度
     * @param bChangeSign 是否转变极性
     */
    public static void baselineAccuracyMakeLargestClassPrediction(int iCorrect[], int iPredict[], int iCount, boolean bChangeSign)
    {
        if(iCount == 0)
            return;
        int iClassCount[] = new int[100];
        int iMinClass = iCorrect[1];
        int iMaxClass = iCorrect[1];
        for(int i = 2; i <= iCount; i++)
        {
            if(iCorrect[i] < iMinClass)
                iMinClass = iCorrect[i];
            if(iCorrect[i] > iMaxClass)
                iMaxClass = iCorrect[i];
        }

        if(iMaxClass - iMinClass >= 100)
            return;
        for(int i = 0; i <= iMaxClass - iMinClass; i++)
            iClassCount[i] = 0;

        for(int i = 1; i <= iCount; i++)
            iClassCount[iCorrect[i] - iMinClass]++;

        int iMaxClassCount = 0;
        int iLargestClass = 0;
        for(int i = 0; i <= iMaxClass - iMinClass; i++)
            if(iClassCount[i] > iMaxClassCount)
            {
                iMaxClassCount = iClassCount[i];
                iLargestClass = i + iMinClass;
            }

        if(bChangeSign)
        {
            for(int i = 1; i <= iCount; i++)
                iPredict[i] = -iLargestClass;

        } else
        {
            for(int i = 1; i <= iCount; i++)
                iPredict[i] = iLargestClass;

        }
    }

    /**
     * 计算预测数组的平均绝对值误差率
     * @param iCorrect 正确数组
     * @param iPredicted 预测数组
     * @param iCount 数组长度
     * @param bChangeSignOfOneArray 是否改变数组的情感极性
     * @return 预测数组的平均绝对值误差率
     */
    public static double absoluteMeanPercentageError(int iCorrect[], int iPredicted[], int iCount, boolean bChangeSignOfOneArray)
    {
        double fAMeanPE = 0.0D;
        if(bChangeSignOfOneArray)
        {
            for(int iRow = 1; iRow <= iCount; iRow++)
                fAMeanPE += Math.abs((double)(iPredicted[iRow] + iCorrect[iRow]) / (double)iCorrect[iRow]);

        } else
        {
            for(int iRow = 1; iRow <= iCount; iRow++)
                fAMeanPE += Math.abs((double)(iPredicted[iRow] - iCorrect[iRow]) / (double)iCorrect[iRow]);

        }
        return fAMeanPE / (double)iCount;
    }
}
