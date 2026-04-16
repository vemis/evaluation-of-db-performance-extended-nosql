using MongoDB.Entities;
using System;

namespace MongoDBEntities.Models.TPC_H
{
    public class LineitemE
    {
        public string l_id { get; set; }
        public int l_orderkey { get; set; }
        public int l_partkey { get; set; }
        public int l_suppkey { get; set; }
        public string l_ps_id { get; set; }

        public int l_linenumber { get; set; }
        public int l_quantity { get; set; }

        public double l_extendedprice { get; set; }
        public double l_discount { get; set; }
        public double l_tax { get; set; }
        public string l_returnflag { get; set; }
        public string l_linestatus { get; set; }
        public Date l_shipdate { get; set; }
        public Date l_commitdate { get; set; }
        public Date l_receiptdate { get; set; }
        public string l_shipinstruct { get; set; }
        public string l_shipmode { get; set; }
        public string l_comment { get; set; }

        public LineitemE(string[] row) : this(
            Convert.ToInt32(row[0]),
            Convert.ToInt32(row[1]),
            Convert.ToInt32(row[2]),
            Convert.ToInt32(row[3]),
            Convert.ToInt32(row[4]),
            Convert.ToDouble(row[5]),
            Convert.ToDouble(row[6]),
            Convert.ToDouble(row[7]),
            row[8],
            row[9],
            new Date(DateTime.Parse(row[10])),
            new Date(DateTime.Parse(row[11])),
            new Date(DateTime.Parse(row[12])),
            row[13],
            row[14],
            row[15])
        { }

        public LineitemE(int l_orderkey, int l_partkey, int l_suppkey, int l_linenumber, int l_quantity,
            double l_extendedprice, double l_discount, double l_tax,
            string l_returnflag, string l_linestatus,
            Date l_shipdate, Date l_commitdate, Date l_receiptdate,
            string l_shipinstruct, string l_shipmode, string l_comment)
        {
            this.l_ps_id = l_partkey.ToString() + "|" + l_suppkey.ToString();
            this.l_id = l_orderkey.ToString() + l_linenumber.ToString();
            this.l_orderkey = l_orderkey;
            this.l_partkey = l_partkey;
            this.l_suppkey = l_suppkey;
            this.l_linenumber = l_linenumber;
            this.l_quantity = l_quantity;
            this.l_extendedprice = l_extendedprice;
            this.l_discount = l_discount;
            this.l_tax = l_tax;
            this.l_returnflag = l_returnflag;
            this.l_linestatus = l_linestatus;
            this.l_shipdate = l_shipdate;
            this.l_commitdate = l_commitdate;
            this.l_receiptdate = l_receiptdate;
            this.l_shipinstruct = l_shipinstruct;
            this.l_shipmode = l_shipmode;
            this.l_comment = l_comment;
        }
    }
}
