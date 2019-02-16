package com.fairsearch.fair;

import com.fairsearch.fair.lib.FailprobabilityCalculator;
import com.fairsearch.fair.lib.MTableFailProbPair;
import com.fairsearch.fair.lib.MTableGenerator;
import com.fairsearch.fair.lib.RecursiveNumericFailprobabilityCalculator;
import com.fairsearch.fair.utils.FairScoreDoc;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.apache.lucene.search.TopDocs;

import java.util.Arrays;

/**
 * This class serves as a wrapper around the utilities we have created for FA*IR ranking
 */
public class Fair {

    private int k; //the total number of elements
    private double p; //The proportion of protected candidates in the top-k ranking
    private double alpha; //The significance level

    public Fair(int k, double p, double alpha){
        //check the parameters before using them (this will throw an exception if things don't look good
        validateBasicParameters(k, p, alpha);

        //asign the attributes
        this.k = k;
        this.p = p;
        this.alpha = alpha;
    }

    /**
     * Creates an mtable using alpha unadjusted.
     * @return            The generated mtable (int[])
     */
    public int[] createUnadjustedMTable() {
        return createMTable(this.alpha, false);
    }

    /**
     * Creates an mtable using alpha adjusted.
     * @return            The generated mtable (int[])
     */
    public int[] createAdjustedMTable() {
        return createMTable(this.alpha, true);
    }

    /**
     * Creates an mtable by passing your own alpha
     * @param alpha       The significance level
     * @param adjustAlpha Boolean indicating whether the alpha be adjusted or not
     * @return            The generated mtable (int[])
     */
    public int[] createMTable(double alpha, boolean adjustAlpha) {
        //check if passed alpha is ok
        validateAlpha(alpha);

        //create the mtable
        MTableGenerator generator = new MTableGenerator(this.k, this.p, alpha, adjustAlpha);
        return Arrays.copyOfRange(generator.getMTable(), 1, generator.getMTable().length);
    }

    /**
     * Computes the alpha adjusted for the given set of parameters
     * @return            The adjusted alpha
     */
    public double adjustAlpha() {
        RecursiveNumericFailprobabilityCalculator adjuster = new RecursiveNumericFailprobabilityCalculator(this.k, this.p, this.alpha);
        MTableFailProbPair failProbPair = adjuster.adjustAlpha();
        return failProbPair.getAlpha();
    }

    /**
     * Computes analytically the probability that a ranking created with the simulator will fail to pass the mtable
     * @return            The adjusted alpha
     */
    public double computeFailureProbability(int[] mtable) {
        if(mtable.length != this.k) {
            throw new ValueException("Number of elements k and (int[]) mtable length must be equal!");
        }

        FailprobabilityCalculator calculator = new RecursiveNumericFailprobabilityCalculator(this.k, this.p, this.alpha);

        // the internal mechanics of the MTableGenerator works with k+1 table length
        // so, we must create a longer interim mtable with a 0th position
        int[] interimMTable = new int[mtable.length + 1];
        System.arraycopy(mtable, 0, interimMTable, 1, mtable.length);

        return calculator.calculateFailprobability(interimMTable);
    }

    /**
     * Checks if the ranking is fair in respect to the mtable
     * @param docs        The ranking to be checked
     * @param mtable      The mtable against to check
     * @return            Returns whether the rankings statisfies the mtable
     */
    public static boolean checkRankingMTable(TopDocs docs, int[] mtable) {
        int countProtected = 0;

        //if the mtable has a different number elements than there are in the top docs return false
        if(docs.scoreDocs.length != mtable.length)
            throw new ValueException("Number of documents in (TopDocs) docs and (int[]) mtable length are not the same!");

        //check number of protected element at each rank
        for(int i=0; i < docs.scoreDocs.length; i++) {
            countProtected += ((FairScoreDoc)docs.scoreDocs[i]).isProtected ? 1 : 0;
            if(countProtected < mtable[i])
                return false;
        }
        return true;
    }

    /**
     * Checks if the ranking is fair for the given parameters
     * @param docs        The ranking to be checked
     * @return            Returns a boolean which specifies whether the ranking is fair
     */
    public boolean isFair(TopDocs docs) {
        return checkRankingMTable(docs, this.createAdjustedMTable());
    }

    /**
     * Validates if n (k), p and alpha are in the required ranges
     * @param n           Total number of elements (above or equal to 10)
     * @param p           The proportion of protected candidates in the top-k ranking (between 0.02 and 0.98)
     * @param alpha       The significance level (between 0.01 and 0.15)
     */
    private static void validateBasicParameters(int n, double p, double alpha) {
        if(n < 10) {
            throw new ValueException("Total number of elements `n (k)` must be above or equal to 10");
        }
        if(p < 0.02 || p > 0.98) {
            throw new ValueException("The proportion of protected candidates `p` in the top-k ranking must between 0.02 and 0.98");
        }
        validateAlpha(alpha);
    }

    private static void validateAlpha(double alpha) {
        if(alpha < 0.01 || alpha > 0.15) {
            throw new ValueException("The significance level `alpha` must be between 0.01 and 0.15)");
        }
    }
}