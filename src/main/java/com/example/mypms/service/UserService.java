package com.example.mypms.service;

import com.example.mypms.mapper.UserMapper;
import com.example.mypms.model.Purchaser;
import com.example.mypms.model.User;
import com.example.mypms.model.Vendor;
import com.example.mypms.plugins.Encrypt;
import com.example.mypms.plugins.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    public ArrayList<User> queryAllUser() {
        return userMapper.queryAllUser();
    }

    public User queryUserByEmail(String email) {
        return userMapper.queryUserByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userMapper.queryUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        user.setRole(userMapper.getRolesByUid(user.getUid()));
        return user;
    }

    public int insertPurchaser(String uname, String email, String pwd) {
        if (userMapper.queryUserByEmail(email) != null) {
            return 0;
        }
        Purchaser purchaser = new Purchaser();
        String uid = "p_" + Random.randomString(10);
        purchaser.setUid(uid);
        purchaser.setUname(uname);
        purchaser.setEmail(email);
        purchaser.setPwd(Encrypt.md5(pwd));
        return userMapper.insertPurchaser(purchaser);
    }

    public int insertVendor(String uname, String email, String pwd, String phone, String address) {
        if (userMapper.queryUserByEmail(email) != null) {
            return 0;
        }
        Vendor vendor = new Vendor();
        String uid = "v_" + Random.randomString(10);
        vendor.setUid(uid);
        vendor.setUname(uname);
        vendor.setEmail(email);
        vendor.setPwd(Encrypt.md5(pwd));
        vendor.setPhone(phone);
        vendor.setAddress(address);
        return userMapper.insertVendor(vendor);
    }

    public String getEmailByUid(String uid) {
        return userMapper.getEmailByUid(uid);
    }
}
