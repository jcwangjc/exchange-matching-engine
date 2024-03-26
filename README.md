# 交易所撮合交易引擎设计

简单高效的撮合交易引擎，是交易线需要设计的核心的功能之一。在这里项目里面，我们将解决撮合引擎设计的三个核心问题，分别是：

* 第一、撮合算法：即如何实现买方和卖方的撮合交易逻辑；
* 第二、OrderBook镜像：即如何在服务重启的时候恢复订单数据；
* 第三：资源调度：即如何将不同的symbol分配到不同的服务器；

---

## 撮合算法

详细说明：
### [exchange-matching-algorithm](https://github.com/jcwangjc/exchange-matching-engine/tree/main/exchange-matching-algorithm "exchange-matching-algorithm")

启动项：
### [exchange-matching-execute](https://github.com/jcwangjc/exchange-matching-engine/tree/main/exchange-matching-execute "exchange-matching-execute")


---
