package com.equipment.management.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.equipment.management.inspection.entity.InspectionPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 点检计划Mapper
 */
@Mapper
public interface InspectionPlanMapper extends BaseMapper<InspectionPlan> {
}
