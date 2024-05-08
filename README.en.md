[中文](README.md) / English


---

## About

neatlogic-framework is the underlying framework of the entire project, and all modules need to reference
neatlogic-framework.

## Modularity

neatlogic is based on Spring MVC, version 5.x. By dynamically loading Servlet, the hierarchical feature of SpringContext
is used to realize modular management. \
As shown in the figure below, beans between submodules do not affect each other and cannot be woven into each other.
Beans of common modules can be woven into beans of submodules. \
![img.png](README_IMAGES/img.png)

* The root context is loaded through ContextLoaderListener to manage public beans.
* The module context is loaded through DispatcherServlet to manage the beans inside the module.
* Dynamically create DispatcherServlet through ModuleInitializer.
* The call between modules is through @RootComponent, as glue (factory mode, template class mode).

When doing secondary development, developers need to put classes into different packages to determine whether the beans
created by this class will become part of the public module. \
Each submodule of neatlogic will be divided into two projects: neatlogic-xxx and neatlogic-xxx-base (if the beans of
this module will not be referenced by other submodules, neatlogic-xxx-base may not be created.)
The package path in neatlogic-xxx starts from neatlogic.module.xxx, and the class here can only be used in the xxx
module after it becomes a bean. \
The package path in neatlogic-xxx-base starts from neatlogic.framework.xxx. After the class here becomes a bean, it can
be referenced by all submodule beans except neatlogic-framework.
And pojo classes, such as dto classes, enumeration classes, etc., we recommend managing them in the neatlogic-xxx-base
project.

This rule is jointly determined by root-content.xml and xxx-servlet-context.xml.

## Multi-tenancy

neatlogic adopts a multi-tenant mode in which the middleware is shared and the database is exclusive.

### Core classes

- NeatLogicRoutingDataSource: Distribute the real datasource through the tenant information in threadlocal.
- NeatLogicBasicDataSource: Inherit HikariDataSource, the datasource actually used by the system, you can do some
  pre-operations before returning to the connection, such as changing the session configuration.

## Processing flow

neatlogic adopts a front-end and back-end separation architecture, and all back-end services are exposed in the form of
restful interfaces for front-end calls. The entry class is ApiDispatcher, which supports three data formats, namely
json, json stream and file. Different data formats need to inherit different base classes. \
![api](README_IMAGES/api.png)
In order to improve reusability and facilitate management, each interface of neatlogic is an independent bean, and the
interface class can inherit the three basic classes of ApiComponentBase, JsonStreamApiComponentBase, and
BinaryStreamApiComponentBase according to actual needs. \
In each interface class, the access rights, operation type (for auditing), input parameters, output parameters,
description, data example and other information of the interface can be defined through annotations, and all interface
documents can be exported with one click according to these configurations.

## Multi-live mechanism

The deployment method of neatlogic is a multi-active architecture, which comes with a simple heartbeat mechanism so that
each service instance can understand the survival status of each other, so as to complete some special drift work, such
as allowing the scheduled job service to drift automatically.

### Heartbeat Mechanism

- Each service instance has a unique service instance ID, which is automatically generated when the service is first
  started, and saved in serverid.conf after generation, followed by a unique identifier for the current service.
- Considering that there may be a firewall inside the enterprise, the heartbeat status is transmitted by the database,
  and the service instances will not send heartbeats to each other.
- Use the heartbeat counter to judge the survival status of the service instance.

### Heartbeat Algorithm

1. When the service instance starts, write the status into the status table (neatlogic library server_status table), and
   set its own status as startup. And start a heartbeat thread to perform heartbeat detection regularly.
2. Every time the heartbeat wakes up, it first clears its own counter, and adds 1 to the counter of its own concerned
   service instance. If the counter of the concerned service instance is greater than a certain threshold, its state is
   set to shutdown.
3. Call all the implementation classes under the drift interface to complete the drift action. All logic that needs to
   be processed when the service instance dies needs to implement the IHeartbreakHandler interface.

Pay attention to the service instance: the service instance id is greater than itself, and the service instance whose
status is startup, if there is no larger service instance id, then find the smallest service instance id, and finally
form a monitoring ring. \
The heartbeat frequency is controlled by setting the heartbeat.rate parameter, and the unit is minutes, and the failure
threshold is controlled by heartbeat.threshold.

### Multithreading

Since neatlogic switches the data source through the tenant information in threadlocal, it cannot directly define thread
to implement asynchronous jobs. All threads need to be created by inheriting NeatLogicThread.

NeatLogicThread will automatically obtain the threadlocal information of the current thread during the instantiation
phase, ensuring that asynchronous threads and threads can use the same data source for work.
NeatLogicThead will also wait for all modules to be loaded before starting to execute, avoiding preemptive execution
during startup and causing exceptions.

Example of use:

```java
CachedThreadPool.execute(new NeatLogicThread(){
@Override
protected void execute(){
        //do something
        }
        });
```

CachedThreadPool is a thread pool in the framework, which can be used directly, **don't create thread pool by yourself
**.
**Considering that CachedThreadPool will be referenced in multiple places and to avoid obvious resource competition
between different tenants,
CachedThreadPool is an infinite thread pool. Before using it, you must pay attention to controlling the number of
threads and considering the execution time of each thread job, so as not to cause system OOM. **

## Timing scheduling

Unified use of SchedulerManager for timing scheduling management, including internal and external jobs.

Example of use:

```java
JobObject.Builder newJobObjectBuilder=new JobObject.Builder(changeId.toString(),this.getGroupName(),this.getClassName(),TenantContext.get().getTenantUuid()).withBeginTime(changeAutoStartVo.getTargetTime()).withIntervalInSeconds(60*60).withRepeatCount(0).addData("changeId",changeId);
        JobObject newJobObject=newJobObjectBuilder.build();
        schedulerManager.loadJob(newJobObject);
```

When the SchedulerManager starts, it will load and run the scheduled jobs of all tenants. Cooperating with
HeartbeatManager can realize the automatic drift of scheduled jobs.

- Internal job: The job is initiated by the system itself, and the user is not allowed to configure it freely. It needs
  to implement the IJob interface.
- External job: It is configured and initiated by the user in job management, supports multiple job instances, and needs
  to implement the IPublicJob interface.

## Full Text Search

Since I don't want to maintain a separate ES environment, and haven't found an ideal solution to solve the problem of ES
data and DB data linkage, neatlogic's full-text search will store word segmentation results in the database, and realize
the full-text search function through SQL query.

The system will automatically create related data tables according to the document type defined in
FullTextIndexInitializer. You only need to implement the relevant methods in FullTextIndexHandlerBase, the system will
automatically write the index data into the corresponding data table, and you don’t need to pay attention to the
specific name of the data table when querying, you only need to write the prefix, and the system will replace it with
the correct one before SQL execution through FulltextIndexInterceptor data table to execute.

![fullindex](README_IMAGES/fullindextables.png)