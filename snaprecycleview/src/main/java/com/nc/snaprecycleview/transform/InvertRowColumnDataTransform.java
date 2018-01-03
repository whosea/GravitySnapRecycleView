package com.nc.snaprecycleview.transform;

import java.util.ArrayList;
import java.util.List;


/**
 * @author ht
 * @date Created on 2017/10/12
 * @description 数据转换类，类似倒置矩阵
 * 把最后一页的空数据也补上
 */
public class InvertRowColumnDataTransform<T> {

    private static final int DEFAULT_ROW = 1;
    private static final int DEFAULT_COLUMN = 1;

    private int row = DEFAULT_ROW;
    private int column = DEFAULT_COLUMN;

    public InvertRowColumnDataTransform(int column, int row) {
        if (row <= 0 || column <= 0)
            throw new IllegalArgumentException("row or column must be not null");

        this.row = row;
        this.column = column;
    }

    public List<T> transform(List<T> dataList) {
        List<T> destList = new ArrayList<T>();

        //页数
        int pageSize = row * column;
        //总数量
        int size = dataList.size();
        //总换后的总数量，包括一页空的数据
        int afterTransformSize;
        if (size < pageSize) {
            afterTransformSize = pageSize;
        } else if (size % pageSize == 0) {
            afterTransformSize = size;
        } else {
            afterTransformSize = (size / pageSize + 1) * pageSize;
        }

        //开始遍历位置，类似置换矩阵
        for (int i = 0; i < afterTransformSize; i++) {
            //第几页
            int pageIndex = i / pageSize;
            //为横坐标
            int columnIndex = (i - pageSize * pageIndex) / row;
            //为纵坐标
            int rowIndex = (i - pageSize * pageIndex) % row;
            //
            int result = (rowIndex * column + columnIndex) + pageIndex * pageSize;

            if (result >= 0 && result < size) {
                destList.add(dataList.get(result));
            } else {
                destList.add(null);
            }
        }
        return destList;
    }

}
