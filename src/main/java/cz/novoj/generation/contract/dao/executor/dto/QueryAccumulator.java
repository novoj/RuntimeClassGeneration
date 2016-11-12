package cz.novoj.generation.contract.dao.executor.dto;

import cz.novoj.generation.contract.dao.query.keyword.Keyword.Purpose;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * State object for the parsing state machine.
 */
@Data
public class QueryAccumulator {
    private final QueryNodeAccumulator[] accumulators;
    private final List<String> unrecognizedWords = new LinkedList<>();
    private QueryNodeAccumulator activeAccumulator;

    public QueryAccumulator(QueryNodeAccumulator... accumulators) {
        this.accumulators = accumulators;
        this.activeAccumulator = null;
    }

    public void switchAccumulator(Purpose purpose) {
        for (QueryNodeAccumulator accumulator : accumulators) {
            if (accumulator.getPurpose() == purpose) {
                this.activeAccumulator = accumulator;
            }
        }
    }

}
