package uk.ac.wlv.utilities;

public class Sort {
  public static void quickSortStrings(String[] sArray, int l, int r) {
    String sMiddle = sArray[(l + r) / 2];
    int i = l;
    int j = r;

    while (i <= j) {
      while (sMiddle.compareTo(sArray[i]) > 0 && i < r) {
        ++i;
      }

      while (sMiddle.compareTo(sArray[j]) < 0 && j > l) {
        --j;
      }

      if (i < j) {
        String Y = sArray[i];
        sArray[i] = sArray[j];
        sArray[j] = Y;
      }

      if (i <= j) {
        ++i;
        --j;
      }
    }

    if (l < j) {
      quickSortStrings(sArray, l, j);
    }

    if (i < r) {
      quickSortStrings(sArray, i, r);
    }

  }

  public static void quickSortInt(int[] iArray, int l, int r) {
    int X = iArray[(l + r) / 2];
    int i = l;
    int j = r;

    while (i <= j) {
      while (iArray[i] < X && i < r) {
        ++i;
      }

      while (X < iArray[j] && j > l) {
        --j;
      }

      if (i <= j) {
        int Y = iArray[i];
        iArray[i] = iArray[j];
        iArray[j] = Y;
        ++i;
        --j;
      }
    }

    if (l < j) {
      quickSortInt(iArray, l, j);
    }

    if (i < r) {
      quickSortInt(iArray, i, r);
    }

  }

  public static void quickSortDoubleWithInt(double[] fArray, int[] iArray2, int l, int r) {
    double X = fArray[(l + r) / 2];
    int i = l;
    int j = r;

    while (i <= j) {
      while (fArray[i] < X && i < r) {
        ++i;
      }

      while (X < fArray[j] && j > l) {
        --j;
      }

      if (i <= j) {
        double fTemp = fArray[i];
        int iTemp = iArray2[i];
        fArray[i] = fArray[j];
        iArray2[i] = iArray2[j];
        fArray[j] = fTemp;
        iArray2[j] = iTemp;
        ++i;
        --j;
      }
    }

    if (l < j) {
      quickSortDoubleWithInt(fArray, iArray2, l, j);
    }

    if (i < r) {
      quickSortDoubleWithInt(fArray, iArray2, i, r);
    }

  }

  public static void quickSortIntWithInt(int[] iArray, int[] iArray2, int l, int r) {
    int X = iArray[(l + r) / 2];
    int i = l;
    int j = r;

    while (i <= j) {
      while (iArray[i] < X && i < r) {
        ++i;
      }

      while (X < iArray[j] && j > l) {
        --j;
      }

      if (i <= j) {
        int iTemp = iArray[i];
        int iTemp2 = iArray2[i];
        iArray[i] = iArray[j];
        iArray2[i] = iArray2[j];
        iArray[j] = iTemp;
        iArray2[j] = iTemp2;
        ++i;
        --j;
      }
    }

    if (l < j) {
      quickSortIntWithInt(iArray, iArray2, l, j);
    }

    if (i < r) {
      quickSortIntWithInt(iArray, iArray2, i, r);
    }

  }

  public static int i_FindStringPositionInSortedArray(String sText, String[] sArray, int iLower, int iUpper) {
    boolean var4 = false;

    int iMiddle;
    while (iUpper - iLower > 2) {
      iMiddle = (iLower + iUpper) / 2;
      if (sText.compareTo(sArray[iMiddle]) < 0) {
        iUpper = iMiddle;
      } else {
        iLower = iMiddle;
      }
    }

    for (iMiddle = iLower; iMiddle <= iUpper; ++iMiddle) {
      if (sArray[iMiddle].compareTo(sText) == 0) {
        return iMiddle;
      }
    }

    return -1;
  }

  public static int i_FindIntPositionInSortedArray(int iFind, int[] iArray, int iLower, int iUpper) {
    boolean var4 = false;

    int iMiddle;
    while (iUpper - iLower > 2) {
      iMiddle = (iLower + iUpper) / 2;
      if (iFind < iArray[iMiddle]) {
        iUpper = iMiddle;
      } else {
        iLower = iMiddle;
      }
    }

    for (iMiddle = iLower; iMiddle <= iUpper; ++iMiddle) {
      if (iArray[iMiddle] == iFind) {
        return iMiddle;
      }
    }

    return -1;
  }

  public static int i_FindStringPositionInSortedArrayWithWildcardsInArray(String sText, String[] sArray, int iLower, int iUpper) {
    int iOriginalLower = iLower;
    int iOriginalUpper = iUpper;

    int iMiddle;
    while (iUpper - iLower > 2) {
      iMiddle = (iLower + iUpper) / 2;
      if (sText.compareTo(sArray[iMiddle]) < 0) {
        iUpper = iMiddle;
      } else {
        iLower = iMiddle;
      }
    }

    for (iMiddle = iUpper; iMiddle >= iLower; --iMiddle) {
      if (sArray[iMiddle].compareTo(sText) == 0) {
        return iMiddle;
      }
    }

    if (iLower > iOriginalLower) {
      --iLower;
    }

    if (iLower > iOriginalLower) {
      --iLower;
    }

    if (iLower > iOriginalLower) {
      --iLower;
    }

    if (iUpper < iOriginalUpper) {
      ++iUpper;
    }

    int iTextLength = sText.length();

    for (iMiddle = iUpper; iMiddle >= iLower; --iMiddle) {
      int iLength = sArray[iMiddle].length();
      if (iLength > 1 && sArray[iMiddle].substring(iLength - 1, iLength).compareTo("*") == 0 && iTextLength >= iLength - 1 && sText.substring(0, iLength - 1).compareTo(sArray[iMiddle].substring(0, iLength - 1)) == 0) {
        return iMiddle;
      }
    }

    return -1;
  }

  public static void quickSortStringsWithInt(String[] sArray, int[] iArray, int l, int r) {
    String sMiddle = sArray[(l + r) / 2];
    int i = l;
    int j = r;

    while (i <= j) {
      while (sMiddle.compareTo(sArray[i]) > 0 && i < r) {
        ++i;
      }

      while (sMiddle.compareTo(sArray[j]) < 0 && j > l) {
        --j;
      }

      if (i < j) {
        String sTemp = sArray[i];
        int iTemp = iArray[i];
        sArray[i] = sArray[j];
        iArray[i] = iArray[j];
        sArray[j] = sTemp;
        iArray[j] = iTemp;
      }

      if (i <= j) {
        ++i;
        --j;
      }
    }

    if (l < j) {
      quickSortStringsWithInt(sArray, iArray, l, j);
    }

    if (i < r) {
      quickSortStringsWithInt(sArray, iArray, i, r);
    }

  }

  public static void quickSortStringsWithStrings(String[] sArray, String[] sArray2, int l, int r) {
    String sMiddle = sArray[(l + r) / 2];
    int i = l;
    int j = r;

    while (i <= j) {
      while (sMiddle.compareTo(sArray[i]) > 0 && i < r) {
        ++i;
      }

      while (sMiddle.compareTo(sArray[j]) < 0 && j > l) {
        --j;
      }

      if (i < j) {
        String sTemp = sArray[i];
        String sTemp2 = sArray2[i];
        sArray[i] = sArray[j];
        sArray2[i] = sArray2[j];
        sArray[j] = sTemp;
        sArray2[j] = sTemp2;
      }

      if (i <= j) {
        ++i;
        --j;
      }
    }

    if (l < j) {
      quickSortStringsWithStrings(sArray, sArray2, l, j);
    }

    if (i < r) {
      quickSortStringsWithStrings(sArray, sArray2, i, r);
    }

  }

  public static void makeRandomOrderList(int[] iArray) {
    if (iArray != null) {
      int iArraySize = iArray.length;
      if (iArraySize >= 1) {
        double[] fRandArray = new double[iArraySize--];

        for (int i = 1; i <= iArraySize; ++i) {
          iArray[i] = i;
          fRandArray[i] = Math.random();
        }

        quickSortDoubleWithInt(fRandArray, iArray, 1, iArraySize);
      }
    }
  }

  public static void quickSortNumbersDescendingViaIndex(double[] fArray, int[] iIndexArray, int l, int r) {
    int i = l;
    int j = r;
    double fX = fArray[iIndexArray[(l + r) / 2]];

    while (i <= j) {
      while (fArray[iIndexArray[i]] > fX && i < r) {
        ++i;
      }

      while (fX > fArray[iIndexArray[j]] && j > l) {
        --j;
      }

      if (i <= j) {
        int iTemp = iIndexArray[i];
        iIndexArray[i] = iIndexArray[j];
        iIndexArray[j] = iTemp;
        ++i;
        --j;
      }
    }

    if (l < j) {
      quickSortNumbersDescendingViaIndex(fArray, iIndexArray, l, j);
    }

    if (i < r) {
      quickSortNumbersDescendingViaIndex(fArray, iIndexArray, i, r);
    }

  }
}
