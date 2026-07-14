package cz.cuni.mff.java.kurinna.microservice.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class UniversalRepositoryImpl implements UniversalRepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    // A1) Non-Indexed Columns
    @Override
    public List<Object[]> a1() {
        String sql = "SELECT * FROM lineitem";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    // A2) Non-Indexed Columns — Range Query
    @Override
    public List<Object[]> a2(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM orders WHERE o_orderdate BETWEEN ?1 AND ?2";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, java.sql.Date.valueOf(startDate));
        query.setParameter(2, java.sql.Date.valueOf(endDate));
        return query.getResultList();
    }

    // A3) Indexed Columns
    @Override
    public List<Object[]> a3() {
        String sql = "SELECT * FROM customer";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    // A4) Indexed Columns — Range Query
    @Override
    public List<Object[]> a4(int minOrderKey, int maxOrderKey) {
        String sql = "SELECT * FROM orders WHERE o_orderkey BETWEEN ?1 AND ?2";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, minOrderKey);
        query.setParameter(2, maxOrderKey);
        return query.getResultList();
    }

    // B1) COUNT
    @Override
    public List<Object[]> b1() {
        String sql = "SELECT COUNT(o.o_orderkey) AS order_count, " +
                "DATE_FORMAT(o.o_orderdate, '%Y-%m') AS order_month " +
                "FROM orders o " +
                "GROUP BY order_month";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    // B2) MAX
    @Override
    public List<Object[]> b2() {
        String sql = "SELECT DATE_FORMAT(l.l_shipdate, '%Y-%m') AS ship_month, " +
                "MAX(l.l_extendedprice) AS max_price " +
                "FROM lineitem l " +
                "GROUP BY ship_month";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    // C1) Non-Indexed Columns
    @Override
    public List<Object[]> c1() {
        String sql = "SELECT c.c_name, o.o_orderdate, o.o_totalprice " +
                "FROM customer c, orders o " +
                "LIMIT 1500000";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    // C2) Indexed Columns
    @Override
    public List<Object[]> c2() {
        String sql = "SELECT c.c_name, o.o_orderdate, o.o_totalprice " +
                "FROM customer c " +
                "JOIN orders o ON c.c_custkey = o.o_custkey";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    // C3) Complex Join 1
    @Override
    public List<Object[]> c3() {
        String sql = "SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice " +
                "FROM customer c " +
                "JOIN nation n ON c.c_nationkey = n.n_nationkey " +
                "JOIN orders o ON c.c_custkey = o.o_custkey";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    // C4) Complex Join 2
    @Override
    public List<Object[]> c4() {
        String sql = "SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice " +
                "FROM customer c " +
                "JOIN nation n ON c.c_nationkey = n.n_nationkey " +
                "JOIN region r ON n.n_regionkey = r.r_regionkey " +
                "JOIN orders o ON c.c_custkey = o.o_custkey";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    // C5) Left Outer Join
    @Override
    public List<Object[]> c5() {
        String sql = "SELECT c.c_custkey, c.c_name, o.o_orderkey, o.o_orderdate " +
                "FROM customer c " +
                "LEFT OUTER JOIN orders o ON c.c_custkey = o.o_custkey";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    // D1) UNION
    @Override
    public List<Object[]> d1() {
        String sql = "(SELECT c_nationkey AS nationkey FROM customer) " +
                "UNION " +
                "(SELECT s_nationkey AS nationkey FROM supplier)";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    // D2) INTERSECT
    @Override
    public List<Object[]> d2() {
        String sql = "SELECT DISTINCT c.c_custkey AS custkey " +
                "FROM customer c " +
                "WHERE c.c_custkey IN (SELECT s.s_suppkey FROM supplier s)";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    // D3) DIFFERENCE
    @Override
    public List<Object[]> d3() {
        String sql = "SELECT DISTINCT c.c_custkey AS custkey " +
                "FROM customer c " +
                "WHERE c.c_custkey NOT IN (SELECT DISTINCT s.s_suppkey FROM supplier s)";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    // E1) Non-Indexed Columns Sorting
    @Override
    public List<Object[]> e1() {
        String sql = "SELECT c_name, c_address, c_acctbal " +
                "FROM customer " +
                "ORDER BY c_acctbal DESC";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    // E2) Indexed Columns Sorting
    @Override
    public List<Object[]> e2() {
        String sql = "SELECT o_orderkey, o_custkey, o_orderdate, o_totalprice " +
                "FROM orders " +
                "ORDER BY o_orderkey";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    // E3) Distinct
    @Override
    public List<Object[]> e3() {
        String sql = "SELECT DISTINCT c_nationkey, c_mktsegment " +
                "FROM customer";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    @Override
    public List<Object[]> q1(int days) {
        String sql = "SELECT " +
                "l.l_returnflag, " +
                "l.l_linestatus, " +
                "SUM(l.l_quantity) AS sum_quantity, " +
                "SUM(l.l_extendedprice) AS sum_extended_price, " +
                "SUM(l.l_extendedprice * (1 - l.l_discount)) AS sum_net_price, " +
                "SUM(l.l_extendedprice * (1 - l.l_discount) * (1 + l.l_tax)) AS sum_net_profit, " +
                "l.l_shipdate " +
                "FROM lineitem l " +
                "WHERE l.l_shipdate <= DATE_ADD(CURDATE(), INTERVAL ?1 DAY) " +
                "GROUP BY l.l_returnflag, l.l_linestatus, l.l_shipdate";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, days);

        return query.getResultList();
    }

    @Override
    public List<Object[]> q2(int size, String type, String region) {
        String sql = "SELECT " +
                "s.s_acctbal, " +
                "s.s_name, " +
                "n.n_name, " +
                "p.p_partkey, " +
                "p.p_mfgr, " +
                "s.s_address, " +
                "s.s_phone, " +
                "s.s_comment " +
                "FROM " +
                "part p, " +
                "supplier s, " +
                "partsupp ps, " +
                "nation n, " +
                "region r " +
                "WHERE " +
                "p.p_partkey = ps.ps_partkey " +
                "AND s.s_suppkey = ps.ps_suppkey " +
                "AND p.p_size = ?1 " +
                "AND p.p_type LIKE ?2 " +
                "AND s.s_nationkey = n.n_nationkey " +
                "AND n.n_regionkey = r.r_regionkey " +
                "AND r.r_name = ?3 " +
                "AND ps.ps_supplycost = ( " +
                "    SELECT MIN(ps2.ps_supplycost) " +
                "    FROM " +
                "      partsupp ps2, " +
                "      supplier s2, " +
                "      nation n2, " +
                "      region r2 " +
                "    WHERE " +
                "      p.p_partkey = ps2.ps_partkey " +
                "      AND s2.s_suppkey = ps2.ps_suppkey " +
                "      AND s2.s_nationkey = n2.n_nationkey " +
                "      AND n2.n_regionkey = r2.r_regionkey " +
                "      AND r2.r_name = ?3 " +
                ") " +
                "ORDER BY " +
                "s.s_acctbal DESC, " +
                "n.n_name, " +
                "s.s_name, " +
                "p.p_partkey ";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, size);
        query.setParameter(2, type);
        query.setParameter(3, region);
        return query.getResultList();
    }

    @Override
    public List<Object[]> q3(String segment, LocalDate orderDate, LocalDate shipDate) {
        String sql = "SELECT " +
                "l.l_orderkey, " +
                "SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue, " +
                "o.o_orderdate, " +
                "o.o_shippriority " +
                "FROM " +
                "customer c, " +
                "orders o, " +
                "lineitem l " +
                "WHERE " +
                "c.c_mktsegment = ?1 " +
                "AND c.c_custkey = o.o_custkey " +
                "AND l.l_orderkey = o.o_orderkey " +
                "AND o.o_orderdate < ?2 " +
                "AND l.l_shipdate > ?3 " +
                "GROUP BY " +
                "l.l_orderkey, " +
                "o.o_orderdate, " +
                "o.o_shippriority " +
                "ORDER BY " +
                "revenue DESC, " +
                "o.o_orderdate ";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, segment);
        query.setParameter(2, java.sql.Date.valueOf(orderDate));
        query.setParameter(3, java.sql.Date.valueOf(shipDate));

        return query.getResultList();
    }

    @Override
    public List<Object[]> q4(LocalDate orderDate) {
        String sql = "SELECT " +
                "o.o_orderpriority, " +
                "COUNT(*) AS order_count " +
                "FROM " +
                "orders o " +
                "WHERE " +
                "o.o_orderdate >= ?1 " +
                "AND o.o_orderdate < DATE_ADD(?1, INTERVAL 3 MONTH) " +
                "AND EXISTS ( " +
                "    SELECT * " +
                "    FROM " +
                "        lineitem l " +
                "    WHERE " +
                "        l.l_orderkey = o.o_orderkey " +
                "        AND l.l_commitdate < l.l_receiptdate " +
                ") " +
                "GROUP BY " +
                "o.o_orderpriority " +
                "ORDER BY " +
                "o.o_orderpriority";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, java.sql.Date.valueOf(orderDate));

        return query.getResultList();
    }

    @Override
    public List<Object[]> q5(String region, LocalDate orderDate) {
        String sql = "SELECT " +
                "n.n_name, " +
                "SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue " +
                "FROM " +
                "customer c, " +
                "orders o, " +
                "lineitem l, " +
                "supplier s, " +
                "nation n, " +
                "region r " +
                "WHERE " +
                "c.c_custkey = o.o_custkey " +
                "AND l.l_orderkey = o.o_orderkey " +
                "AND l.l_suppkey = s.s_suppkey " +
                "AND c.c_nationkey = s.s_nationkey " +
                "AND s.s_nationkey = n.n_nationkey " +
                "AND n.n_regionkey = r.r_regionkey " +
                "AND r.r_name = ?1 " +
                "AND o.o_orderdate >= ?2 " +
                "AND o.o_orderdate < DATE_ADD(?2, INTERVAL 1 YEAR) " +
                "GROUP BY " +
                "n.n_name " +
                "ORDER BY " +
                "revenue DESC";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, region);
        query.setParameter(2, java.sql.Date.valueOf(orderDate));

        return query.getResultList();
    }
}
