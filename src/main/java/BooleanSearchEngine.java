import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    Map<String, List<PageEntry>> results;

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        // прочтите тут все pdf и сохраните нужные данные,
        // тк во время поиска сервер не должен уже читать файлы
        File[] files = pdfsDir.listFiles();
        results = new HashMap<>();
        for (File file : files) {
            var doc = new PdfDocument(new PdfReader(file));
            for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                String text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                Map<String, Integer> freqs = new HashMap<>(); // мапа, где ключом будет слово, а значением - частота
                String[] words = text.split("\\P{IsAlphabetic}+");
                for (var word : words) { // перебираем слова
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }
                for (Map.Entry<String, Integer> freq : freqs.entrySet()) {
                    List<PageEntry> pageEntries = new ArrayList<>();
                    PageEntry pageEntry = new PageEntry(file.getName(), i, freq.getValue());
                    if (results.containsKey(freq.getKey())) {
                        results.get(freq.getKey()).add(pageEntry);
                    } else {

                        pageEntries.add(pageEntry);
                        results.put(freq.getKey(), pageEntries);
                    }
                }
            }
            for (Map.Entry<String, List<PageEntry>> res : results.entrySet()) {
                Collections.sort(res.getValue());
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        // тут реализуйте поиск по слову
        return results.get(word);
    }
}
