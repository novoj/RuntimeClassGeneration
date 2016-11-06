package cz.novoj.generation.contract.dao.query.instance;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public interface QueryNodeVisitor {

    void accept(LeafQueryNode keywordInstance);

    void accept(ContainerQueryNode keywordInstance);

}
