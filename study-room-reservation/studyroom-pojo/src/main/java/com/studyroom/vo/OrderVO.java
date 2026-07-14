package com.studyroom.vo;

import com.studyroom.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO extends Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    //座位编号
    private String seatCode;

    //区域名称
    private String areaName;

    //用户姓名
    private String userName;

}
