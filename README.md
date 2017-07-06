# NetsScoreView
NetsScoreView一个网状评分自定义控件，支持多种自定义属性
# 先看看效果吧
![img](https://github.com/HanJinLiang/NetsScoreView/blob/master/NetsScoreView.gif)
# 集成步骤
## 在根项目gradle添加
``` java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
## 在项目gradle添加
``` java
  dependencies {
	        compile 'com.github.HanJinLiang:NetsScoreView:V1.0.0'
	}
```
# 使用说明
在XML布局直接使用
```xml
<com.hanjinliang.netsscore.NetsScoreView
        android:id="@+id/NetsScoreView"
        android:layout_width="300dp"
        android:layout_height="300dp"
        netsscore:NetsColor="#afafaf"
        netsscore:ScoreFillColor="#60FF4081"
        netsscore:ScoreLineColor="#FF4081"
        netsscore:ScoreTxtColor="#2b2b2b"
        netsscore:MaxScore="100"
        netsscore:EdgeCount="6"
        netsscore:TxtSize="14sp" />
```
也可以直接new NetsScoreView() 使用
### 设置数据源
``` java
     /**
     * 设置数据源
     * @param edgeCount 多边形变数
     * @param scores  数据源
     */
    public void setData(int edgeCount,float[] scores){}
```
### 支持自定义分数Format
``` 
    mNetsScoreView.setTxtFormat(new NetsScoreView.TxtFormat() {
            @Override
            public String originalDataFormat(int index, float value) {
                return value+"分数";//格式化显示数值
            }
        });
```
## 自定义属性说明
| 属性名称       | 解释         |
| ------------- |:-------------:|
| NetsColor     | 网格线的颜色 | 
| ScoreLineColor| 分数覆盖物边缘线颜色|
| ScoreFillColor | 分数覆盖物填充颜色     |
| EdgeCount | 多边形边数     |
| MaxScore | 分数最大值    |
| ScoreTxtColor | 分数字体颜色|
| TxtSize | 分数字体大小     |

## 其他
参考自[SpiderWebScoreView](https://github.com/xiaopansky/SpiderWebScoreView)，纯粹当做自己练习
