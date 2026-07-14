# 共享自习室预约系统

> 共享自习室分时预约管理系统，支持用户通过移动端查看自习室区域/座位/套餐信息，在线预约座位，支付后扫码签到使用。

## 项目结构

```
zzzxs01/
├── study-room-reservation/    # 后端服务（Spring Boot 2.7.3）
│   ├── studyroom-common/      #   公共模块（工具类、常量、异常、JWT配置）
│   ├── studyroom-pojo/        #   数据对象模块（Entity、DTO、VO）
│   ├── studyroom-server/      #   业务服务模块（Controller、Service、Mapper）
│   ├── sql/                    #   数据库脚本
│   └── pom.xml
├── study-room-mobile/         # 移动端（UniApp Vue3）
│   ├── pages/                 #   页面（15个）
│   ├── api/                   #   API服务层（7个模块）
│   ├── utils/                 #   工具（请求封装、认证、格式化等）
│   ├── components/            #   公共组件
│   └── pages.json
├── software/                  # 管理端部署（Nginx + Vue2编译产物）
│   ├── nginx-1.20.2/
│   │   ├── conf/             #   Nginx配置
│   │   ├── html/studyroom/   #   管理端前端（编译产物）
│   │   └── nginx.exe
│   └── ...
└── README.md
```

## 技术栈

| 端 | 技术 | 端口 |
|------|------|------|
| 后端 | Spring Boot 2.7.3 + MyBatis + MySQL + Redis + JWT | 8080 |
| 移动端 | UniApp Vue3 | 5173（H5开发） |
| 管理端 | Vue2 + Element UI + ECharts（编译产物） | 80（Nginx） |
| 数据库 | MySQL 8.x，库名 study_room_reservation | 3306 |
| 缓存 | Redis | 6379 |

## 快速启动

### 1. 数据库准备
```bash
# 导入建表脚本
mysql -u root -p123456 < study-room-reservation/sql/study_room_reservation.sql
# 执行修复脚本
mysql -u root -p123456 < study-room-reservation/sql/fix_avatar_column.sql
mysql -u root -p123456 < study-room-reservation/sql/fix_image_paths.sql
# 添加用户密码字段（移动端登录需要）
mysql -u root -p123456 < study-room-reservation/sql/add_user_password.sql
```

### 2. 启动后端
```bash
cd study-room-reservation
mvn clean install -DskipTests
cd studyroom-server
mvn spring-boot:run
# 服务启动在 http://localhost:8080
```

### 3. 启动移动端
- 用 HBuilderX 打开 `study-room-mobile` 目录
- 点击"运行" → "运行到浏览器" → 选择 Chrome
- 测试账号：`13800138000` / `123456`

### 4. 启动管理端（可选）
```bash
cd software/nginx-1.20.2
start nginx.exe
# 管理端访问 http://localhost
# 管理员账号：admin / 123456
```

## 移动端功能模块

| 模块 | 页面 | 说明 |
|------|------|------|
| 登录注册 | login, register | 手机号+密码登录/注册 |
| 首页 | home | 场馆状态、区域概览、套餐推荐 |
| 选座 | area, seat-list, seat-detail | 区域→座位→时段选择 |
| 预约 | confirm, pay, success | 计费→支付→签到码 |
| 签到 | checkin | 扫码签到/手动签到 |
| 我的预约 | order-list, order-detail | 6状态筛选+分页 |
| 套餐 | package-list | 套餐中心 |
| 客服 | chat | 智能客服（需后端开启） |
| 个人中心 | profile | 用户信息、功能入口 |

## 后端用户端接口

| 接口 | 方法 | 路径 | 认证 |
|------|------|------|------|
| 手机号登录 | POST | /user/user/loginByPassword | 否 |
| 用户注册 | POST | /user/user/register | 否 |
| 用户信息 | GET | /user/user/profile | 是 |
| 更新信息 | PUT | /user/user/update | 是 |
| 场馆状态 | GET | /user/shop/status | 否 |
| 区域列表 | GET | /user/area/list | 是 |
| 座位列表 | GET | /user/seat/list | 是 |
| 座位详情 | GET | /user/seat/{id} | 是 |
| 套餐列表 | GET | /user/package/list | 是 |
| 提交预约 | POST | /user/order/submit | 是 |
| 预约支付 | POST | /user/order/pay | 是 |
| 扫码签到 | POST | /user/order/checkin | 是 |
| 取消预约 | PUT | /user/order/cancel | 是 |
| 我的预约 | GET | /user/order/list | 是 |
| 预约详情 | GET | /user/order/detail/{id} | 是 |
| 文件上传 | POST | /user/common/upload | 是 |
| 智能客服 | POST | /user/chat/send | 是 |

> 认证头字段：`authentication`，值为 JWT Token

## 订单状态

| 状态值 | 名称 | 说明 |
|--------|------|------|
| 1 | 待支付 | 已提交预约，等待支付 |
| 2 | 已预约 | 已支付，等待签到 |
| 3 | 使用中 | 已签到，正在使用 |
| 4 | 已完成 | 预约结束，自动完成 |
| 5 | 已取消 | 用户或系统取消 |
| 6 | 已过期 | 未签到，自动过期 |
