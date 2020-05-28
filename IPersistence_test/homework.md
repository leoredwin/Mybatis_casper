#1、Mybatis动态sql是做什么的？都有哪些动态sql？简述一下动态sql的执行原理？
---
####Mybatis动态sql可以让我们在Xml映射文件内，以标签的形式编写动态sql，完成逻辑判断和动态拼接sql的功能，
####Mybatis提供了9种动态sql标签trim|where|set|foreach|if|choose|when|otherwise|bind
####利用#{}和${}动态生成对应sql。拼接成想要执行的sql

***
#2、Mybatis是否支持延迟加载？如果支持，它的实现原理是什么？
---
#####Mybatis只支持一对多一对多查询的延迟加载，可以用lazyLoadingEnabled来开关
#####原理就是使用cglib动态代理时，当发现a.getB()是空的时候就先单独查询关联B的sql，B有数据的时候在赋值给a这样就都有值了

***
#3、Mybatis都有哪些Executor执行器？它们之间的区别是什么？
---
####simpleExecutor：每次执行时都会创建一个新的statement，使用完立刻关闭
####ReuseExecutor：每次执行先用statementId查询statement，存在就是用，不存在就创建，使用完不关闭，放在map中，可重复使用的statement
####BatchExecutor：执行update时将所有sql放在批处理中，统一处理，他缓存了多个statement

#4、简述下Mybatis的一级、二级缓存（分别从存储结构、范围、失效场景。三个方面来作答）？
####一级缓存是默认开启的，属于sqlSession级别，和sqlSession生命周期相同，第一次查询有结果时将statementId作为key，结果作为value存在map中
####后面相同的statementId就会先从缓存中获取，没有的话再去db中获取，如果执行update，delete操作会晴空缓存，重复第一次查询逻辑

####二级缓存是基于mapper级别的，可以支持多个sqlSession去使用Mapper命中缓存，二级缓存需要手动开启，在配置中cache-anable参数设定，可以设置type缓存丢弃策略。FIFO LRU等还可以设定刷新时间

***
#5、简述Mybatis的插件运行原理，以及如何编写一个插件？
---
####Mybatis仅可以针对ParameterHandler、ResultSetHandler、StatementHandler、Executor这4种接口的插件，
####Mybatis使用JDK的动态代理，为需要拦截的接口生成代理对象以实现接口方法拦截功能，每当执行这4种接口对象的方法时，就会进入拦截方法，具体就是InvocationHandler的invoke()方法，当然，只会拦截那些你指定需要拦截的方法。
####实现Mybatis的Interceptor接口并复写intercept()方法，然后在给插件编写注解，指定要拦截哪一个接口的哪些方法就好了，还需要在配置文件中配置你编写的插件