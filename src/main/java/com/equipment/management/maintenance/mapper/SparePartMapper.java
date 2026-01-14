package com.equipment.management.maintenance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.equipment.management.maintenance.entity.SparePart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SparePartMapper extends BaseMapper<SparePart> {

    @Select("SELECT * FROM spare_part WHERE stock_qty < safe_qty AND deleted = 0")
    List<SparePart> selectLowStockParts();

    @Update("UPDATE spare_part SET stock_qty = stock_qty - #{quantity} WHERE id = #{id} AND stock_qty >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("UPDATE spare_part SET stock_qty = stock_qty + #{quantity} WHERE id = #{id}")
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Select("SELECT * FROM spare_part WHERE code = #{code} AND deleted = 0")
    SparePart selectByCode(@Param("code") String code);
}
