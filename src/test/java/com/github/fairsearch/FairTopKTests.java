package com.github.fairsearch;

import com.github.fairsearch.utils.FairnessTableLookup;
import com.github.fairsearch.lib.FairTopK;
import com.github.fairsearch.lib.MTableGenerator;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.LuceneTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FairTopKTests extends LuceneTestCase {

    private class FairnessTableMock implements FairnessTableLookup {

        @Override
        public int fairness(int trials, float proportion, float significance) {
            return 0;
        }

        @Override
        public int[] fairnessAsTable(int k, float p, float a) {

            MTableGenerator tableGenerator = new MTableGenerator(k, p, a, false);
            return tableGenerator.getMTable();
        }
    }

    public void testFairRankingWith10Elements() {

        FairTopK topK = new FairTopK(new FairnessTableMock());
        List<ScoreDoc> npQueue = new ArrayList<>();
        List<ScoreDoc> pQueue = new ArrayList<>();

        npQueue.add(new ScoreDoc(1, 10));
        npQueue.add(new ScoreDoc(3, 9));
        npQueue.add(new ScoreDoc(5, 8));
        npQueue.add(new ScoreDoc(7, 7));
        npQueue.add(new ScoreDoc(9, 6));

        pQueue.add(new ScoreDoc(2, 5));
        pQueue.add(new ScoreDoc(4, 4));
        pQueue.add(new ScoreDoc(6, 3));
        pQueue.add(new ScoreDoc(8, 2));
        pQueue.add(new ScoreDoc(10, 1));

        TopDocs topDocs = topK.fairTopK(npQueue, pQueue, 10, 0.6f, 0.1f );

        assertEquals(1, topDocs.scoreDocs[0].doc);
        assertEquals(3, topDocs.scoreDocs[1].doc);
        assertEquals(2, topDocs.scoreDocs[2].doc);
        assertEquals(5, topDocs.scoreDocs[3].doc);
        assertEquals(4, topDocs.scoreDocs[4].doc);
        assertEquals(7, topDocs.scoreDocs[5].doc);
        assertEquals(6, topDocs.scoreDocs[6].doc);
        assertEquals(9, topDocs.scoreDocs[7].doc);
        assertEquals(8, topDocs.scoreDocs[8].doc);
        assertEquals(10, topDocs.scoreDocs[9].doc);
    }

    public void testFairRankingWith10ElementsAndFewProtected() {

        FairTopK topK = new FairTopK(new FairnessTableMock());
        List<ScoreDoc> npQueue = new ArrayList<>();
        List<ScoreDoc> pQueue = new ArrayList<>();

        npQueue.add(new ScoreDoc(1, 10));
        npQueue.add(new ScoreDoc(3, 9));
        npQueue.add(new ScoreDoc(5, 8));
        npQueue.add(new ScoreDoc(7, 7));
        npQueue.add(new ScoreDoc(9, 6));
        npQueue.add(new ScoreDoc(2, 5));
        npQueue.add(new ScoreDoc(4, 4));


        pQueue.add(new ScoreDoc(6, 3));
        pQueue.add(new ScoreDoc(8, 2));
        pQueue.add(new ScoreDoc(10, 1));

        TopDocs topDocs = topK.fairTopK(npQueue, pQueue, 10, 0.6f, 0.1f );

        assertEquals(1, topDocs.scoreDocs[0].doc);
        assertEquals(3, topDocs.scoreDocs[1].doc);
        assertEquals(6, topDocs.scoreDocs[2].doc);
        assertEquals(5, topDocs.scoreDocs[3].doc);
        assertEquals(8, topDocs.scoreDocs[4].doc);
        assertEquals(7, topDocs.scoreDocs[5].doc);
        assertEquals(10, topDocs.scoreDocs[6].doc);
        assertEquals(9, topDocs.scoreDocs[7].doc);
        assertEquals(2, topDocs.scoreDocs[8].doc);
        assertEquals(4, topDocs.scoreDocs[9].doc);
    }

    public void testFairRankingWith10ElementsAndNoProtectedElements() {

        FairTopK topK = new FairTopK(new FairnessTableMock());
        List<ScoreDoc> npQueue = new ArrayList<>();
        List<ScoreDoc> pQueue = new ArrayList<>();

        npQueue.add(new ScoreDoc(1, 10));
        npQueue.add(new ScoreDoc(3, 9));
        npQueue.add(new ScoreDoc(5, 8));
        npQueue.add(new ScoreDoc(7, 7));
        npQueue.add(new ScoreDoc(9, 6));
        npQueue.add(new ScoreDoc(2, 5));
        npQueue.add(new ScoreDoc(4, 4));
        npQueue.add(new ScoreDoc(6, 3));
        npQueue.add(new ScoreDoc(8, 2));
        npQueue.add(new ScoreDoc(10, 1));

        TopDocs topDocs = topK.fairTopK(npQueue, pQueue, 10, 0.6f, 0.1f );

        assertEquals(1, topDocs.scoreDocs[0].doc);
        assertEquals(3, topDocs.scoreDocs[1].doc);
        assertEquals(5, topDocs.scoreDocs[2].doc);
        assertEquals(7, topDocs.scoreDocs[3].doc);
        assertEquals(9, topDocs.scoreDocs[4].doc);
        assertEquals(2, topDocs.scoreDocs[5].doc);
        assertEquals(4, topDocs.scoreDocs[6].doc);
        assertEquals(6, topDocs.scoreDocs[7].doc);
        assertEquals(8, topDocs.scoreDocs[8].doc);
        assertEquals(10, topDocs.scoreDocs[9].doc);
    }

    public void testFairRankingWith10ElementsAndBiggerKValue() {

        FairTopK topK = new FairTopK(new FairnessTableMock());
        List<ScoreDoc> npQueue = new ArrayList<>();
        List<ScoreDoc> pQueue = new ArrayList<>();

        npQueue.add(new ScoreDoc(1, 10));
        npQueue.add(new ScoreDoc(3, 9));
        npQueue.add(new ScoreDoc(5, 8));
        npQueue.add(new ScoreDoc(7, 7));
        npQueue.add(new ScoreDoc(9, 6));

        pQueue.add(new ScoreDoc(2, 5));
        pQueue.add(new ScoreDoc(4, 4));
        pQueue.add(new ScoreDoc(6, 3));
        pQueue.add(new ScoreDoc(8, 2));
        pQueue.add(new ScoreDoc(10, 1));

        TopDocs topDocs = topK.fairTopK(npQueue, pQueue, 20, 0.6f, 0.1f );

        assertEquals(1, topDocs.scoreDocs[0].doc);
        assertEquals(3, topDocs.scoreDocs[1].doc);
        assertEquals(2, topDocs.scoreDocs[2].doc);
        assertEquals(5, topDocs.scoreDocs[3].doc);
        assertEquals(4, topDocs.scoreDocs[4].doc);
        assertEquals(7, topDocs.scoreDocs[5].doc);
        assertEquals(6, topDocs.scoreDocs[6].doc);
        assertEquals(9, topDocs.scoreDocs[7].doc);
        assertEquals(8, topDocs.scoreDocs[8].doc);
        assertEquals(10, topDocs.scoreDocs[9].doc);
    }
}
