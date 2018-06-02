# WifiClock<br/>
初衷是实现钉钉Wifi打卡的功能  其次是配合自己的PHP后台展示打卡记录。<br/>
<br/>
<br/>
最终可能出现两版<br/>
<br/>
1.一个独立的PHP后台  <br/>
2.抓取公司打卡记录的App<br/>
<br/>
因为公司目前并不支持WIFI打卡<br/>
我也拿不到打卡记录<br/>
更不可能修改打卡记录了<br/>
那这么说我做的项目然并卵？<br/>

目前已经实现通过Fidder4抓取 到打卡记录.并用MUI成功显示出来。<br/>
理论上是可以实现抓取打卡记录  并存入自由数据库的。<br/>

忘深的想确实有点黑。。。<br/>
走走看看吧,毕竟这不是一个小项目。<br/>


2018/05/22 我们的自动打卡逻辑已经完成  且可在后台自动检索指定WIFI 测距 准确率高达100% <br/>
接下来我们将进一步完善 PHP后台逻辑  并加入人员管理 打卡记录两张表 <br/>
<br/>
 /**<br/>
     * 先分析一下 自动打卡的条件<br/>
     * 1.WIFI连上后<br/>
     * 2.WIFI距离路由器距离到达临界值<br/>
     * 3.GPS定位到当前位置<br/>
     * 4.手动打开自定打卡APP（必须条件 长期运行可加入白名单）<br/>
     * 5.后台长期运行的服务 能够通过WIFI打开的广播 扫描WIFI环境并选择连接上指定WIFI.(目前只通过SSID判断即可)<br/>
     * 6.根据前面的条件 寻找最佳时机  请求PHP服务器 打卡<br/>
     *<br/>
     *<br/>
     * 思考：手机打开WIFI开关后,是否存在连接上指定WIFI立即发送广播的可能,自己实现是否可行?<br/>
     * 1 自己实现的方法：当距离当前路由器小于100米的时候 开启后台长期运行的服务可以轮训 遍历当前WIFI列表 直到连接上指定WIFI 查寻当前连接WIFI距离条件打卡<br/>
     *  问题1  如何确定路由器的唯一性(MAC 去掉MAC的后四位即可) '必须'<br/>
     *  问题2  如何确定WIFI的唯一性(SSID?)  '非必须'因为一个路由器能发出几个WIFI<br/>
     * */<br/>
	 
参考文档<br/>
https://blog.csdn.net/earthyuguoguo/article/details/53220850<br/>

参考大神<br/>
https://github.com/ThanosFisherman/WifiUtils<br/>

参考获取路由器MAC地址<br/>
https://blog.csdn.net/crazyman2010/article/details/50464256<br/>

 完成查询打卡记录（分页 条件查询  联查）<br/>
 #注册会员 <br/>
 #修改用户头像 意见反馈  点赞 <br/>
 加入用户的WIFI密码管理  <br/>
 WIFI传配置文件WIFIINFO.JSON <br/>
 忘记密码<br/>
 验证码部分<br/>
 
 
 接口文档<br/>
 https://documenter.getpostman.com/view/4338352/wificlock/RW8FDkpc<br/>