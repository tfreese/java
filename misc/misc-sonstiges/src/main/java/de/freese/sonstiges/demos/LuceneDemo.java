// Created: 24 Juli 2024
package de.freese.sonstiges.demos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TermFrequencyAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

/**
 * @author Thomas Freese
 */
public final class LuceneDemo {
    public static void main(final String[] args) throws Exception {
        final Path path = Path.of("build", "lucene-dir");

        if (!Files.exists(path)) {
            System.err.println("Create Directory: " + path.toAbsolutePath());
            Files.createDirectories(path);
        }
        else {
            System.err.println("Use Directory: " + path.toAbsolutePath());
        }

        // Create the index
        // Directory index = new ByteBuffersDirectory();
        final Directory index = new NIOFSDirectory(path);

        // check/repair the index
        // try (CheckIndex checkIndex = new CheckIndex(index)) {
        //     checkIndex.setDoSlowChecks(true);
        //     checkIndex.setInfoStream(System.out);
        //
        //     CheckIndex.Status status = checkIndex.checkIndex();
        //
        //     if (!status.clean) {
        //         checkIndex.exorciseIndex(status);
        //     }
        // }

        // Specify the analyzer for tokenizing text.
        // The same analyzer should be used for indexing and searching
        final CharArraySet stopWords = new CharArraySet(2, true);
        stopWords.add("in");
        stopWords.add("for");
        final Analyzer analyzer = new StandardAnalyzer(stopWords);
        // Analyzer analyzer = new EnglishAnalyzer(stopWords);
        // Analyzer analyzer = new GermanAnalyzer(stopWords);

        final IndexWriterConfig config = new IndexWriterConfig(analyzer);

        try (IndexWriter indexWriter = new IndexWriter(index, config)) {
            indexWriter.deleteAll();

            final IndexWriter.DocStats docStats = indexWriter.getDocStats();

            if (docStats.numDocs == 0) {
                addDoc(indexWriter, "Lucene in Action", "193398817");
                addDoc(indexWriter, "Lucene for Dummies", "55320055Z");
                addDoc(indexWriter, "Managing Gigabytes", "55063554A");
                addDoc(indexWriter, "The Art of Computer Science", "9900333X");

                // for (int i = 0; i < 1_000_000; i++) {
                //     UUID uuid = UUID.randomUUID();
                //     String uuidString = uuid.toString();
                //     addDoc(indexWriter, uuidString, uuidString);
                // }

                indexWriter.commit();
                indexWriter.flush();
            }
        }

        // Query
        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        // Query query = new QueryParser("title", analyzer).parse("lucene");

        // MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title", "isbn"}, analyzer);
        // parser.setDefaultOperator(QueryParser.Operator.AND);
        // Query query = parser.parse("title:lucene isbn:193398817");

        final Query query = new TermQuery(new Term("title", "lucene"));
        // PhraseQuery
        // WildcardQuery
        // BooleanQuery

        // 3. search
        final int hitsPerPage = 10;

        try (IndexReader reader = DirectoryReader.open(index)) {
            final IndexSearcher searcher = new IndexSearcher(reader);
            final TopDocs topDocs = searcher.search(query, hitsPerPage);

            // Display results
            System.out.println("Found " + topDocs.totalHits);

            // for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            //     Document document = searcher.doc(scoreDoc.doc);
            //     System.out.println(document.get("isbn") + "\t" + document.get("title"));
            // }

            for (int i = 0; i < topDocs.totalHits.value(); ++i) {
                final ScoreDoc scoreDoc = topDocs.scoreDocs[i];
                final Document document = searcher.storedFields().document(scoreDoc.doc);
                System.out.println((i + 1) + ". " + document.get("isbn") + "\t" + document.get("title"));

                final IndexableField indexableField = document.getField("title");
                // System.out.println(indexableField.getCharSequenceValue());
                // System.out.println(indexableField.storedValue().getStringValue());
                // System.out.println(indexableField.stringValue());

                try (TokenStream tokenStream = indexableField.tokenStream(analyzer, null)) {
                    tokenStream.reset();

                    while (tokenStream.incrementToken()) {
                        // PackedTokenAttributeImpl
                        final CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                        final OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
                        final TermFrequencyAttribute termFrequencyAttribute = tokenStream.getAttribute(TermFrequencyAttribute.class);

                        // PayloadAttribute payloadAttribute = tokenStream.addAttribute(PayloadAttribute.class);
                        // BytesRef payload = payloadAttribute.getPayload();
                        // PayloadHelper.encodeFloat(1F, payload.bytes, payload.offset);

                        System.out.printf("[%s, %d, %d, %d]%n", charTermAttribute, offsetAttribute.startOffset(),
                                offsetAttribute.endOffset(), termFrequencyAttribute.getTermFrequency());
                    }

                    tokenStream.end();
                }
            }
        }

        // TokenStream ts = analyzer.tokenStream(field, content);

        index.close();
    }

    private static void addDoc(final IndexWriter indexWriter, final String title, final String isbn) throws IOException {
        final Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));

        // Use a string field for isbn because we don't want it tokenized.
        doc.add(new StringField("isbn", isbn, Field.Store.YES));

        indexWriter.addDocument(doc);
    }

    private LuceneDemo() {
        super();
    }
}
