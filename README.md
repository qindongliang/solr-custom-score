# solr-custom-score
solr自定义评分组件demo

### （一）背景介绍
大多数时候我们使用lucene/solr/elasticsearch自带的评分查询都是没问题的，当然这也仅仅限于简单的业务或者对搜索排名
不敏感的场景中，假设业务方要求有若干业务因子要干扰到排名，同时还不能放弃框架本身的文本相似度评分，那么应该怎么做呢？
 这种场景尤其是在电商类的一些垂直搜索中体现比较明显，比如，新商品加分，口碑好的加分，图片清晰的加分，商品介绍详细的加分，大促的商品加分等等等等
如何把众多的业务因素加到的总的评分里面?

###（二）实现策略
（1）在索引的时候把众多的业务评分计算提前计算好，存储成一个字段，然后查询的时候根据这个字段排名<br/>

评价：比较简单暴力，适合加权固定，不经常改变评分因素的业务使用，查询性能最高  ，更新，改变，调试评分比较麻烦<br/>

（2）在索引的时候把众多的业务评分因子都索引成一个字段，在查询的时候动态获取各个字段评分计算后，加入总的评分从而影响
最终的排名<br/>

评价：对动态更改评分支持比较好，查询性能稍差<br/>

（3）对于业务的评分因素，动静分离，静态评分因子长期不变的，就全部计算完存储成一个字段，动态的也存储若干字段，最终的评分
由静态业务评分+动态业务评分+相似度评分综合得出，从而影响最终的排名<br/>

评价：合理规划评分因素，动静分离，算是业务与技术的一个折中<br/>

###（三）实现方式
#####A : 通过重写QueryParser实现
1，继承CustomScoreProvider类，重写customScore方法，从DocValues中动态读取评分有关因子，计算后，影响总评分<br/>
2，继承CustomScoreQuery类，重写getCustomScoreProvider方法，需要用到1，返回自定义的CustomScoreProvider类
到此，在lucene中就完事了，但是在solr中我们还需要继续<br/>
3，继承QParser类，重写parse方法，需要用到2，并在构造方法中，完成一些必须的初始化操作<br/>
4，继承QParserPlugin类，重写createParser方法，需要用到3，至此，代码完成<br/>
打包项目成一个jar，拷贝至server\solr-webapp\webapp\WEB-INF\lib中<br/>
5，在solrconfig.xml中，注册我们写的插件：<br/>

````java
<queryParser name="myqp" class="com.easy.custom.queryparser.MyQueryParserPlugin">
  <lst name="words">
       <str name="word">easy_money</str>
       <str name="word">easy_count</str>
       <str name="word">easy_test</str>
     </lst>
</queryParser>
````
6， 重启solr，或者reload指定的core<br/>
7，打开solr的ui页面，指定defType，测试搜索，如果log不报错，就证明使用成功了<br/>
![测试搜索](http://dl2.iteye.com/upload/attachment/0117/3784/63af55df-80c2-3f02-bacc-3328850475fb.png) 

#####B : 通过重写Function Query实现
1，继承ValueSource类，重写getValues方法，并在返回的方法中，完成评分计算逻辑<br/>
2，继承ValueSourceParser类，并重写parser方法，返回1定义的类，
建议在parser方法里面，获取ValueSource然后传入自定义的ValueSource类里面复用，
不建议直接从DocValues里面读取，因为基于这个IndexSearch的打开的ValueSource耗费
资源更少。至此，代码完成打包项目成一个jar，拷贝至server\solr-webapp\webapp\WEB-INF\lib中<br/>

3，在solrconfig.xml中，注册我们的组件：<br/>
````java
//此处，也可也定义需要传入的参数
<valueSourceParser name="myfunc" class="com.easy.custom.function.MyValueParser"  />   
````
4，打开solr的ui页面进行查询，不报错的话，即查询成功，可以看到和我们第一种方式的结果是一致的<br/>
![测试搜索](http://dl2.iteye.com/upload/attachment/0117/3786/873f910b-3f7e-3b20-97bd-7aa95934ec52.png) 

###（四）总结
其实核心功能还是使用lucene实现的，solr/es则是在lucene的基础上提供了强大灵活的插件机制，这样以来，我们就能更容易实现一些我们特殊需求的定制化。 
###（五）  公众号：我是攻城师（woshigcs） 如有问题，可在后台留言咨询
