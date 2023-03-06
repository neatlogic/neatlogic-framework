## 系统架构
后端语言使用java开发，后端框架：Spring5.x和Mybatis3.x，前端框架：Vue2.x+iView，后端中间件：Tomcat9.X+，前端中间件：Nginx，数据库：Mysql8+、Mongodb4+。\
**NeatLogic使用动态加载Servlet和SpringContext分层特性解决模块化问题**。\
<img src="https://github.com/neatlogic/.github/blob/main/images/framework.png?raw=true" width="500px">
* root context通过ContextLoaderListener加载，管理公共的bean。
* module context通过DispatcherServlet加载，管理模块内部的bean。
* 通过ModuleInitializer动态创建DispatcherServlet。
* 模块之间调用通过@RootComponent，作为粘合剂（工厂模式、模板类模式）。\
整个设计可以做到各个子模块之间的bean不会相互冲突，一些公共服务可以放到框架层，让所有子模块都可以引用。