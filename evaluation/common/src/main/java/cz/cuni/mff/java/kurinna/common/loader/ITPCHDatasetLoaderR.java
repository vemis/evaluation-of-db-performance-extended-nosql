package cz.cuni.mff.java.kurinna.common.loader;

/**
 * Interface for loading TPC-H relational-model tables into a NoSQL store.
 *
 * Implementations supply the ODM-specific persistence logic (Morphia, Morphium,
 * Spring Data MongoDB, …) while this contract guarantees every future NoSQL
 * microservice exposes the same loading surface.
 *
 * Each method reads the corresponding TPC-H pipe-delimited {@code .tbl} file
 * from the given {@code filePath} and persists all records into the underlying
 * store.  Call order matters only for referential integrity checking (if any);
 * the recommended order is: regions → nations → customers → orders →
 * lineitems → partsupps → parts → suppliers.
 */
public interface ITPCHDatasetLoaderR {

    void loadRegions(String filePath);

    void loadNations(String filePath);

    void loadCustomers(String filePath);

    void loadOrders(String filePath);

    void loadLineitems(String filePath);

    void loadPartsupps(String filePath);

    void loadParts(String filePath);

    void loadSuppliers(String filePath);

    /**
     * Convenience method that loads all eight tables from the given directory.
     * The directory must contain the standard TPC-H {@code .tbl} files.
     *
     * @param dataDirectory path to the directory containing the {@code .tbl} files
     */
    default void loadAll(String dataDirectory) {
        String sep = dataDirectory.endsWith("/") ? "" : "/";
        loadRegions(dataDirectory + sep + "region.tbl");
        System.gc();
        loadNations(dataDirectory + sep + "nation.tbl");
        System.gc();
        loadCustomers(dataDirectory + sep + "customer.tbl");
        System.gc();
        loadOrders(dataDirectory + sep + "orders.tbl");
        System.gc();
        loadLineitems(dataDirectory + sep + "lineitem.tbl");
        System.gc();
        loadPartsupps(dataDirectory + sep + "partsupp.tbl");
        System.gc();
        loadParts(dataDirectory + sep + "part.tbl");
        System.gc();
        loadSuppliers(dataDirectory + sep + "supplier.tbl");
        System.gc();
    }
}
