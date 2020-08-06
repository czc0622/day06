package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Modle;
import com.xiaoshu.entity.ModleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ModleMapper extends BaseMapper<Modle> {
    long countByExample(ModleExample example);

    int deleteByExample(ModleExample example);

    List<Modle> selectByExample(ModleExample example);

    int updateByExampleSelective(@Param("record") Modle record, @Param("example") ModleExample example);

    int updateByExample(@Param("record") Modle record, @Param("example") ModleExample example);
}