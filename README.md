# PMS

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
java -jar target/pms-1.0.0-SNAPSHOT.jar
```

默认端口 `8080`。

## 测试

```bash
mvn test
```

## 当前功能：卡片增删改查

单一模块 `com.pms.card`，包结构：

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

## 人员离职通知

新增 `com.pms.personnel` 模块，接收 OA 系统离职通知并把人员状态置为「离职」。

### 接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/personnel/departure` | 接收 OA 离职通知，人员状态更新为 DEPARTED |

鉴权（假设：OA 共享静态应用凭证），由 `OaAuthInterceptor` 在参数绑定前完成：

- `X-App-Key`：应用标识
- `X-App-Secret`：应用密钥

凭证通过环境变量 `OA_APP_KEY` / `OA_APP_SECRET` 注入（必填，为空启动 fail-fast），比较采用 `MessageDigest.isEqual` 恒时比较。

请求体：

```json
{
  "eventId": "EVT-20261021-000001",
  "employeeId": "E10086",
  "tenantId": "T001",
  "departureTime": "2026-10-21 18:00:00",
  "reason": "个人原因",
  "operator": "admin"
}
```

响应（统一结构 `code` / `message` / `data`）：

```json
{ "code": "0", "message": "ok", "data": { "employeeId": "E10086", "status": "DEPARTED" } }
```

状态码与业务码：

| HTTP | code | 含义 | 是否可重试 |
|---|---|---|---|
| 200 | 0 | 成功（含幂等重复事件） | — |
| 400 | 40001 | 参数校验失败 | 否 |
| 400 | 40002 | 事件与人员不匹配 | 否 |
| 401 | 40101 | 应用凭证校验失败 | 否 |
| 404 | 40401 | 人员不存在 | 否 |
| 500 | 50001 | 系统内部错误 | 是 |

### 数据模型

`personnel` 表（`(tenant_id, employee_id)` 唯一索引）：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint | 主键，自增 |
| tenant_id | varchar(64) | 租户标识 |
| employee_id | varchar(64) | 人员唯一标识（OA 工号） |
| name | varchar(128) | 姓名 |
| status | varchar(32) | 状态：ACTIVE / DEPARTED |
| departure_time | datetime | 离职生效时间 |
| created_at / updated_at | datetime | 创建/更新时间 |

`departure_event` 表（`event_id` 唯一索引，幂等键）：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint | 主键，自增 |
| event_id | varchar(128) | OA 离职事件唯一 ID |
| tenant_id / employee_id | varchar(64) | 人员定位 |
| reason / operator | varchar | 离职原因 / 操作人 |
| created_at | datetime | 处理时间 |

生产 MySQL 建表与唯一索引脚本见 `src/main/resources/db/migration/V1__personnel_departure.sql`（H2 开发/测试环境由 JPA `ddl-auto=update` 自动建表）。

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
