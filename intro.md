# 系统说明

## 配置说明
1. 申请百度地图Key
[申请说明地址](http://lbsyun.baidu.com/index.php?title=androidsdk/guide/key)

将得到的 api key 替换[AndroidManifest](https://github.com/donlan/MapFun/blob/master/map/src/main/AndroidManifest.xml)中的：
```
<meta-data
            android:name="com.baidu.lbsapi.API_KEY"
            android:value="key 替换" />
```
2. 申请LeanCode
[移动开发工具TAB](https://www.qcloud.com/product/TAB)
创建应用替换[ModelConfig](https://github.com/donlan/MapFun/blob/master/model/src/main/java/dong/lan/avoscloud/ModelConfig.java)中的
```
private static final String API_ID = "";
private static final String API_KEY = "";

```
[后台管理](https://console.qcloud.com/tab) 中创建对应的Class


## 一、使用的技术栈
* LeandCode（数据存储，实时通信）
* EventBus（组件间通信）
* BaiDu Map SDK（地图模块）
* Glide(图片加载显示)
* Picture-Library(图片选择裁剪压缩)

## 二、自定义View相关
* LabelTextView（标签式TextView）
* SlidingMenu(侧滑菜单)

## 三、项目结构

### Module
* app 主模块
* base 项目通用的内容，包括基类，自定义view等
* map 百度地图的封装模块
* RTPermission Android运行时权限处理模块
* model 数据模型模块

### app Module
- activity (APP页面)
- adapter （列表使用的适配器）
- event （EventBus事件）
- feature (登录注册契约)
- helper （一些工具）
- im （即时通信相关）
- mvp （MVP结构）
    - contract（契约）
    - model（模型）
    - presenter（处理）
    
App    

### 
![](mapFun.jpg)