package com.synaptix.toast.plugin.synaptix.runtime.interpreter.algo;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.collections.CollectionUtils;

public class NameFileReader {

    public NameFileReader() {

    }

    public void loadNames() {

        NameFileReader.class.getResourceAsStream("Names.xls");

        try {
           /* HSSFWorkbook workBook = new HSSFWorkbook(ipStream);
            HSSFSheet workSheet = workBook.getSheet("Boys");

            if (workSheet != null) {
                for(short i=2; i<2503; i++) {
                    HSSFRow row = workSheet.getRow(i);
                    //System.out.println("Row:" + i);
                    HSSFCell cell = row.getCell((short)1);
                    String name = cell.getStringCellValue();
                    NameIndexer.indexName(name);
                }

                System.out.println("Processing names xls complete.");
                //NameIndexer.printIndex();
                promptUser();

            } else {
                throw new NullPointerException("WorkSheet not Found!");
            }*/

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void promptUser() {
        final Scanner scanner = new Scanner(System.in);
        System.out.print("Please input your phrase: ");
        final String phrase = scanner.nextLine();

        final List<PhraseMatchResults> topResults = NameIndexer.findMatchingPhrases(phrase);

        for (final Iterator<PhraseMatchResults> iterator = topResults.iterator(); iterator.hasNext();) {
            final PhraseMatchResults results = iterator.next();

            System.out.println("*********** ALGORITHM: " + results.getAlgoName());

            System.out.println("Exact match words are: ");
            for (final Iterator<String> phrases = results.getExactMatches().iterator(); phrases.hasNext();) {
                final String word = phrases.next();
                System.out.println(word);
            }

            System.out.println("Near match words are: ");
            for (final Iterator<String> phrases = results.getNearMatches().iterator(); phrases.hasNext();) {
                final String word = phrases.next();
                System.out.println(word);
            }

            System.out.println("*********** END ALGORITHM *****************");

        }

        final PhraseMatchResults soundExResults = topResults.get(0);
        final PhraseMatchResults metaPhoneResults = topResults.get(1);
        final PhraseMatchResults dMetaPhoneResults = topResults.get(2);

        //Comparison
        System.out.println("&&&&&&&&&&& EXACT ANALYSIS &&&&&&&&&&&&");
        final Collection unionCol = CollectionUtils.union(soundExResults.getExactMatches(), metaPhoneResults.getExactMatches());
        System.out.println(unionCol);
        final Collection interCol = CollectionUtils.intersection(soundExResults.getExactMatches(), metaPhoneResults.getExactMatches());
        System.out.println(interCol);

        System.out.println("&&&&&&&&&&& NEAR ANALYSIS &&&&&&&&&&&");
        final Collection unionColn = CollectionUtils.union(soundExResults.getNearMatches(), metaPhoneResults.getNearMatches());
        CollectionUtils.intersection(soundExResults.getNearMatches(), metaPhoneResults.getNearMatches());

        System.out.println("#######METAPHONE BETTER THAN SOUNDEX???#######");

        System.out.println("MetaPhone result set has all: " + metaPhoneResults.getExactMatches().containsAll(soundExResults.getExactMatches()));
        System.out.println("MetaPhone Near result set has all: " + metaPhoneResults.getNearMatches().containsAll(soundExResults.getNearMatches()));

        System.out.println("Whats missing in SOUNDEX near matches?");
        System.out.println(CollectionUtils.subtract(unionColn, soundExResults.getNearMatches()));

        System.out.println("Whats missing in METAPHONE near matches?");
        System.out.println(CollectionUtils.subtract(unionColn, metaPhoneResults.getNearMatches()));

        System.out.println("####### DOUBLEMETAPHONE BETTER THAN METAPHONE???#######");

        System.out.println("DMetaPhone result set has all: " + dMetaPhoneResults.getExactMatches().containsAll(metaPhoneResults.getExactMatches()));
        System.out.println("DMetaPhone Near result set has all: " + dMetaPhoneResults.getNearMatches().containsAll(metaPhoneResults.getNearMatches()));

        final Collection unionDColn = CollectionUtils.union(dMetaPhoneResults.getNearMatches(), metaPhoneResults.getNearMatches());
        CollectionUtils.intersection(dMetaPhoneResults.getNearMatches(), metaPhoneResults.getNearMatches());

        System.out.println("Whats missing in METAPHONE near matches?");
        System.out.println(CollectionUtils.subtract(unionDColn, metaPhoneResults.getNearMatches()));

        System.out.println("Whats missing in DOUBLEMETAPHONE near matches?");
        System.out.println(CollectionUtils.subtract(unionDColn, dMetaPhoneResults.getNearMatches()));

    }

    public static void main(final String args[]) {
        final NameFileReader reader = new NameFileReader();
        reader.loadNames();
    }

}