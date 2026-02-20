package cz.cuni.mff.mongodb_java.morphia;

import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational.LineitemR;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational.OrdersR;
import dev.morphia.Datastore;

import javax.sound.sampled.Line;
import java.util.List;

public class QueriesR {
    /**
     * A1) Non-Indexed Columns
     *
     * This query selects all records from the lineitem table
     * ```sql
     *         SELECT * FROM lineitem;
     * ```
     */
    public static List<LineitemR> A1(Datastore datastore) {
        List<LineitemR> a1 = datastore
                .find(LineitemR.class)
                .iterator()
                .toList();

        return a1;
    }

    public static List<OrdersR> A2(Datastore datastore) {
        List<OrdersR> a2 = datastore
                .find(OrdersR.class)
                .filter()
                .iterator()
                .toList();

        return a2;
    }


    /*
    * ### A1) Non-Indexed Columns

This query selects all records from the lineitem table
```sql
SELECT * FROM lineitem;
```
### A2) Non-Indexed Columns — Range Query

This query selects all records from the orders table where the order date is between '1996-01-01' and '1996-12-31'
```sql
SELECT * FROM orders
WHERE o_orderdate
    BETWEEN '1996-01-01' AND '1996-12-31';
```

### A3) Indexed Columns

This query selects all records from the customer table
```sql
SELECT * FROM customer;
```

### A4) Indexed Columns — Range Query

This query selects all records from the orders table where the order key is between 1000 and 50000
```sql
SELECT * FROM orders
WHERE o_orderkey BETWEEN 1000 AND 50000;
```
    * */
}
