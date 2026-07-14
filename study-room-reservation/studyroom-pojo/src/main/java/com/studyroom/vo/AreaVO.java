package com.studyroom.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 区域VO（管理端返回）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键ID
    private Long id;

    //区域名称
    private String name;

    //排序
    private Integer sort;

    //状态
    private Integer status;

    //区域描述
    private String description;

    //区域座位总数
    private Integer seatCount;

    //创建时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;

}
