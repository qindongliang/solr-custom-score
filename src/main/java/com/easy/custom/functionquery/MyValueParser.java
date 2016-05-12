package com.easy.custom.functionquery;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by qindongliang on 2016/5/11.
 */
public class MyValueParser extends ValueSourceParser {


    final static Logger log= LoggerFactory.getLogger(MyValueParser.class);

    //接受传过来的参数
    List<String> params;

    public void init(NamedList args) {

        //得到一个映射之后，转成NamedList便于操作
        params=((NamedList)args.get("words")).getAll("word");
        if(params.isEmpty()){
            throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,"至少有一个参数");
        }

        // handle configuration parameters
        // passed through solrconfig.xml
        for(String pm:params){
            log.info("函数查询参数：{}",pm);
        }
        this.params=params;
        log.info("函数查询的前台参数： size:{}  info:{}",params.size(),params.toString());

    }

    @Override
    public ValueSource parse(FunctionQParser fq) throws SyntaxError {


        //log.info("参数啥东西：{} ",fq.getString());
        //得到的结果如下
        //参数啥东西：myfunc(easy_year,easy_money)



//        log.info("参数啥东西：{}",functionQParser.getParams().getParameterNamesIterator().toString());

//         String year=functionQParser.parseArg();
//        String money=functionQParser.parseArg();
//        log.info("进来就看有几个valuesource：{} ",functionQParser.parseValueSourceList().size());
//        log.info("进来就看有几个valuesource：{} ",functionQParser.parseValueSourceList().size());






//        log.info("函数的前台几个字段：{} , value list长度：{}",year+","+money,vs.size());

        return new FunctionValueSource(params,fq.parseValueSourceList());
    }
}
