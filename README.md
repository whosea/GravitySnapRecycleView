#GravitySnapRecycleView
Android Recycleview 翻页效果（类似ViewPager）

原文链接：https://www.zybuluo.com/whosea/note/1006457

#效果
项目应用效果：

![](https://raw.githubusercontent.com/whosea/GravitySnapRecycleView/master/doc/freegift.gif)

![](https://raw.githubusercontent.com/whosea/GravitySnapRecycleView/master/doc/product.gif)

demo效果：

![](https://raw.githubusercontent.com/whosea/GravitySnapRecycleView/master/doc/snap1.gif)

![](https://raw.githubusercontent.com/whosea/GravitySnapRecycleView/master/doc/snap2.gif)

![](https://raw.githubusercontent.com/whosea/GravitySnapRecycleView/master/doc/snap3.gif)

#用法
创建处理翻页效果类（另外总共支持start end top bottom 和 center 五种行为）：
```
GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.CENTER);
snapHelper.setColumn(3);//如果一页里面有超过1列的都需要设置
snapHelper.attachToRecyclerView(recyclerview);
```
翻页监听
```
PageIndicatorHelper pageIndicatorHelper = new PageIndicatorHelper();
pageIndicatorHelper.setPageColumn(column);
pageIndicatorHelper.setRecyclerView(rvCenter);
pageIndicatorHelper.setOnPageChangeListener(new GravityPageChangeListener() {
    @Override
    public void onPageSelected(int position,int currentPage,int totalPage) {
		Log.e("MainActivity",currentPage+ "/"+totalPage);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
});
```