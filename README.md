# Docker Builder

This project is a prototype of a way to make docker more friendly to applications with combinatoric issues.

## Hadoop Example

Hadoop currently supports oracle/openjdk 6/7, has multiple vendors (apache, bigtop, cloudera, hortonworks, mapr, pivotal, etc.), and multiple deployment options (datanode + nodemanager, datanode, nodemanager, w/ regionserver, etc.).  Because of this, creating a docker image is very painful since you either have to do a ton of work to support these cases, or you flat out ignore them and hard code one deployment option (java 6 with apache with everything installed).

# Usage

To use docker-builder, you first must define a few "traits" (explained later) that define isolated behaviors with requirements about how construction works.

Here is a example of java support.

```
SELF: rhel
NAMED: java:openjdk-1.7

RUN: yum install -y java-1.7.0-openjdk-devel
```

This example should look similar to the normal Dockerfile, but with a few differences:

  * instructions are postfixed with ':'.  This is to make parsing simple
  * new instructions defined: self, named

## Self

Self is how a trait defines requirements for how an image is constructed.  In the above example (`SELF: rhel`) the abstract name 'rhel' is used as a way to say that this trait works against all 'rhel' systems (centos, rhel, etc.).

If a trait has multiple requirements, then it defines these using 'with' keyword.

```
SELF: rhel with java
NAMED: datanode:datanode-2.4
```

In the above example, the trait is saying that the image must contain both 'rhel' and 'java' before this trait can be mixed in.

## Named

Self defines the requirements for a trait, and named is the way to express what the image defines.

In the above example of java, the `NAMED: java:openjdk-1.7` expresses that the trait is a instance of the abstract type `java`, but has a more concrete type of `openjdk-1.7`.  When another trait requires `java`, this requirement can be resolved by the above defined trait.

### Example

Below is a trivial example of hadoop that could support multiple different rhel distros, and java versions.

centos.docker:

```
# base image for centos
FROM: centos:centos6
NAMED: rhel:centos6
```

java.docker:

```
# mixin to add java 7 support
SELF: rhel
NAMED: java:openjdk-1.7

RUN: yum install -y java-1.7.0-openjdk-devel
```

hadoop.docker:

```
# mixin to add datanode
SELF: rhel with java
NAMED: datanode:datanode-2.4

RUN: mkdir -p /opt/hadoop
RUN: cd /opt/hadoop && wget 'http://mirrors.koehn.com/apache/hadoop/common/hadoop-2.4.1/hadoop-2.4.1.tar.gz' && tar zxvf hadoop-2.4.1.tar.gz && rm hadoop-2.4.1.tar.gz
```

Build the image:

```
docker-builder centos.docker java.docker hadoop.docker
```

Generates Dockerfile

```
FROM centos:centos6
RUN yum install -y java-1.7.0-openjdk-devel
RUN mkdir -p /opt/hadoop
RUN cd /opt/hadoop && wget 'http://mirrors.koehn.com/apache/hadoop/common/hadoop-2.4.1/hadoop-2.4.1.tar.gz' && tar zxvf hadoop-2.4.1.tar.gz && rm hadoop-2.4.1.tar.gz
```

# Model

The idea for this prototype came from Scala's trait system, which lets users "mixin" behaviors at creation time (new) rather than doing it at declaration time (defining class).

To help with the combinatoric problem, you can "mixin" docker traits that add new behaviors.

### Scala

The idea for this prototype came from Scala's trait system.  You can write code like

```
new MyClass with Logging with Monitoring with Healthchecks with ...
```

to create a new object with all the different behaviors "mixed" in.

For more details on Scala's trait system: [checkout scala tour](http://dcapwell.github.io/scala-tour/)
