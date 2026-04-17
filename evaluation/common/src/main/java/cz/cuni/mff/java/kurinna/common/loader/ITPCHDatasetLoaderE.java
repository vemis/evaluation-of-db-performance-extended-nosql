package cz.cuni.mff.java.kurinna.common.loader;

/**
 * Interface for loading TPC-H embedded-model collections into a NoSQL store.
 *
 * Each method receives the root data directory containing the standard TPC-H
 * {@code .tbl} files and builds the paths to individual files internally.
 * Methods that require joining multiple source files (e.g. orders + lineitems)
 * read all necessary files themselves.
 *
 * Implementations supply the ODM-specific persistence logic while this
 * contract guarantees every future document-model microservice exposes the
 * same loading surface.
 */
public interface ITPCHDatasetLoaderE {

    /** Loads orders embedded with their full lineitems list (used by R1, R2, R8, R9). */
    void loadOrdersEWithLineitems(String dataDirectory);

    /** Loads orders with a shuffled array of lineitem field values as tags (used by R3). */
    void loadOrdersEWithLineitemsArrayAsTags(String dataDirectory);

    /** Loads the indexed variant of orders with lineitem tags (used by R4). */
    void loadOrdersEWithLineitemsArrayAsTagsIndexed(String dataDirectory);

    /** Loads orders embedded with their full customer→nation→region chain (used by R5). */
    void loadOrdersEWithCustomerWithNationWithRegion(String dataDirectory);

    /** Loads minimal order documents containing only orderkey, orderdate, comment (used by R6). */
    void loadOrdersEOnlyOComment(String dataDirectory);

    /** Loads the text-indexed variant of minimal order comment documents (used by R7). */
    void loadOrdersEOnlyOCommentIndexed(String dataDirectory);

    /**
     * Loads all six embedded collections.
     * Light collections are loaded first; the heavyweight {@code ordersEWithLineitems}
     * collection is loaded last.
     */
    default void loadAll(String dataDirectory) {
        loadOrdersEOnlyOComment(dataDirectory);
        loadOrdersEOnlyOCommentIndexed(dataDirectory);
        loadOrdersEWithLineitemsArrayAsTags(dataDirectory);
        loadOrdersEWithLineitemsArrayAsTagsIndexed(dataDirectory);
        loadOrdersEWithCustomerWithNationWithRegion(dataDirectory);
        loadOrdersEWithLineitems(dataDirectory);
    }
}
