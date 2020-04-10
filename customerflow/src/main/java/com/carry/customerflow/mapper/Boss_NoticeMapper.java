package com.carry.customerflow.mapper;

import com.carry.customerflow.bean.Boss_Notice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface Boss_NoticeMapper {
    void editBoss_Notice(@Param("boss_notice")Boss_Notice boss_notice);
    List<Boss_Notice> getBoss_Notice(@Param("username")String username);
    void changeBoss_NoticeStatus(@Param("username")String username,@Param("status")Integer status,@Param("id")Integer id);
    void deleteBoss_Notice(@Param("username")String username,@Param("id")Integer id);
    void deleteBoss_NoticeByUsername(@Param("username")String username);
}
