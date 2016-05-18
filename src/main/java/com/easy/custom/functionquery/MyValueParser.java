package com.easy.custom.functionquery;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by qindongliang on 2016/5/11.
 */
public class MyValueParser extends ValueSourceParser {


    final static Logger log= LoggerFactory.getLogger(MyValueParser.class);

    //接受传过来的参数
    private int maxYears;
    private int money_maxTimes;
    private int money_base;

    // handle configuration parameters
    // passed through solrconfig.xml
    public void init(NamedList args) {
        //得到一个映射之后，转成NamedList便于操作
        maxYears=(Integer) ((NamedList)args.get("strategy")).get("maxYears");
        money_maxTimes=(Integer) ((NamedList)args.get("strategy")).get("money_maxTimes");
        money_base=(Integer) ((NamedList)args.get("strategy")).get("money_base");
        log.info("初始化加权因子参数：  maxYears:{} money_maxTimes:{}  money_base:{} ",maxYears,money_maxTimes,money_base);
    }

    @Override
    public ValueSource parse(FunctionQParser fq) throws SyntaxError {
        return new FunctionValueSource(maxYears,money_maxTimes,money_base,fq.parseValueSourceList());
    }
}
