package cz.novoj.generation.contract.dao.executor.helper;

import cz.novoj.generation.contract.dao.keyword.instance.KeywordInstance;
import lombok.Getter;

/**
 * Created by Rodina Novotnych on 06.11.2016.
 */
public class MethodQuery {
    @Getter private KeywordInstance filter;
    @Getter private KeywordInstance sort;

    MethodQuery(KeywordInstanceAccumulator... accumulators) {
        for (KeywordInstanceAccumulator accumulator : accumulators) {
            switch (accumulator.getKind()) {
                case Filter:
                    filter = accumulator.getFinalKeyword();
                    break;
                case Sort:
                    sort = accumulator.getFinalKeyword();
                    break;
            }
        }
    }
}
