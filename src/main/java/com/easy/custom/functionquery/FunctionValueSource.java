package com.easy.custom.functionquery;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by qindongliang on 2016/5/11.
 */
public class FunctionValueSource extends ValueSource {
    final static Logger log= LoggerFactory.getLogger(FunctionValueSource.class);
    List<String> params;

    private  List<ValueSource>  valueSources;


    public FunctionValueSource(List<String> params,List<ValueSource> source) {
        this.params = params;
        this.valueSources=source;
    }

    @Override
    public FunctionValues getValues(Map map, final LeafReaderContext leafReaderContext) throws IOException {

        final FunctionValues y=this.valueSources.get(0).getValues(map,leafReaderContext);
        final FunctionValues m=this.valueSources.get(1).getValues(map,leafReaderContext);
        return new FloatDocValues(this) {
            @Override
            public float floatVal(int i) {

//                  NumericDocValues year=null;
//                  NumericDocValues money=null;


//                    year = DocValues.getNumeric(leafReaderContext.reader(), "easy_year");
                    long year = y.longVal(i);
                    double money=m.doubleVal(i);
//                    log.info("函数查询这啥东西：{} , {}",year,money);
//                    money = DocValues.getNumeric(leafReaderContext.reader(), "easy_money");

                    float year_score = 1;
                    float money_socre = 1;
                    if (year< 2010) {
                        year_score = 5;
                    }

                    if (money > 600) {
                        money_socre = 7;
                    }

                log.info("函数查询数据： docid:{} year:{} money:{} year_score:{} money_score:{} "
                ,i,year,money,year_score,money_socre);
                return year_score*money_socre;
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String description() {
        return null;
    }
}
