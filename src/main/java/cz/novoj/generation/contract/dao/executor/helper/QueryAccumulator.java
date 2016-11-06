package cz.novoj.generation.contract.dao.executor.helper;

import cz.novoj.generation.contract.dao.keyword.Keyword;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Rodina Novotnych on 06.11.2016.
 */
@Data
class QueryAccumulator {
    private final KeywordInstanceAccumulator[] accumulators;
    private final List<String> unrecognizedWords = new LinkedList<>();
    private KeywordInstanceAccumulator activeAccumulator;

    QueryAccumulator(KeywordInstanceAccumulator... accumulators) {
        this.accumulators = accumulators;
        this.activeAccumulator = null;
    }

    void switchAccumulator(Keyword.Kind kind) {
        for (KeywordInstanceAccumulator accumulator : accumulators) {
            if (accumulator.getKind() == kind) {
                this.activeAccumulator = accumulator;
            }
        }
    }

}
