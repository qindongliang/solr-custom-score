package com.easy.custom.handler;

import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * Created by qindongliang on 2016/5/11.
 */
public class MyHandler  extends RequestHandlerBase {

    final static Logger log= LoggerFactory.getLogger(MyHandler.class);
    //统计指标，请求次数
    volatile long numRequests;
    //统计指标，总耗时
    volatile long totalTime;
    //统计指标，总请求出错次数
    volatile long numErrors;

    //从solrconfig.xml里面传过来的参数
    List<String> words;

    @Override
    public void init(NamedList args) {

        words=((NamedList)args.get("words")).getAll("word");
        if(words.isEmpty()){
            throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,"至少有一个参数");
        }
        super.init(args);
    }

    @Override
    public void handleRequestBody(SolrQueryRequest solrQueryRequest, SolrQueryResponse solrQueryResponse) throws Exception {
        numRequests++;
        long startTime=System.currentTimeMillis();
        try{
            HashMap<String,Integer> counts=new HashMap<String, Integer>();
            SolrParams params=solrQueryRequest.getParams();
            String q=params.get(CommonParams.Q);//从url中得到q参数

            for(String text:q.split(" ")){
                if(words.contains(text)){

                    Integer ct=counts.containsKey(text)?counts.get(text):0;
                    counts.put(text,ct+1);

                }else{
                    log.warn("过滤掉此单词：{}",text);
                }

            }

            NamedList<Integer> re=new NamedList<Integer>();
            for(String word:words){
                re.add(word,counts.get(word));
            }

            solrQueryResponse.add("results",re);

        }catch (Exception e){
            numErrors++;
            log.error("统计出错: ",e.getMessage());
        }finally {
            totalTime+=System.currentTimeMillis()-startTime;
        }

    }

    @Override
    public String getDescription() {
        return "三劫散仙";
    }


    @Override
    public String getVersion() {
        return "1.0";
    }


    @Override
    public String getSource() {
        return "http://qindongliang.iteye.com/";
    }

    @Override
    public NamedList<Object> getStatistics() {

        NamedList all=new SimpleOrderedMap();
        all.add("requests count",numRequests);
        all.add("errors count",numErrors);
        all.add("totalTimes(ms)",totalTime);

        return all;
    }
}
