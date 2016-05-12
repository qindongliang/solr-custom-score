package com.easy.custom.queryparser;

import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;

import java.util.List;

/**
 * Created by qindongliang on 2016/5/9.
 */
public class MyParser extends QParser {

    private Query innerQuery;

    private List<String> params;


    @Override
    public Query parse() throws SyntaxError {
        return new MyQuery(params,innerQuery);
    }


    public MyParser(List<String> pfield,String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        super(qstr, localParams, params, req);
        try {
            QParser parser = getParser(qstr, "lucene", getReq());
            this.innerQuery = parser.parse();
        } catch (SyntaxError ex) {
            throw new RuntimeException("error parsing query", ex);
        }
        this.params=pfield;
    }



}
