# 基于Android的行踪记录器

### 介绍
该系统是一个基于Android的行政记录器，目的是对用户的定位信息进行监控。     
该系统分为 用户端 和 管理端     
用户端是定位数据的生产者，每隔1秒将当前的定位信息发送至服务器；     
管理端是定位数据的消费者，每隔1秒将定位信息从服务器上拉下来；     

### 用户端界面预览

![用户端界面](https://images.gitee.com/uploads/images/2021/0729/132526_fa0cb5fe_7525468.png "用户端界面.png")

### 用户端定位界面预览

![用户端定位界面](https://images.gitee.com/uploads/images/2021/0729/132510_73125788_7525468.png "33333333.png")

### 管理端界面预览

![管理端界面](https://images.gitee.com/uploads/images/2021/0729/132447_dd02fb72_7525468.png "管理端界面.png")


### 软件架构

-TrackRecorder 整个项目文件
--admin 管理端
--app 用户端


### 安装教程

1.  使用 Android Studio，导入本项目
2.  启动第三方模拟器（如雷电模拟器）
3.  运行项目

### 使用说明

启动项目必须开启手机定位权限，否则无法定位     
![权限1](https://images.gitee.com/uploads/images/2021/0729/132406_053db73e_7525468.png "权限1.png")

![权限2](https://images.gitee.com/uploads/images/2021/0729/132421_7cf8f840_7525468.png "权限2.png")

### 欢迎光临我的博客：https://zwz99.blog.csdn.net/        
![我的CSDN博客](https://images.gitee.com/uploads/images/2021/0604/100703_32e14138_7525468.jpeg "132246_599dbf21_7525468.jpeg")

