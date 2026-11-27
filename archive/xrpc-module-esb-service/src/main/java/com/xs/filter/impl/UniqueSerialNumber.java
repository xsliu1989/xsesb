package com.xs.filter.impl;

import com.xs.dto.Dto;
import com.xs.exception.DefineException;
import com.xs.filter.Filter;
import com.xs.serve.StructureUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 相当于全局流水号 ， 由渠道传送，不能重复
 *
 */
@Component
@Order(1)
public class UniqueSerialNumber  implements Filter {
    @Override
    public void check(Dto dto) throws DefineException {

            // 检查流水号
            String x = StructureUtil.getNonce(dto.getNonce());
            if (x == null) {
                StructureUtil.setNonce(dto.getNonce());
            } else {
                throw new DefineException("863", "唯一流水号重复");
            }






    }
}
