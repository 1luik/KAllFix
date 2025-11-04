因为我的世界是一个单线程游戏所以模组的代码也是单线程的所以无法保证彻底修复所有bug就无法关闭对应的issues
## 使用源码

moonrise_fast_palette功能使用了[moonrise](https://github.com/Tuinity/Moonrise)的源码

修复gcyr和canary的兼容问题功能功能使用了[canary](https://github.com/AbdElAziz333/Canary)的源码

##  状态
目前是直接提交到[gitee](https://gitee.com/n1luik/k_multi_threadingK_multi_threading)在每天的会自动更新一次[github](https://github.com/1luik/KAllFix)

Code is committed to [Gitee](https://gitee.com/n1luik/k_multi_threadingK_multi_threading) directly. [Github](https://github.com/1luik/KAllFix) will update each day automatically (UTC 00:00)

这个项目包含了2个mod     k_all_fix， K_multi_threading

理论上1.17+ java11+都可以用但是需要改源码把没有的部分删除

可以用-DKMT_D=[任意字符]和-DKAllFix_D=[任意字符]禁用独立mod

idea的MinecraftDev插件有bug必须jar一下才可以正常查看源码

debug的唯一作用就是连接多线程而不是替代专用工具，火花的基础单位是毫秒自带的工具是纳秒不到1毫秒的会强制转成1毫秒，这玩意把他当个装饰就可以，保存的json没有任何东西可以利用因为这个功能浪费我太多时间了

停止开发debug太消耗时间了直到我需要这个功能了

想在客户端需要使用-DKMT_Client=true jvm参数，需要再1.0.4.0一后才可以

##  使用方法

1: 把gradle.properties里的代理删了 
    
    .\gradlew.bat jar
 
或者

    ./gradlew jar

然后把build/libs的文件复制到游戏目录

1.0.3.18之前

    java -jar k_multi_threading-xxx.jar -i [空或安装目录]

1.0.3.18开始可以直接运行但是保留安装功能

按照提示操作或者去n1luik.K_multi_threading.core.util.Util看源码

会生成k_multi_threading-base.jar和k_multi_threading-asm.jar两个文件

有一些功能需要启动一次游戏收集数据在下一次启动是使用，当然这一次启动是无法正常工作的

有一些功能会下载一些库，如果下载失败可以手动放到./lib：
- Zstd-jni 1.5.7-2 下载地址：https://repo1.maven.org/maven2/com/github/luben/zstd-jni/1.5.7-2/
- 在启用调试模式的时候需要[火花]spark 任意版本（没有测试过低版本但是最好不要低于1.10.53要不然可能不能生成保存成火花的格式）放入mods
  
##  功能
- 优化[悠然一派]Atmospheric的检测愚人节每一次都要计算Calendar的性能问题
- 优化[Upgraded Additional Trims]upgradedadditionaltrims的性能问题
- 优化[Upgraded Core]upgradedcore的性能问题
- 修复[灾变]L_Ender 's Cataclysm在拥有数千实体的情况下出现性能问题
- 修复farm_and_charm 沉浸农艺[Let's Do] Farm & Charm 的大量发送无效数据包问题
- 优化farm_and_charm 沉浸农艺[Let's Do] Farm & Charm 鸡窝的ai性能问题
- 修复gcyr和canary的兼容问题，但是可能gcyr空间站边界会有碰撞箱问题
- 修复[Petrolpark 的库]petrolpark 1.4.2服务器不可以在部分java使用问题
  - 可以使用-DKAF-DisablePetrolpark=false关闭
- 修复Biolith兼容问题(范围包括重新生成MultiNoiseBiomeSource的mod，例如所有的使用MCreator的所有有这个操作的mod)
- 并不是修复了所有的使用MCreator的模组，小部使用分模组需要定制修复代码（例如）Terramity
- (MCreator所有模组, terramity（尝试解决这一类问题）)
- 添加可独立关闭的多线程
- 解决alexscaves磁场依托答辩问题
- 解决createdieselgenerators不会用导致他的mixin性能有很大问题问题
- (不安全)（需要开启-DKAF-NbtIoMixin_NotGZip=true)加一个try catch解决nbtio的gzip问题
- 解决老destroy的数据库连不上就崩服问题
- -DKMT-threadMax=[线程池的线程数] | 1.0.3
- -DKMT-callMax=[多线程任务默认分配最大使用线程] | 1.0.3
- -DKMT-ThreadpoolKeepAliveTime=[毫秒]多线程线程池停止不需要的线程的时间 | 1.0.3
- 使用-DKAF-RemoveMixin:[类名]禁用指定的mixin，KMT也可以
- -DKMT-ChunkGeneratorMode2Start=[true, false]相当于自动在开始的执行 /SetterWorldConfig world setM2 %%KMT-ChunkGeneratorMode2Start%%
- -DKMT-ChunkGeneratorMode2MultiThreadingSize=[数量]相当于自动在开始的执行 /SetterWorldConfig world setM2 %%KMT-ChunkGeneratorMode2MultiThreadingSize%%
- -DKAF-ChunkBreedingControlSize=[多少实体] 在区块实体大于指定数量不允许繁殖，无jvm参数或jvm参数非数组或空字符直接禁用，-DKAF-ChunkBreedingControlSizeEnable=[true, false]是内部检测结果缓存会被覆盖，按照我的世Huntersxy玩家的要求添加
- -DKAF-ClientboundKeepAlivePacket_Max=[多少毫秒] 修改ClientboundKeepAlivePacket数据包的时间要求，默认15秒
  - ClientboundKeepAlivePacket包是需要小于30秒发送一次要不然就会被踢出服务器理由是连接超时
- 通过-DKAF-ServerTimeout=[多少秒]设置服务器连接超时时间，不一定有用可以试试另一个方式
  - forge自带的另一个建议2个都设置: -Dforge.readTimeout=[多少秒]
  - 指令:
    - #kmt_debug
      - 需要开启KMT-Debug
      - start [可选的interval]
        - 开始纪录
        - interval的单位是毫秒默认是1.5，这个参数跟火花的这个参数功能一样但是精度更高
      - stop [文件名，保存在服务器的./位置不可以有同名的文件夹和文件]
        - 停止并保存，火花格式必须安装火花
        - spark [true,false-是否保存所有线程不保存的话只有链接后的异步和多线程]
          - 仅保存成spark的格式
        - json
          - 直接把数据保存成json，没有link
        - all [true,false-spark格式是否保存所有线程不保存的话只有链接后的异步和多线程]
          - 保存2种格式
    - debug_GetterClassFile 类名
      - 获取游戏最终游戏运行的类型文件数据，会保存在游戏目录下面“保存时间的时间戳_save.class”
    -  SetterWorldConfig [world, ClearErrorSize, RemoveRemoveErrorSize]
      - 设置之后并不会保存
        - world [维度的注册id]:
          - setM2 [true, false] 设置维度的实现过程方式是另一种，但是可能会卡死
          - setMultiThreading [数量] 默认0，设置多线程同时运行任务的数量，但是可能会卡死
        - ClearErrorSize 直接清除报错导致服务器崩溃的次数
        - RemoveRemoveErrorSize 让服务器不会纪录崩溃的次数，无限拦截

##  可开启
- -DKMT-Debug=true 启用多线程同步
- -DKMT-SafeUnloadChunk=true 启用在区块生成线程运行区块卸载
- -DIndependencePlayer=true 开启玩家异步，这玩意大概率是负优化
- -DKAF-ServerGamePacketListenerImplMixin2=true 移除服务器移动距离的安全检查，这个可能跟一个模组不兼容会让玩家进不去服务器
- -DKAF-gtceu.MedicalConditionTrackerMixin=true 禁止添加gtm的辐射
- -DKAF-RemoveClientboundKeepAlivePacket=true 禁用ClientboundKeepAlivePacket功能
- -DKAF-Fix_fabric-object-builder-api.jar=true 修复信雅互联的fabric-object-builder-api不兼容47.3.27的问题，我因为这个问题让这个mod晚发了半个月他们还没有解决
- -DKAF-FixTFMGDestroy=true修复[坍毁化合]destroy和[机械动力：工业长路]tfmg的兼容问题，需如果在没有安装2个的情况下会直接出现[坍毁化合]destroy的Creative Pump无法正常连接机械动力的管道
- -DKAF-FixConfigAuto=true自动修改配置文件为正确的选项
- -DKAF-RemoveFlyingTest=true移除飞行检查
- -DKAF-fix.asynchronous.ClientboundCustomQueryPacket=true握手异步，如果加的mod太多可能会导致握手的时候堵包导致不能玩服务器
- -DKMT-LoginMultiThreading=true登陆多线程，防止模组过多在[net.minecraftforge.event.OnDatapackSyncEvent]的过程中出现bug
  - 多线程登录只能在原版的登录数据包才可以多线程其他模型需要适配目前只适配了[原版]
  - -DKMT-LoginMultiThreading.ConnectionLock=true在tick结束的时候等待异步执行完成，关闭可以起到一定的优化效果但是会导致登录速度变慢
    - 必须开启
  - -DKMT-LoginMultiThreading.TaskSizeMax=[数字]设置最多运行多少登陆任务让其他的等待，默认8
- -DKAF-LoginProtectionMod=true添加登陆保护功能，但是需要服务器和客户端都启用
- -DKAF-FixAllPacket=true修复数据包[网络]，但是需要服务器和客户端都启用
  - 目前修复的mod ：
  - [通用机械：炸药]MekanismExplosives
  - Create Sabers
  - [深渊：第二章]TATOS
- -DKMT-threadpool-async=true会崩
- -DKAF-moonrise_fast_palette=true 移植的moonrise的fast_palette功能
- 多线程jei和emi：
  - 操作
    - -DKAF-MultiThreadingJEI=true只能在客户端使用，多线程jei不兼容emi
    - -DKAF-MultiThreadingEMI=true只能在客户端使用，多线程jei必须有emi
    - -DKAF-MultiThreadingJEICommon=true只能在客户端使用，基础修改，必须启用
  - 自动获取的cpu最大线程数不一定准确，大概率是作者是虚拟机的问题需要手动设置-KAF-JeiMultiThreading-TasxMax=[cpu最大线程数win多百分之15 linux多百分之10 cpu线程越多设置越多]
- -DKAF-UnsafeCinderscapesFix1=true修改[余烬奇景]cinderscapes的enableAshFall性能消耗函数限制在地狱
- -DKAF-TooltipMultiThreading=true吧Tooltip事件改成多线程
  - 这个非常不建议，这个有很多大问题
    - 这个不应该被使用
- -DKAF-packetOptimize=true优化一部分原版数据包，需要客户端服务器一起开启
  - -DKAF-packetOptimize.AttributesReOutputTime=[毫秒]设置强制重新发送的间隔时间，默认2分钟
  - -DKAF-packetOptimize.CompatibilityMode.ClientboundBlockEntityDataPacket=true更保守的ClientboundBlockEntityDataPacket压缩，但是会影响性能
  - -DKAF-packetOptimize.CompatibilityMode.ClientboundSectionBlocksUpdatePacket=true更保守的ClientboundSectionBlocksUpdatePacket和ClientboundBlockUpdatePacket压缩，但是会影响性能
- -DKAF-PlainTextSearchTreeMultiThreading=true创造模式物品栏多线程，可以给emi启动加速
  - 自动获取的cpu最大线程数不一定准确，大概率是作者是虚拟机的问题需要手动设置-DKAF-PlainTextSearchTreeMultiThreading-TasxMax=[cpu最大线程数win多百分之15 linux多百分之10 cpu线程越多设置越多]
- -DKAF-InjectDatapack=true会在游戏目录检测InjectDatapack文件夹并在加载完在这个文件夹打包的数据包数据包在加载完所有数据包之后加载，格式跟正常数据包不一样
  - 不支持成就
  - 无法通过任何方式在data数据里找到这些json
  - 格式：
  -  ./InjectDatapack/*.zip：
  - config.json:
    - ``` json
       {
         "data": [
           {
              "type": "注册表名",
              "name": "注册名",
              "file": "文件名"
           }
         ],
         "tag": [
           {
              "type": "注册表名",
              "tag": "标签名",
              "file": "文件名"
           }
         ],
      }
  - 数据文件


## 问题
- 里面的灾变mod大于了121mb了但是gitee单个文件只能100mb所以材质包
- 某些模组可能需要再客户端安装KAllFix（即使这个mod什么都没做）
- 如果不手动设置-DKMT-threadMax=[cpu线程数]的话可能会导致地形生成莫名其妙卡死，还是的话继续调高这个
- cupboard模组的logOffthreadEntityAdd功能可能不兼容，我不想直接让他直接消失而且被人发现，可以通过-DKAF-FixConfigAuto=true自动禁用
- canary的world.tick_scheduler功能会覆盖ClassInstanceMultiMap的多线程修复只能关闭，可以通过-DKAF-FixConfigAuto=true自动禁用
  - 在游戏目录/config/canary.properties新开一行写上写上[mixin.world.tick_scheduler=false]
  - 自动移除的方式是吧mixin.world.tick_scheduler=false是直接写config/canary.properties里面
- canary的entity_by_type功能会覆盖ClassInstanceMultiMap的多线程修复只能关闭，可以通过-DKAF-FixConfigAuto=true自动禁用
  - 在游戏目录/config/canary.properties新开一行写上写上[mixin.collections.entity_by_type=false]
  - 自动移除的方式是吧mixin.collections.entity_by_type=false是直接写config/canary.properties里面
- lithium的world.tick_scheduler功能会覆盖ClassInstanceMultiMap的多线程修复只能关闭，可以通过-DKAF-FixConfigAuto=true自动禁用
  - 在游戏目录/config/lithium.properties新开一行写上写上[mixin.world.tick_scheduler=false]
  - 自动移除的方式是吧mixin.world.tick_scheduler=false是直接写config/lithium.properties面
- lithium的entity_by_type功能会覆盖ClassInstanceMultiMap的多线程修复只能关闭，可以通过-DKAF-FixConfigAuto=true自动禁用
  - 在游戏目录/config/lithium.properties新开一行写上写上[mixin.collections.entity_by_type=false]
  - 自动移除的方式是吧mixin.collections.entity_by_type=false是直接写config/lithium.properties里面
- 多线程会出现线程池拦截报错不会崩溃的情况，但是有一个拦截次数超过这个次数会报错，这个次数是可以设置的
- 跟现代化修复(modernfix)的mixin.perf.cache_upgraded_structures可能冲突的，建议先试一试会不会出问题在关闭
  - 关闭方法：在config/modernfix-mixins.properties文件新增一行插入的mixin.perf.cache_upgraded_structures=false
- 登陆多线程可能会导致服务器提前接受到ServerboundMovePlayerPacket导致报错一次
- [跑酷！]ParCool在开启多线程登陆的时候需要进服务器之后死一下才可以使用
- 盖亚魔典4 会有小概率报错Modifier is already applied on this attribute!
- 1.20.1的[余烬奇景]cinderscapes我根本做不到优化enableAshFall功能只能禁用，做不到的是找到在那里生成的这个生物群系在生成这个的维度启用
  - 浪费性能：中高
  - 浪费原因：在所有时间的每一个区块获取128次获取区块
  - 解决方法：添加一个不安全优化让他只能在在地狱触发
- 多线程jei问题：
  - 因为emi的问题fusion的兼容根本不可能实现，但是目前我玩的时候没有影响
- NuclearCraft: Neoteric的裂变反应堆重启有小概率会导致冷却计算错误
- 1.0.3.3之后n1luik.K_multi_threading.core.base.ParaServerChunkProvider和n1luik.KAllFix.util.TaskRun还有一部分区块生成的代码多线程必须c2要不然性能很差（java会自动c2但是需要运行一会）
- KAF-PlainTextSearchTreeMultiThreading没有使用JProfiler测试明确可以生效
- 我输入/polymorph conflicts之后无没有任何输出无法测试InjectDatapack功能
- 无法使用[火花]spark他无法显示几百上千个线程，但是可以作为补充获取自带调试无法获取的线程，自带的只能对多线程的一部分生效
- 多线程从1.0.3.12版本在我的Windows Server 2022 Datacenter 21H2 20348.3091|pve8.2.2修改版kvm e5-2690v4*2虚拟host cpu出现小概率了系统产生的线程调度bug会导致系统管理线程异常cpu算力消耗是正常情况的900%
  - 解决方法：重启游戏服务器

## 故障纪录
- 没有地狱，问题是因为测试模组导致数据包损坏，解决方法：
  - 吧老备份的或者一样种子一样一样规则的level.dat替换
- 问题：
  - 不一定数据完全可能会出现数据偏差
- 文档之前有的忘加-D了

