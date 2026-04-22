package sample.wordfrequencyanalyzer.utils;

import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.postag.POSModel;
import opennlp.tools.tokenize.TokenizerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.controllers.MainController;

import java.io.InputStream;

public class PhrasalDetector {
    public final POSTaggerME posTagger;
    private final TokenizerME tokenizer;
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    public PhrasalDetector() {
        try {
            posTagger = new POSTaggerME(loadPOSModel());
            tokenizer = new TokenizerME(loadTokenModel());
        } catch (Exception e) {
            throw new RuntimeException("Загрузите модели OpenNLP!", e);
        }
    }

    private POSModel loadPOSModel() throws Exception {
        try (InputStream modelIn = getClass().getResourceAsStream("/models/opennlp-en-ud-ewt-pos-1.3-2.5.4.bin")) {
            if (modelIn == null) {
                throw new RuntimeException("❌ НЕ НАЙДЕН: модуль ТЭГИРОВАНИЯ /models/opennlp-en-ud-ewt-pos-1.3-2.5.4.bin");
            }
            return new POSModel(modelIn);
        }
    }

    private TokenizerModel loadTokenModel() throws Exception {
        try (InputStream modelIn = getClass().getResourceAsStream("/models/opennlp-en-ud-ewt-tokens-1.3-2.5.4.bin")) {
            if (modelIn == null) {
                throw new RuntimeException("❌ НЕ НАЙДЕН: модуль ТОКЕНИЗАЦИИ /models/opennlp-en-ud-ewt-tokens-1.3-2.5.4.bin");
            }
            return new TokenizerModel(modelIn);
        }
    }

    public boolean isPhrasal(String str) {
        if (str == null || str.trim().isEmpty()) return false;

        String[] tokens = tokenizer.tokenize(str);

        // Если не 2 и не 3 слова, это точно не фраз. глагол
        if (tokens.length != 2 && tokens.length != 3) return false;

        String[] tags = posTagger.tag(tokens);

//        logger.debug("\nСТРОКА: " + str + "\nТОКЕНЫ: " + Arrays.toString(tokens) +
//                    "\nТЭГИ: " + Arrays.toString(tags) + "\nКОЛ-ВО СЛОВ: " + tokens.length);

        if (tokens.length == 2) {
            return  twoWordsInPhrasalVerb(tags, tokens);
        } else {
            return threeWordsInPhrasalVerb(tags);
        }

    }

    private boolean twoWordsInPhrasalVerb (String[] tags, String[] tokens) {
        // Проверка, чтобы точно знать, что это фразовая частица, а не drive fast условно
        if ("VERB".equals(tags[0]) && "ADV".equals(tags[1])) {
            return PhrasalParticle.isPhrasalParticle(tokens[1]);
        } else {
            return "VERB".equals(tags[0]) && "ADP".equals(tags[1])
                    || "NOUN".equals(tags[0]) && "ADP".equals(tags[1]);
        }

    }

    private boolean threeWordsInPhrasalVerb (String[] tags) {
        return ("VERB".equals(tags[0]) || "NOUN".equals(tags[0])) && "ADP".equals(tags[1]) && "ADP".equals(tags[2])
                || "VERB".equals(tags[0]) && "ADV".equals(tags[1]) && "ADP".equals(tags[2]);
    }

}
