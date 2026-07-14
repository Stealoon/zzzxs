package com.studyroom.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeatPageQueryDTO implements Serializable {

    //当前页码
    private int page;

    //每页显示记录数
    private int pageSize;

    //座位编号（模糊查询）
    private String seatCode;

    //所属区域ID筛选
    private Long areaId;

    //状态筛选
    private Integer status;

}
