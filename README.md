# xone-tenant

一站式账户管理服务（骨架工程）。

## 技术栈

- Java 8
- Spring Boot 2.7.18
- Spring Data JPA
- H2（内存库，控制台 `/h2-console`）
- Maven

单体打包为单个 JAR，运行在 Spring Boot 内嵌 Tomcat 容器中。

## 启动

```bash
mvn spring-boot:run
```

或：

```bash
mvn clean package
java -jar target/xone-tenant-1.0.0-SNAPSHOT.jar
```

默认端口 `8080`。

## 测试

```bash
mvn test
```

## 当前功能：卡片增删改查

单一模块 `com.xone.tenant.card`，包结构：

```
card/
  entity/       SpaceCard（实体）、CardRequest（请求 DTO）
  repository/   SpaceCardRepository（JPA 仓储）
  service/      SpaceCardService（业务逻辑）
  controller/   SpaceCardController（REST 接口）
```

### 接口

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/cards/{id}` | 查询单张卡片，不存在返回 404 |
| GET | `/api/cards?spaceId=1&status=DRAFT` | 查询空间下的卡片列表，status 可选 |
| POST | `/api/cards` | 新建卡片，返回 201 |
| PUT | `/api/cards/{id}` | 更新卡片，不存在返回 404 |
| DELETE | `/api/cards/{id}` | 删除卡片，不存在返回 404 |

### 数据模型

`space_card` 表：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint | 主键，自增 |
| space_id | bigint | 所属空间 ID |
| title | varchar(200) | 卡片标题，必填 |
| type | varchar(64) | 卡片类型 |
| status | varchar(32) | 卡片状态：DRAFT / PUBLISHED / ARCHIVED |
| content | varchar(4000) | 卡片内容 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

## 调用示例

```bash
# 新建
curl -X POST http://localhost:8080/api/cards \
  -H "Content-Type: application/json" \
  -d '{"spaceId":1,"title":"开户申请卡","type":"FORM","content":"请填写开户信息"}'

# 列表
curl "http://localhost:8080/api/cards?spaceId=1"

# 更新
curl -X PUT http://localhost:8080/api/cards/1 \
  -H "Content-Type: application/json" \
  -d '{"spaceId":1,"title":"开户申请卡（改）","type":"FORM","content":"...","status":"PUBLISHED"}'

# 删除
curl -X DELETE http://localhost:8080/api/cards/1
```
