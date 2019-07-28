package com.changwu.AJSpider.text;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: Changwu
 * @Date: 2019/7/25 22:45
 */
@AllArgsConstructor
@Data
public class News {
    public News(){

    }
    String title;
    String date;
    String body;
}
