package com.example.dianping.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Adward_Z
 * @date 2020/7/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonErr {

    private Integer code;

    private String message;
}
