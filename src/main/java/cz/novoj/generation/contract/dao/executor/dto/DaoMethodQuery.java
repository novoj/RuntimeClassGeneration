package cz.novoj.generation.contract.dao.executor.dto;

import cz.novoj.generation.contract.dao.query.instance.QueryNode;
import lombok.Getter;

/**
 * Created by Rodina Novotnych on 06.11.2016.
 */
public class DaoMethodQuery {
    @Getter private QueryNode filter;
    @Getter private QueryNode sort;

    public DaoMethodQuery(QueryNodeAccumulator... accumulators) {
        for (QueryNodeAccumulator accumulator : accumulators) {
            switch (accumulator.getKind()) {
                case Filter: filter = accumulator.getFinalKeyword(); break;
                case Sort: sort = accumulator.getFinalKeyword(); break;
            }
        }
    }
}
