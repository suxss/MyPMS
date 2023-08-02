package com.example.mypms.mapper;

import com.example.mypms.model.Purchaser;
import com.example.mypms.model.Role;
import com.example.mypms.model.User;
import com.example.mypms.model.Vendor;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Mapper
@Repository
public interface UserMapper {
    ArrayList<User> queryAllUser();

    User queryUserByEmail(String email);

    Role getRolesByUid(String uid);

    int insertPurchaser(Purchaser purchaser);

    int insertVendor(Vendor vendor);

    String getEmailByUid(String uid);
}
