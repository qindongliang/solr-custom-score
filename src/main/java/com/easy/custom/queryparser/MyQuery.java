package com.easy.custom.queryparser;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.util.List;

/**
 * Created by qindongliang on 2016/5/9.
 */
public class MyQuery extends CustomScoreQuery {
    private List<String> params;

    public MyQuery(List<String> params,Query subQuery) {
        super(subQuery);
        this.params=params;
    }

    @Override
    protected CustomScoreProvider getCustomScoreProvider(LeafReaderContext context) throws IOException {
        //此处返回，定义的Provider
        MyProvider provider=new MyProvider(context);
        provider.setParams(params);
        return provider;
    }
}
