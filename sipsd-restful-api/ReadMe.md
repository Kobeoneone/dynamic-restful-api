该项目支持4中数据库，分别是mysql，sqlserver，oracle，postgresql的自动化服务(有bug的地方请多海涵 欢迎提交ticket)
集成了druid数据源，自动化发布的查询服务支持分页，删除/新增/修改 无分页。大家都懂

为了减少项目体量没有集成redis，个人认为集成redis比较好


1. 使用自动化接口需注册数据库信息，当前表名为cm_dbinfo，请参照resources下面的sql
2. cm_dbinfo当前默认是在mysql数据库-cic  你可以更换你想要的数据库
3. 自动化接口通过查询cm_dbinfo中的jdbc连接字符串，用户名，密码来用jdbc动态连接数据库
4. 如果要支持手写sql发布动态服务需要增加sm_sys_restfulapi表，你可以裁剪相应字段
5. 以下是crud的接口规范
   1. 查询(GET)：http://127.0.0.1:8085/dbapi/query/此处为cm_dbinfo的Name字段/表名
           http://127.0.0.1:8085/dbapi/query/此处为cm_dbinfo的Name字段/表名/id
   
   2. 更新(POST): http://127.0.0.1:8085/dbapi/edit/此处为cm_dbinfo的Name字段/表名(body为json字符串)
      示例：http://127.0.0.1:8085/dbapi/edit/此处为cm_dbinfo的Name字段/cm_dbinfo
           {
               "addcode": "1",
               "addtime": "2018-07-31 16:00:07.0",
               "alias": "",
               "area_id": 0,
               "catalogue_id": 0,
               "code": "0C732D02-5C9B-4AB5-AE25-896544E5F0CD",
               "connstr": "",
               "connstr2": "",
               "db_name": "",
               "db_schema": "",
               "dbtype": "",
               "flag": 1,
               "id": 8,
               "name": "GeoDoc",
               "pwd": "",
               "region": "",
               "remark": "",
               "setcode": "",
               "settime": "",
               "source": "",
               "source_type": 0,
               "status": 1,
               "tags": "",
               "user_id": "",
               "version": ""
           } 
      
   3. 新增(POST)：http://127.0.0.1:8085/dbapi/save/此处为cm_dbinfo的Name字段/表名
      示例：http://127.0.0.1:8085/dbapi/save/此处为cm_dbinfo的Name字段/cm_dbinfo
           {
               "addcode": "1",
               "addtime": "2018-07-31 16:00:07.0",
               "alias": "",
               "area_id": 0,
               "catalogue_id": 0,
               "code": "0C732D02-5C9B-4AB5-AE25-896544E5F0CD",
               "connstr": "",
               "connstr2": "",
               "db_name": "",
               "db_schema": "",
               "dbtype": "",
               "flag": 1,
               "id": 8,
               "name": "GeoDoc",
               "pwd": "",
               "region": "",
               "remark": "",
               "setcode": "",
               "settime": "",
               "source": "",
               "source_type": 0,
               "status": 1,
               "tags": "",
               "user_id": "",
               "version": ""
           } 
           
   4. 删除(GET):http://127.0.0.1:8085/dbapi/delete/此处为cm_dbinfo的Name字段/表名/表id
      注：只支持带有id主键的表
      
   5.自定义sql:(GET):http://127.0.0.1:8085/dbapi/exec/此处为cm_dbinfo的Name字段/表名/此处为sm_sys_restfulapi的map_service_code字段