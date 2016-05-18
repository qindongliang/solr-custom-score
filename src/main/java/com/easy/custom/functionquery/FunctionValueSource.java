package com.easy.custom.functionquery;

import com.easy.custom.tools.ScoreTools;
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


    private  List<ValueSource>  valueSources;
    //限制最大年份
    private int maxYears;
    //限制最大资金倍数，超过   money_basex倍数的钱，统一按成一个分数算
    private int money_maxTimes;
    //资金基数
    private int money_base;


    public FunctionValueSource(int maxYears,int money_maxTimes,int money_base,List<ValueSource> source) {
        this.maxYears=maxYears;
        this.money_base=money_base;
        this.money_maxTimes=money_maxTimes;
        this.valueSources=source;
    }

    @Override
    public FunctionValues getValues(Map map, final LeafReaderContext leafReaderContext) throws IOException {

        final FunctionValues y=this.valueSources.get(0).getValues(map,leafReaderContext);
        final FunctionValues m=this.valueSources.get(1).getValues(map,leafReaderContext);
        return new FloatDocValues(this) {
            @Override
            public float floatVal(int i) {
                    long year = y.longVal(i);
                    double money=m.doubleVal(i);
                    float year_score = ScoreTools.getYearScore(year,maxYears);
                    float money_socre = ScoreTools.getMoneyScore(money,money_maxTimes,money_base);
//                log.info("得分详情：year:{} money:{} year_score:{} money_score:{} total:{}"
//                ,year
//                ,money,year_score,money_socre
//                ,year_score*money_socre);
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
