# 交易所撮合交易引擎设计

简单高效的撮合交易引擎，是交易线需要设计的核心的功能之一。在这里项目里面，我们将解决撮合引擎设计的三个核心问题，分别是：

* 第一、撮合算法：即如何实现买方和卖方的撮合交易逻辑；
* 第二、OrderBook镜像：即如何在服务重启的时候恢复订单数据；
* 第三：资源调度：即如何将不同的symbol分配到不同的服务器；

---

## 开发前置

在开始了解撮合交易系统设计原理之前，我们应该对“交易”有一些基础的概念。详细内容我们将在对应模块进行讲解。

- 市价单：市价购买，不进订单薄；
- 限价单：限价购买，没有完成的就进入订单薄；
- 订单薄：保存没有完成的限价单的集合；
- 盘口：等同于订单薄；

## 撮合算法

详细说明见：### [exchange-matching-algorithm](https://github.com/jcwangjc/exchange-matching-engine/tree/main/exchange-matching-algorithm "exchange-matching-algorithm")

---
