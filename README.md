# 基于Springboot和Vue框架的运动场地预约系统设计与实现

基于Springboot和Vue框架的运动场地预约系统设计与实现

## 需求分析

### 用户中心

业务描述：

用户查看、设置（添加）、修改、删除账号以及用户个人信息，用户个人信息包括：用户ID（唯一且不可修改）、用户昵称（唯一）、绑定手机号、常用地址、历史订单等

---

包含业务以及对应规则：

* 注册：根据手机号+验证码注册
* 登录：
  * 根据手机号+验证码登录
  * 根据用户ID+用户密码+图形验证码登录
* 登出
* 修改用户密码：用户通过输入旧密码验证身份，然后设置新密码
* 设置密码：逻辑同修改用户密码，只不过旧密码为默认密码（123456）

* 绑定手机号：通过接收、校验手机收到的验证码来绑定用户手机号

* 修改手机号：和绑定手机号类似，需要先对旧手机号进行短信验证，然后对新手机号进行短信验证

* 用户ID：由用户注册时自动生成，用户仅能**查看**

* 增加地址：增加用户常用地址

* 删除地址：删除用户常用地址

* 修改地址：修改用户常用地址
* 我的订单：查看用户所有订单信息，包括待开始和未评价，但不含已经取消的订单
* 我的收藏：查看用户所有收藏的场馆信息

### 场地信息

业务描述：

显示所有已经收录的运动场馆的信息，供用户查看，并可以进行条件筛选和排序。场馆信息包括：场馆ID（唯一且不可修改）、场馆名称、场馆所在地址、场馆简介、场地类型（室内|室外|地下）、场馆功能（提供的服务）（乒乓球、足球、篮球、网球、羽毛球、排球、田径、门球、游泳、健身房、桌球、舞蹈、武术、拳击、射箭）、标签（初级、中级、高级、有空调、有夜场、女生多、有培训教练、提供发票、停车方便、木质地板、塑胶地板、人造草皮、天然草皮、WIFI、免费饮水、提供洗浴、卫生间、更衣室）、交通信息

---

包含业务以及对应规则：

* 条件筛选：根据用户选择的筛选条件（标签、功能、类型）对查询结果进行筛选
* 排序：
  * 按照距离就近排序
  * 按照热度高低排序 ~~（热度即浏览量）~~
  * 按照评分高低排序
* 查找：
  * 根据场馆名称查找 （同名连锁店全部显示)
  * 根据地址查找

* 查看：对场馆进行详细介绍，包括：场馆的名称、简介、评分、地址、类型、场馆功能、标签、交通信息（用户评价？）



### 预约服务

业务描述：

为用户提供场地提前预约服务，用户可以对场馆内的空闲场地进行预约、取消预约。一次预约产生一个订单号ID（唯一且不可修改），订单还包括~~运动项目（足|篮|排）~~、状态（待开始、已完成、已取消）、日期、开始时间、结束时间、费用、用户ID、场馆ID

*注：（暂不支持改签、转让、代金卡券、活动促销、会员折扣、月卡服务、满减等附加功能）* 

*一个基本的服务单元为：一个场馆的某个场地的某个时段（一小时为最小单位），根据此单价进行计费。*

**???**是否接受拼单 一起参与某项运动（足篮排需要多人），类似于团购。 同样支持单人购买。

---

包含业务以及对应规则：

* 查找场地：为了跟场地信息中的查找业务进行区分和互补，根据用户提供的运动项目（足|篮|排）、日期（未来一周内？）和时间段（6:00至24:00？）显示所有空闲场地
  * 支持条件筛选
  * 支持结果排序（距离最近、评分最高、热度最高、价格降序、价格升序）

* 预约：预约前查询场馆内空闲场地的空闲时间段，用户在其中进行选择，并以此计费。用户预约时即扣全额费用，之后不再有支付行为。
* 取消预约：查找用户所有已经支付但为履约的预约订单，用户选择特定订单然后取消，根据退费规则进行退费。
* **拼单预约**：通过pdd拼单的形式进行预约。

***

补充：

拼单预约需求分析：

* 用户可以**创建**一个队伍，设置队伍人数，队伍名称（标题）、标书、超过时间 P0
* 根据名称搜索队伍
* 成员退出队伍
* 解散队伍
* 分享队伍，邀请加入队伍
* 消息通知 ？？可以将预约费用等购买信息转发到队伍内？
* 修改队伍信息 ？
* 展示队伍成员？

问题：需要区分已经预定的和未预定的。每次，由前端查询的时间点计算，在这之前的无法预约，在这之后7天内的必须显示（已预约的和未预约的），已预约为从数据库中查询到的预约记录，未查到的不返回给前端，前端默认显示。



整场/半场预约：可以对半分。 类似于组队。 

AA制拼盘预约：如果有，则自动组队；如果没有则创建队伍。



***

实现：先进行拼单页面的开发（类似拼多多），然后点击事件后，创建队伍成功（team表单新增一行元素），用户队伍关系表新增一行；然后预约表单新增一行元素（队伍发起人购买行为）；如果后继者点击组队拼单，队伍先查看该队伍最大人数（由体育场馆和体育项目决定：篮球可以：2/4/6团 羽毛球：2/4团, 游泳不适合包场），如果小于最大人数则吸纳新成员，用户队伍关系表新增一行（team新增一名user），同时booking预约表单中新增一行元素（产生购买行为）。



### 个性推荐

业务描述：

用于首屏显示。若用户未登录，则按照热度、评分的top10场馆进行推荐显示；若用户已经登录，则按照用户的地址就近top10场馆进行推荐显示。

---

包含业务以及对应规则：

* 推荐场馆：	
  * 若用户未登录，则按照热度、评分的top10场馆进行推荐显示
  * 若用户已经登录，则按照用户的地址就近top10场馆进行推荐显示

### 用户评价

业务描述：

用户对服务进行评价，以及对评价进行管理。

---

包含业务以及对应规则：

* 增加评价：
  * 用户必须已经注册 
  * 用户必须完成预约完成并履约（取消预约不算）
  * 用户评价必须超过5字
  * 用户对同一订单只能评价一次
  * 用户评价必须带有评分（1-5）

* 删除评价：
  * 用户仅能删除自己的评价
* 点赞评价：
  * 用户可以点赞所有评价
  * 同一用户对相同评价仅能点赞一次
  * 用户可以取消点赞

## 数据库设计

### 数据需求划分

#### 实体集

根据需求分析，数据集需要包含三个实体集：用户、场地、订单。

* 用户

用户实体包括以下属性：用户ID、用户昵称、绑定的手机号、常用地址。其中用户ID为实体关键字。

* 场地

场地实体包括以下属性：场馆ID、场馆名称、场馆所在地址、场馆简介、场地类型、场馆功能、标签(包含场馆功能标签)、交通信息。其中场馆ID为实体关键字；用设施名称courtName来区分多块相同属性的资源如：1号篮球场...

* 订单

订单实体包括以下属性：订单号ID、运动项目、状态、日期、时间、费用、用户ID、场馆ID。其中订单号ID为实体关键字。

* 队伍

队伍实体里面必须包含订单信息，因为队伍的意义即是提供**拼单服务**。

* 价目表

场地id - 运动项目 - 类型（拼团/包场）- 时间区间 - 每小时价格-体育设施名称-散客最大容量

* 已预定时间表

场地id-运动项目 - 类型（拼团/包场）- 时间区间 - 体育设施名称-关联bookingId

### 联系集

* 预约

用户和场地之间有多对多的预约联系集

* 取消预约

用户和场地之间有多对多的取消预约联系集

**？？？**是否有必要给取消预约单独建个表

---

弱联系集：

* 位于

用户和常用地址之间有一对多的位于联系集

* 拥有

场地和标签之间有一对多的拥有联系集

### 概念设计

#### 描绘E-R图

略

#### 生成关系模型

user(<u>userId</u>, userName, phone) 

place(<u>placeId</u>, placeName, placeAddress, descriptions, trafficInfo) 

booking(<u>bookingId</u>, type, status, date, time, fee, userId, placeId)

cancel(<u>bookingId</u>, type, status, date, time, fee, userId, placeId)

locate(<u>userId</u>, <u>address</u>)

have（<u>placeId</u>, <u>label</u>)



#### 范式判断

略

#### 数据表设计

* user 

| 属性         | 属性类型      | 备注                                         | 主键 | 外键 |
| ------------ | ------------- | -------------------------------------------- | ---- | ---- |
| userId       | bigint        | 用户id                                       | √    |      |
| username     | varchar(256)  | 用户昵称                                     |      |      |
| userPassword | varchar(512)  | 用户密码                                     |      |      |
| avatarUrl    | varchar(1024) | 用户头像                                     |      |      |
| gender       | tinyint       | 性别                                         |      |      |
| userAccount  | varchar(256)  | 用户账号                                     |      |      |
| phone        | varchar(128)  | 用户手机号                                   |      |      |
| email        | varchar(512)  | 邮箱                                         |      |      |
| createTime   | datetime      | 创建时间                                     |      |      |
| updateTime   | datetime      | 更新时间                                     |      |      |
| isDelete     | tinyint       | 是否删除                                     |      |      |
| userStatus   | int           | 用户状态 <br />0 - 正常                      |      |      |
| userRole     | int           | 用户角色 <br />0 - 普通用户<br /> 1 - 管理员 |      |      |
| tags         | varchar(1024) | 标签 json字符串                              |      |      |
| userAddress  | varchar(1024) | 用户地址                                     |      |      |

**数据库建表**

```mysql
-- 用户表
create table user
(
    userId           bigint auto_increment comment 'userId'
        primary key,
    userAccount  varchar(256) null comment '账号',
    username     varchar(256) null comment '用户昵称',
    userPassword varchar(512)       not null comment '密码',
    avatarUrl    varchar(1024) null comment '用户头像',
    gender       tinyint null comment '性别',
    phone        varchar(128) null comment '电话',
    email        varchar(512) null comment '邮箱',
    userStatus   int      default 0 not null comment '状态 0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0 not null comment '是否删除',
    userRole     int      default 0 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    tags         varchar(1024) null comment '标签 json 列表',
    userAddress  varchar(1024) null comment '用户地址'
) comment '用户';

```

- place

| 属性         | 属性类型      | 备注                    | 主键 | 外键 |
| ------------ | ------------- | ----------------------- | ---- | ---- |
| placeName    | varchar(256)  | 场地名称                |      |      |
| avatarUrl    | varchar(1024) | 场地头像                |      |      |
| placeId      | bigint        | 地点id                  | √    |      |
| placeStatus  | int           | 场地状态 <br />0 - 正常 |      |      |
| phone        | varchar(128)  | 电话                    |      |      |
| email        | varchar(512)  | 邮箱                    |      |      |
| createTime   | datetime      | 创建时间                |      |      |
| updateTime   | datetime      | 更新时间                |      |      |
| isDelete     | tinyint       | 是否删除                |      |      |
| tags         | varchar(1024) | 标签 json字符串         |      |      |
| offers       | varchar(1024) | 提供体育项目 json字符串 |      |      |
| placeAddress | varchar(1024) | 地址                    |      |      |
| descriptions | varchar(1024) | 场地描述                |      |      |
| trafficInfo  | varchar(1024) | 交通信息                |      |      |

**数据库建表**

```mysql
-- 场地表
create table place
(
    placeId           bigint auto_increment comment 'placeId'
        primary key,
    placeName     varchar(256) null comment '场地名称',
    avatarUrl    varchar(1024) null comment '场地头像',
    phone        varchar(128) null comment '电话',
    email        varchar(512) null comment '邮箱',
    placeStatus   int      default 0 not null comment '状态 0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0 not null comment '是否删除',
    tags         varchar(1024) null comment '标签 json 列表',
    placeAddress  varchar(1024) null comment '场地地址',
    descriptions   varchar(1024) null comment '场地描述',
    trafficInfo   varchar(1024) null comment '交通信息' ,
    offers		 varchar(1024) null comment '提供体育项目 json字符串'
) comment '场地';

```



* booking

| 属性          | 属性类型     | 备注                                                         | 主键 | 外键 |
| ------------- | ------------ | ------------------------------------------------------------ | ---- | ---- |
| bookingId     | bigint       | 订单id                                                       | √    |      |
| bookingStatus | tinyint      | 状态 <br />0 - 未支付<br /><br />1 - 已支付<br />2 - 已取消（未支付）<br />3 - 已退款<br /> |      |      |
| sport         | varchar(128) | 体育项目名称                                                 |      |      |
| fee           | decimal(6,2) | 金额                                                         |      |      |
| userId        | bigint       | 用户id                                                       |      | √    |
| placeId       | bigint       | 场地id                                                       |      | √    |
| beginTime     | datetime     | 订单开始时间                                                 |      |      |
| endTime       | datetime     | 订单结束时间                                                 |      |      |
| createTime    | datetime     | 创建时间                                                     |      |      |
| updateTime    | datetime     | 更新时间                                                     |      |      |
| isDelete      | tinyint      | 是否删除                                                     |      |      |

**数据库建表**

```mysql
-- 订单表
create table booking
(
    bookingId           bigint auto_increment comment 'bookingId'
        primary key,
    bookingStatus   int   default 0 not null comment '状态0 - 待支付 1 - 支付成功 2 - 取消（未支付）3 - 退款',
    type		 tinyint not null comment '订单类型 0 - 单买 1 - 团购',
    fee 			decimal(6,2) not null comment '金额',
    sport		 varchar(128) not null comment '体育项目名称',
    placeId           bigint not null comment '场地id',
    userId           bigint not null comment '用户id',
    beginTime 	 datetime null comment '开始时间',
    endTime      datetime null comment '结束时间',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0 not null comment '是否删除'
) comment '订单';
```

将sport干脆改为PriceId 这样price表格中可以查到sport的类型，同时还可以通过priceId查询到更多信息如体育设施名称，这样的话可以返回前端一个可视化的界面了。

* price

priceId - 场地id - 运动项目 - 类型（拼团/包场）-体育设施名称- 时间区间 - 每小时价格

| 属性       | 属性类型     | 备注                       | 主键 | 外键 |
| ---------- | ------------ | -------------------------- | ---- | ---- |
| priceId    | bigint       | 价格id                     | √    |      |
| placeId    | bigint       | 场地id                     |      | √    |
| sport      | varchar(128) | 体育项目名称               |      |      |
| type       | tinyint      | 订单类型 0 - 单买 1 - 团购 |      |      |
| courtName  | varchar(128) | 体育设施名称               |      |      |
| beginTime  | datetime     | 开始时间点                 |      |      |
| endTime    | datetime     | 结束时间点                 |      |      |
| unitPrice  | decimal(6,2) | 单价                       |      |      |
| createTime | datetime     | 创建时间                   |      |      |
| updateTime | datetime     | 更新时间                   |      |      |
| isDelete   | tinyint      | 是否删除                   |      |      |

总容量：对于散客来说，需要制定一个总容量来限定人数。

```mysql
-- 价目表
create table price
(
    priceId           bigint auto_increment comment 'priceId'
        primary key,
    placeId      bigint not null comment '场地id',
    sport		 varchar(128) not null comment '体育项目名称',
    type		 tinyint not null comment '订单类型 0 - 单买 1 - 团购',    
    beginTime 	 datetime null comment '开始时间',
    endTime      datetime null comment '结束时间',
    unitPrice	 decimal(6,2) not null comment '单价',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0 not null comment '是否删除',
    courtName    varchar(128) not null comment '体育设施名称，如篮球场1'
) comment '价目表';
```

* bookingTime

| 属性          | 属性类型     | 备注                       | 主键 | 外键 |
| ------------- | ------------ | -------------------------- | ---- | ---- |
| bookingTimeId | bigint       | 预约时间id                 | √    |      |
| placeId       | bigint       | 场地id                     |      | √    |
| bookingId     | bigint       | 订单id                     |      | √    |
| sport         | varchar(128) | 体育项目名称               |      |      |
| type          | tinyint      | 订单类型 0 - 单买 1 - 团购 |      |      |
| courtName     | varchar(128) | 体育设施名称               |      |      |
| beginTime     | datetime     | 开始时间点                 |      |      |
| endTime       | datetime     | 结束时间点                 |      |      |
| createTime    | datetime     | 创建时间                   |      |      |
| updateTime    | datetime     | 更新时间                   |      |      |
| isDelete      | tinyint      | 是否删除                   |      |      |

```mysql
-- 已预订时间表
create table bookingTime
(
    bookingTimeId           bigint auto_increment comment 'bookingTimeId'
        primary key,
    placeId      bigint not null comment '场地id',
    bookingId    bigint not null comment '订单id',
    sport		 varchar(128) not null comment '体育项目名称',
    type		 tinyint not null comment '订单类型 0 - 单买 1 - 团购',    
    beginTime 	 datetime null comment '开始时间',
    endTime      datetime null comment '结束时间',
	courtName    varchar(128) not null comment '体育设施名称，如篮球场1',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0 not null comment '是否删除'
) comment '已预订时间表';
```



* cancel

| 属性      | 属性类型     | 备注                     | 主键 | 外键 |
| --------- | ------------ | ------------------------ | ---- | ---- |
| bookingId | bigint       | (0，4 294 967 295)       | √    |      |
| type      | varchar      |                          |      |      |
| status    | tinyint      | (0,2)                    |      |      |
| date      | DATE         | 1000-01-01/9999-12-31    |      |      |
| time      | TIME         | '-838:59:59'/'838:59:59' |      |      |
| fee       | decimal(6,2) |                          |      |      |
| userId    | bigint       | (0，4 294 967 295)       |      | √    |
| placeId   | bigint       | (0，4 294 967 295)       |      | √    |

* locate

| 属性    | 属性类型 | 备注               | 主键 | 外键 |
| ------- | -------- | ------------------ | ---- | ---- |
| userId  | bigint   | (0，4 294 967 295) | √    | √    |
| address | varchar  |                    | √    |      |

* have

| 属性    | 属性类型 | 备注               | 主键 | 外键 |
| ------- | -------- | ------------------ | ---- | ---- |
| placeId | bigint   | (0，4 294 967 295) | √    | √    |
| label   | varchar  |                    | √    |      |

如何设计资源？

利用booking表来生成：查询条件限制date和time 在某个范围内，如果查到了就标记为被booked，反之为空闲，等待被标记。



#### 数据库模式图

略



### 后端逻辑实现

**用户相关 userController**

* 登录逻辑 √

修改登录失败的返回值，报错信息。√

* 注册逻辑 √
* 登出逻辑 √
* 删除用户逻辑 √
* 获取当前登录用户信息 √
* 更新用户信息√

**场地相关 placeController**

* 新增场地√

当前用户必须为管理员 ->请求参数不能为空 ->不能跟已有的场地信息重合 placeName ->插入

* 删除场地√

当前用户必须为管理员  -> 请求参数不能为空 ->删除

* 通过id查询场地信息√

参数不为空-> 查询

* 条件查询场地信息√

所需数据结构：

PlaceQuery封装类：只能查找特定信息

~~PlaceVo封装类： 连带其他信息~~ （没必要）

判空-> 根据PlaceQuery内容**条件查询**(如果不是管理员，不能查找到已经下架的场馆) -> 查询 ->返回脱敏数据

* 条件查询场地并返回分页信息 √

逻辑同上。返回类型不一样。

* 修改场地信息√

参数不为空 ->登录用户为管理员 ->判断场地id是否存在 ->修改

* 推荐场地 ×

目前的逻辑为默认顺序分页展示

**预定相关bookingController**

增加用户订单中sport（可以看出爱好哪些运动） √

* 创建订单 √

老接口为：add 没有余量校验功能和addBookingTime逻辑， 而且请求体已经更新，老的接口不适用于新的请求体， 建议删除

新接口为：add/list

**（如何保证订单价格不被修改？）**后端通过查询价目表重新计算一次，如果不符创建失败。

判空 -> 设置userId为登录用户 -> 查询placeId是否存在 ->计算是否有余量 ->计算金额是否正确->设置bookingStatus为未支付 -> 创建

成功创建后，执行addBookingTime逻辑：

创建一个新对象：判空->校验参数（bookingId不用过多校验，因为是后端传入的）是否存在->创建 



* 修改订单（改签） ×

判空 ->查询placeId是否存在 -> 设置userId为登录用户 -> 状态必须为已支付-> 修改bookingStatus -> 修改金额 （如何多退少补？）-> 修改 

* 取消订单（支付成功退票 未支付取消）√

判空 ->查询bookingId是否存在 -> 判断userId是否为当前登录用户 ->状态必须为未支付-> 修改bookingStatus -> 修改

成功修改后，执行deleteBookingTime逻辑：

判空 -> 根据bookingId删除bookingTime

* 支付订单 √

判空 ->查询bookingId是否存在 -> 判断userId是否为当前登录用户 -> 状态必须为未支付->修改bookingStatus -> 修改

* 退票 √

判空 ->查询bookingId是否存在 -> 判断userId是否为当前登录用户 -> 状态必须为已支付->修改bookingStatus -> 修改

成功修改后，执行deleteBookingTime逻辑：

判空 -> 根据bookingId删除bookingTime

* 列出所有订单 √

所需数据结构：

BookingQuery封装类：只能查找特定信息

BookingPlaceUserVo封装类： 将订单和用户、场地信息结合。

判空-> 根据bookingQuery内容**条件查询**(如果不是管理员，仅能查看自己的订单) -> 查询 ->返回脱敏数据

* 用户查看订单 √

同上。

* 用户点评订单 ×
* 列出所有已被预定的时间段

判空 -> 已登录(可选) -> 根据条件查询bookingTime表 ->返回 

**价目相关**

*不做前端*

MySQL中实现场地价目表： 场地-体育项目-单价 √

* 插入√

判空 -> 鉴权 -> 队伍是否存在  -> type是否存在 -> sport是否存在 -> 单价是否为空 -> 插入 

* 删除√

根据id删除

* 修改√

根据id修改订单

priceId必须参数

* 查询√

管理员可以查询全部price 

普通用户查询当前place：需要提供placeId、type、sport、courtName

### 前端界面展示

* 开发bookingIndex主页：√

需要用到recommend功能 开发相关接口 √

* 修改找场search/searhResult页：√

展示标签进一步细化 √

修改前端的展示标签 ×

改get请求地址为/place/search/tags √

* 修复登录模块：

debug √

如何加上路由跳转？ 现在是重定向到主页

~~未登录点击自动登录or注册？？~~ ×

未登录显示“登录”按钮 ；登录显示用户账号信息√

* 开发订单模块bookingList:

显示我的订单 √

修改：bookingList里面的数据类型为bookingVO不是booking，需要在前端重新定义一个数据类型 √

完善订单按钮的功能；√

* 开发placePage页面

展示场地详情信息√

展示场地图片（最好多张）×

* 开发placeChoose页面

界面更加美观，有可视化，有图例√

完成预定按钮逻辑 √

显示体育运动类型选择 √

前端在选择完时间段之后请求价目表查看单价，通过乘以持续时间计算总价格。 √

修改为可以监控前端所有组件的改变 然后改变总价格。使用 watchEffect钩子 √

目前采取的方案是前端需要计算，还是说计算全部交给后端？？ 前端需要基本的计算：总价=单价*时间 √

问题是price/get只能返回一个单价，如果碰到了跨越多个价格区间的时间段，会报错。（后端做了限制 √

加锁，限制狂点产生的资源异常 √
