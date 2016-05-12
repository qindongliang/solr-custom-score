package com.easy.custom.queryparser;

import org.apache.lucene.index.*;
import org.apache.lucene.queries.CustomScoreProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by qindongliang on 2016/5/9.
 */
public class MyProvider extends CustomScoreProvider {

    private List<String> params;
    final static Logger log= LoggerFactory.getLogger(MyProvider.class);
    //配置参数
    public void setParams(List<String> params) {
        this.params = params;
    }

    public MyProvider(LeafReaderContext context) {
        super(context);
    }

    @Override
    public float customScore(int doc, float subQueryScore, float valSrcScore) throws IOException {
        //直接从valuesource中读取数据
        final NumericDocValues year = DocValues.getNumeric(this.context.reader(), "easy_year");
        final NumericDocValues money = DocValues.getNumeric(this.context.reader(), "easy_money");

        //一段demo，小于2010年的加分，大于500万资金的加分
        float year_score=1;
        float money_socre=1;
         if(year.get(doc)<2010){
            year_score=5;
         }
        double m=Double.longBitsToDouble(money.get(doc));
        if(m>600){
            money_socre=7;
        }

        /*
         * 通过得分相乘放大分数
         * 此处可以控制与原有得分结合的方式，加减乘除都可以
         * **/

        log.info("前台参数：大小:{}  内容:{} ",params.size(),params);
        log.info("查询一次：docid:{} year:{} money:{} 1score:{} 2score:{} year_score:{} money_score:{} ",
                doc,year.get(doc),m
                ,subQueryScore,valSrcScore,year_score,money_socre);
        return  subQueryScore*valSrcScore*year_score*money_socre;
    }

}
