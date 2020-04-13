package com.carry.customerflow.mapper;

import com.carry.customerflow.bean.Personal_Information;
import com.carry.customerflow.bean.User;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

public interface Personal_InformationMapper {
    void initializePersonal_Information(@Param("personal_information")Personal_Information personal_information);
    Personal_Information searchPersonal_InformationByUsername(@Param("username")String username);
    List<Personal_Information> searchPersonal_InformationList(@Param("param")String param);
    User checkUserPassword(@Param("username")String username,@Param("password")String password);
    void changeUserPassword(@Param("username")String username,@Param("newPassword")String newPassword);
    void editPersonal_Information(@Param("personal_information")Personal_Information personal_information,@Param("username")String username);
    List<Personal_Information> searchPersonal_InformationByUsernameOrNameExcludePassword(@Param("status")String status,@Param("param")String param);
    void deletePersonal_InformationByUsername(@Param("username")String username);
}
