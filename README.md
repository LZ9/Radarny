# Radarny雷达图
Radarny是一个简单的雷达多维图表控件，支持动画渲染。 我在[TacticalTwerking](https://github.com/TacticalTwerking)的[AbilityChartView](https://github.com/TacticalTwerking/AbilityChartView)基础上做了一些优化和扩展：
1. 增加了更多的属性和参数配置，并支持用代码进行设置 
2. 支持圆形边框或多边形边框
3. 数值渲染支持线条和平面
4. 图表任意宽高下均自动居中

## 目录
- [1、引用方式](https://github.com/LZ9/Radarny#1引用方式)
- [2、效果图](https://github.com/LZ9/Radarny#2效果图)
- [3、使用教程](https://github.com/LZ9/Radarny#3使用教程)
- [扩展](https://github.com/LZ9/Radarny#扩展)

## 1、引用方式
由于jcenter删库跑路，请大家添加mavenCentral依赖
```
repositories {
    ...
    mavenCentral()
    ...
}
```
在你需要调用的module里的dependencies中加入以下依赖
```
implementation 'ink.lodz:radarny:1.0.4'
```

## 2、效果图
<div align="center">
    <img src="https://github.com/LZ9/Radarny/blob/master/img/SVID_20221108_151519_1.gif?raw=true" height="600"/>
</div>

## 3、使用教程
Radarny的代码设置方法如下：
```
    radarnyView
        .setMaxValue(100f)//设置数值最大上限
        .setAnimDuration(400)//设置动画延时，设置0则关闭动画效果，默认关闭
        .setFrameColor(color)//设置外边框颜色
        .setFrameRound(true)//设置外边框是否为圆形，否为多边形，默认圆形
        .setFrameStrokeWidth(5)//设置外边框线宽度
        .setInnerFrameColor(color)//设置内圈边框颜色
        .setInnerFrameStrokeWidth(3)//设置内圈边框线宽度
        .setInnerFramePercentage(0.3f)//设置内圈边框占比
        .setInnerLineColor(color)//设置内部线颜色
        .setInnerLineStrokeWidth(3)//设置内部线宽度
        .setShowLine(true)//设置是否显示内部线
        .setTextColor(color)//设置文字颜色
        .setTextPercentage(1.2f)//设置文字离边框占比
        .setTextSize(sp2px(11))//设置文字大小
        .setValueColor(color)//设置数值颜色
        .setValueStrokeWidth(5)//设置数值线宽度
        .setValuePaintStyle(Paint.Style.FILL)//设置数值绘画风格
        .setShowSrc(true)//设置是否显示中心图标，默认不显示
        .setSrcResId(R.drawable.xxx)//获取图片资源id
        .setSrcWidth(dp2px(25))//设置图片宽度
        .setSrcHeight(dp2px(25))//设置图片高度
        .setSrcBgColor(Color.WHITE)//设置图片背景颜色
        .setSrcBgPercentage(0.7f)//设置图片背景占比
        .setData(list)//设置数据
        .build()//完成构建并渲染
```
对应的XML属性配置如下：
```
    <com.lodz.android.radarny.RadarnyView
        android:id="@+id/radarny_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:animDuration="500"
        app:frameColor="@color/xxx"
        app:frameWidth="5"
        app:innerFrameColor="@color/xxx"
        app:innerFramePercentage="0.3"
        app:innerFrameWidth="3"
        app:isRound="true"
        app:isShowLine="true"
        app:isShowSrc="true"
        app:lineColor="@color/xxx"
        app:lineWidth="3"
        app:maxValue="100"
        app:src="@mipmap/xxx"
        app:srcBgColor="@color/xxx"
        app:srcBgPercentage="0.7"
        app:srcHeight="25dp"
        app:srcWidth="25dp"
        app:textColor="@color/xxx"
        app:textPercentage="1.2"
        app:textSize="11sp"
        app:valueColor="@color/xxx"
        app:valuePaintStyle="fill"
        app:valueWidth="5" />
```
如果你已经都用xml配置完毕，只需一句代码便可进行渲染：
```
    radarnyView.setData(list).build()
```
- 数据必须通过setData()来设置
- 数据最大值可通过setMaxValue()来设置
- 渲染时可调用build()方法
- 控件宽高会影响内部表格的展示，可通过参数配置将表格样式调整到最适宜的状态

## 扩展

- [更新记录](https://github.com/LZ9/Radarny/blob/master/radarny/readme_update.md)
- [回到顶部](https://github.com/LZ9/Radarny#radarny雷达图)

## License
- [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

Copyright 2022 Lodz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
